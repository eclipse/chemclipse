/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - fix bug 544729 AMDIS Idnetifier misses ELU File
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.internal.identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.runtime.IExtendedRuntimeSupport;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.runtime.RuntimeSupportFactory;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.IOnsiteSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.support.PeakProcessorSupport;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class AmdisIdentifier {

	public static final String IDENTIFIER = "AMDIS Identifier";
	/*
	 * AMDIS is able to import CDF files.
	 */
	private static final String CONVERTER_ID = "net.openchrom.msd.converter.supplier.cdf";

	@SuppressWarnings({"rawtypes", "unchecked"})
	public IProcessingResult<Void> calulateAndSetDeconvolutedPeaks(IChromatogramSelectionMSD chromatogramSelection, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) throws InterruptedException {

		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		DefaultProcessingResult<Void> result = new DefaultProcessingResult<>();
		/*
		 * Settings
		 */
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		IOnsiteSettings onsiteSettings = peakDetectorSettings.getOnsiteSettings();
		/*
		 * amdisTmpPath, e.g.:
		 * E.g.: /home/openchrom/.wine/drive_c/tmp
		 */
		String amdisTmpPath = PreferenceSupplier.getAmdisTmpPath();
		File file = new File(amdisTmpPath + File.separator + chromatogram.getName());
		IProcessingInfo<File> processingInfo = ChromatogramConverterMSD.getInstance().convert(file, chromatogram, CONVERTER_ID, subMonitor.split(10));
		if(processingInfo == null) {
			result.addErrorMessage(IDENTIFIER, "Conversion to CDF returned no result");
			return result;
		}
		for(IProcessingMessage message : processingInfo.getMessages()) {
			result.addMessage(message);
		}
		File fileChromatogram = processingInfo.getProcessingResult();
		if(fileChromatogram == null) {
			result.addErrorMessage(IDENTIFIER, "Conversion to CDF returned no file");
			return result;
		}
		if(result.hasErrorMessages()) {
			return result;
		}
		try {
			AMDISParser parser = new AMDISParser(fileChromatogram);
			IProcessingResult<IPeaks<?>> amdisPeaks = executeAMDIS(fileChromatogram, onsiteSettings, parser, subMonitor.split(80));
			result.addMessages(amdisPeaks);
			if(result.hasErrorMessages()) {
				return result;
			}
			//
			IPeaks peaks = amdisPeaks.getProcessingResult();
			if(peaks == null) {
				result.addErrorMessage(IDENTIFIER, "Parsing peaks does not return a result");
				return result;
			}
			//
			IProcessingResult<Void> insertPeaks = PeakProcessorSupport.insertPeaks(chromatogramSelection, peaks.getPeaks(), peakDetectorSettings, subMonitor.split(10));
			result.addMessages(insertPeaks);
		} finally {
			fileChromatogram.delete();
		}
		return result;
	}

	private IProcessingResult<IPeaks<?>> executeAMDIS(File fileChromatogram, IOnsiteSettings onsiteSettings, AMDISParser parser, IProgressMonitor monitor) throws InterruptedException {

		IExtendedRuntimeSupport runtimeSupport;
		String amdisApplication = PreferenceSupplier.getAmdisApplication();
		String filePath = getAmdisCompatibleFilePath(fileChromatogram);
		try {
			runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(amdisApplication, filePath);
		} catch(FileNotFoundException e) {
			return createErrorResult("Can't get AMDIS executable, make sure that AMDIS is installed and the configuration points to the right AMDIS location", e);
		}
		try {
			runtimeSupport.getAmdisSupport().modifySettings(onsiteSettings);
			try {
				runtimeSupport.executeRunCommand();
			} catch(IOException e) {
				return createErrorResult("Can't execute AMDIS", e);
			}
			return parser.parse(monitor);
		} finally {
			try {
				runtimeSupport.executeKillCommand();
			} catch(IOException e) {
			}
		}
	}

	private <T> IProcessingResult<T> createErrorResult(String msg, Exception e) {

		DefaultProcessingResult<T> errorResult = new DefaultProcessingResult<>();
		errorResult.addErrorMessage(IDENTIFIER, msg + ": " + e.getMessage());
		return errorResult;
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
