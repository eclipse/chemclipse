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

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.internal.identifier.AmdisIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.settings.PeakDetectorSettings;
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
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakProcessorSupport {

	public static final String PEAK_CONVERTER_ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.peak.elu";

	public void extractEluFileAndSetPeaks(IChromatogramSelectionMSD chromatogramSelection, File file, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo<IPeaks<?>> processingInfo = PeakConverterMSD.convert(file, PEAK_CONVERTER_ID, monitor);
		IPeaks peaks = processingInfo.getProcessingResult();
		insertPeaks(chromatogramSelection, peaks.getPeaks(), peakDetectorSettings, monitor);
	}

	/**
	 * Extracts the given ELU file and sets the peaks.
	 *
	 * @param chromatogramSelection
	 * @param file
	 * @param minSignalToNoiseRatio
	 * @param monitor
	 * @return
	 */
	public static IProcessingResult<Void> insertPeaks(IChromatogramSelectionMSD chromatogramSelection, List<IPeak> peaks, PeakDetectorSettings peakDetectorSettings, IProgressMonitor monitor) {

		DefaultProcessingResult<Void> result = new DefaultProcessingResult<>();
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		for(IPeak peak : peaks) {
			if(peak instanceof IPeakMSD) {
				/*
				 * Try to add the peak.
				 */
				try {
					IPeakMSD peakMSD = (IPeakMSD)peak;
					IPeakModelMSD peakModelMSD = peakMSD.getPeakModel();
					//
					int startScan = peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_START_SCAN) instanceof Integer ? (int)peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_START_SCAN) : 0;
					int stopScan = peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_STOP_SCAN) instanceof Integer ? (int)peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_STOP_SCAN) : 0;
					int maxScan = peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_MAX_SCAN) instanceof Integer ? (int)peakModelMSD.getTemporarilyInfo(IPeakReader.TEMP_INFO_MAX_SCAN) : 0;
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
								List<Integer> retentionTimes = new ArrayList<>();
								for(int scan = startScan; scan <= stopScan; scan++) {
									retentionTimes.add(chromatogram.getScan(scan).getRetentionTime());
								}
								chromatogramPeakMSD.getPeakModel().replaceRetentionTimes(retentionTimes);
							}
						} catch(Exception e) {
							result.addWarnMessage(AmdisIdentifier.IDENTIFIER, "Pre check failed for peak: " + e);
						}
						/*
						 * Add the peak.
						 */
						chromatogram.addPeak(chromatogramPeakMSD);
					}
				} catch(IllegalArgumentException e) {
					result.addWarnMessage(AmdisIdentifier.IDENTIFIER, "Adding AMDIS peak failed: " + e);
				} catch(PeakException e) {
					result.addWarnMessage(AmdisIdentifier.IDENTIFIER, "Adding AMDIS peak failed: " + e);
				}
			}
		}
		return result;
	}

	private static boolean isValidPeak(IChromatogramPeakMSD peak, int startRetentionTime, int stopRetentionTime, PeakDetectorSettings peakDetectorSettings) {

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
