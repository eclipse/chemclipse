/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.internal.identifier;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.runtime.IExtendedRuntimeSupport;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.runtime.RuntimeSupportFactory;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.IOnsiteSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.support.PeakProcessorSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.IProgressMonitor;

public class AmdisIdentifier {

	private static final Logger logger = Logger.getLogger(AmdisIdentifier.class);
	/*
	 * AMDIS is able to import CDF files.
	 */
	private static final String CONVERTER_ID = "net.openchrom.msd.converter.supplier.cdf";

	public void calulateAndSetDeconvolutedPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		/*
		 * Settings
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		IOnsiteSettings onsiteSettings = peakDetectorSettings.getOnsiteSettings();
		/*
		 * amdisTmpPath, e.g.:
		 * E.g.: /home/openchrom/.wine/drive_c/tmp
		 */
		String amdisTmpPath = PreferenceSupplier.getAmdisTmpPath();
		File file = new File(amdisTmpPath + File.separator + chromatogram.getName());
		IProcessingInfo processingInfo = ChromatogramConverterMSD.getInstance().convert(file, chromatogram, CONVERTER_ID, monitor);
		/*
		 * There are no messages if the chromatogram has been converted correctly.
		 */
		if(!processingInfo.hasErrorMessages()) {
			IExtendedRuntimeSupport runtimeSupport = null;
			try {
				/*
				 * amdisApplication, e.g.:
				 * /home/openchrom/.wine/drive_c/Programme/NIST/AMDIS32-271/AMDIS32$.exe
				 */
				File fileChromatogram = processingInfo.getProcessingResult(File.class);
				/*
				 * If an elu file already exists, delete it.
				 */
				File eluFile = getEluFileName(fileChromatogram);
				if(eluFile.exists()) {
					eluFile.delete();
				}
				/*
				 * Prepare AMDIS.
				 */
				String amdisApplication = PreferenceSupplier.getAmdisApplication();
				String filePath = getAmdisCompatibleFilePath(fileChromatogram);
				runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(amdisApplication, filePath);
				runtimeSupport.getAmdisSupport().modifySettings(onsiteSettings);
				runtimeSupport.executeRunCommand();
				/*
				 * Wait until the ELU file is written.
				 * Why is the file name sometimes upper case.
				 */
				parseELUFile(fileChromatogram, runtimeSupport, chromatogramSelection, peakDetectorSettings, monitor);
				//
			} catch(Exception e) {
				logger.warn(e);
			} finally {
				/*
				 * Kill AMDIS
				 */
				if(runtimeSupport != null) {
					try {
						runtimeSupport.executeKillCommand();
					} catch(IOException e) {
						logger.warn(e);
					}
				}
			}
		}
	}

	private void parseELUFile(File fileChromatogram, IExtendedRuntimeSupport runtimeSupport, IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		try {
			File eluFile = getEluFileName(fileChromatogram);
			Thread.sleep(runtimeSupport.getSleepMillisecondsBeforeExecuteRunCommand() + 1000);
			/*
			 * Check that the file exists.
			 */
			if(eluFile.exists()) {
				long fileSize = FileUtils.sizeOf(eluFile);
				boolean amdisWritesData = true;
				while(amdisWritesData) {
					/*
					 * Wait until AMDIS has written the peak data.
					 */
					Thread.sleep(1000);
					long fileSizeNew = FileUtils.sizeOf(eluFile);
					if(fileSize < fileSizeNew) {
						fileSize = fileSizeNew;
					} else {
						amdisWritesData = false;
					}
				}
				/*
				 * Import the peaks.
				 */
				PeakProcessorSupport peakProcessorSupport = new PeakProcessorSupport();
				peakProcessorSupport.extractEluFileAndSetPeaks(chromatogramSelection, eluFile, peakDetectorSettings, monitor);
			} else {
				logger.warn("The ELU file couldn't be parsed.");
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private File getEluFileName(File fileChromatogram) {

		return new File(fileChromatogram.getParent() + File.separator + fileChromatogram.getName().replace(".CDF", ".ELU").toUpperCase());
	}

	/**
	 * Returns the AMDIS compatible file path.
	 * 
	 * @param fileChromatogram
	 * @return String
	 */
	private String getAmdisCompatibleFilePath(File fileChromatogram) {

		String filePath = "";
		String amdisTmpPath = PreferenceSupplier.getAmdisTmpPath();
		if(OperatingSystemUtils.isWindows()) {
			/*
			 * e.g.:
			 * C:\tmp\Chromatogram1.CDF
			 */
			filePath = amdisTmpPath + File.separator + fileChromatogram.getName();
		} else {
			/*
			 * Wine
			 */
			String wineTmpPath = amdisTmpPath + File.separator + fileChromatogram.getName();
			if(wineTmpPath.contains(".wine/dosdevices/")) {
				/*
				 * /home/openchrom/.wine/dosdevices/c:/tmp/Chromatogram1.CDF
				 */
				String[] vals = wineTmpPath.split("/dosdevices/");
				if(vals.length == 2) {
					filePath = vals[1].replace("/", "\\");
				}
			} else if(wineTmpPath.contains(".wine/drive_")) {
				/*
				 * /home/openchrom/.wine/drive_c/tmp/Chromatogram1.CDF
				 */
				String[] vals = wineTmpPath.split("/drive_");
				if(vals.length == 2) {
					filePath = vals[1].replaceFirst("([a-zA-z])(/)(.*)", "$1:$2$3");
					filePath = filePath.replace("/", "\\");
				}
			} else {
				filePath = "C:\\tmp\\" + fileChromatogram.getName();
			}
		}
		//
		return filePath;
	}
}
