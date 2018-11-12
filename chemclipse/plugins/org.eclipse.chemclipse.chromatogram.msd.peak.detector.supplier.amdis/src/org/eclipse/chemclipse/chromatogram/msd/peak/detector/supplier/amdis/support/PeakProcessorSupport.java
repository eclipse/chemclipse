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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakProcessorSupport {

	private static final String PEAK_CONVERTER_ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.peak.elu";
	private static final Logger logger = Logger.getLogger(PeakProcessorSupport.class);

	/**
	 * Extracts the given ELU file and sets the peaks.
	 * 
	 * @param chromatogramSelection
	 * @param file
	 * @param minSignalToNoiseRatio
	 * @param monitor
	 */
	public void extractEluFileAndSetPeaks(IChromatogramSelectionMSD chromatogramSelection, File file, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		extractEluFileAndSetPeaks(chromatogram, startRetentionTime, stopRetentionTime, file, peakDetectorSettings, monitor);
	}

	private void extractEluFileAndSetPeaks(IChromatogramMSD chromatogram, int startRetentionTime, int stopRetentionTime, File file, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		try {
			IProcessingInfo processingInfo = PeakConverterMSD.convert(file, PEAK_CONVERTER_ID, monitor);
			IPeaks peaks = processingInfo.getProcessingResult(IPeaks.class);
			//
			for(IPeak peak : peaks.getPeaks()) {
				if(peak instanceof IPeakMSD) {
					/*
					 * Try to add the peak.
					 */
					try {
						IPeakMSD peakMSD = (IPeakMSD)peak;
						IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
						//
						int startScan = (peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_START_SCAN) instanceof Integer) ? (int)peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_START_SCAN) : 0;
						int stopScan = (peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_STOP_SCAN) instanceof Integer) ? (int)peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_STOP_SCAN) : 0;
						int maxScan = (peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_MAX_SCAN) instanceof Integer) ? (int)peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_MAX_SCAN) : 0;
						/*
						 * There seems to be an offset of 1 scan.
						 * Why? No clue ...
						 */
						startScan++;
						stopScan++;
						maxScan++;
						/*
						 * Add only peaks above the given minSignalToNoiseRatio and within the
						 * chromatogram selection.
						 */
						IChromatogramPeakMSD chromatogramPeakMSD = new ChromatogramPeakMSD(peakModelMSD, chromatogram, "AMDIS Peak (ELU)");
						if(isValidPeak(chromatogramPeakMSD, startRetentionTime, stopRetentionTime, peakDetectorSettings)) {
							/*
							 * Adjust the peak retention times if possible.
							 */
							try {
								/*
								 * Pre-check
								 */
								if(startScan > 0 && stopScan > startScan && maxScan > startScan) {
									List<Integer> retentionTimes = new ArrayList<Integer>();
									for(int scan = startScan; scan <= stopScan; scan++) {
										retentionTimes.add(chromatogram.getScan(scan).getRetentionTime());
									}
									chromatogramPeakMSD.getPeakModel().replaceRetentionTimes(retentionTimes);
								}
							} catch(Exception e) {
								logger.warn(e);
							}
							/*
							 * Add the peak.
							 */
							chromatogram.addPeak(chromatogramPeakMSD);
						}
					} catch(IllegalArgumentException e) {
						logger.warn(e);
					} catch(PeakException e) {
						logger.warn(e);
					}
				}
			}
		} catch(TypeCastException e) {
			logger.warn(e);
		}
	}

	private boolean isValidPeak(IChromatogramPeakMSD peak, int startRetentionTime, int stopRetentionTime, PeakDetectorSettings peakDetectorSettings) {

		/*
		 * Null
		 */
		if(peak == null) {
			return false;
		}
		//
		IPeakModel peakModel = peak.getPeakModel();
		/*
		 * Chromatogram Selection Check
		 */
		if(peakModel.getStartRetentionTime() < startRetentionTime || peakModel.getStopRetentionTime() > stopRetentionTime) {
			return false;
		}
		/*
		 * Min S/N
		 */
		if(peak.getSignalToNoiseRatio() < peakDetectorSettings.getMinSignalToNoiseRatio()) {
			return false;
		}
		/*
		 * Leading and tailing
		 */
		float leading = peakModel.getLeading();
		if(leading < peakDetectorSettings.getMinLeading() || leading > peakDetectorSettings.getMaxLeading()) {
			return false;
		}
		float tailing = peakModel.getTailing();
		if(tailing < peakDetectorSettings.getMinTailing() || tailing > peakDetectorSettings.getMaxTailing()) {
			return false;
		}
		/*
		 * Checks passed.
		 */
		return true;
	}
}
