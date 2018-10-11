/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.AbstractBaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.tic.settings.DetectorSettings;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineDetector extends AbstractBaselineDetector {

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogramSelection, baselineDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(baselineDetectorSettings instanceof DetectorSettings) {
				calculateBaseline(chromatogramSelection);
			}
		}
		return processingInfo;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		DetectorSettings detectorSettings = PreferenceSupplier.getDetectorSettings();
		return setBaseline(chromatogramSelection, detectorSettings, monitor);
	}

	/**
	 * Calculates the baseline.
	 */
	@SuppressWarnings("rawtypes")
	private void calculateBaseline(IChromatogramSelection chromatogramSelection) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		//
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * Get and adjust the lowest value.
		 */
		float lowestTIC = getLowestTIC(chromatogram, startScan, stopScan);
		if(lowestTIC > 0) {
			/*
			 * Let the value be just above the threshold.
			 * Otherwise, scans could be removed by applying
			 * a baseline subtract method if the lowest value
			 * is a big as the lowest TIC.
			 */
			float backgroundAbundance;
			if(lowestTIC >= 1) {
				backgroundAbundance = lowestTIC - 1;
			} else {
				backgroundAbundance = lowestTIC - Float.MIN_VALUE;
			}
			/*
			 * Apply the baseline
			 */
			for(int i = startScan; i < stopScan; i++) {
				IScan scan = chromatogram.getScan(i);
				IScan scanNext = chromatogram.getScan(i + 1);
				int startRetentionTime = scan.getRetentionTime();
				int stopRetentionTime = scanNext.getRetentionTime();
				baselineModel.addBaseline(startRetentionTime, stopRetentionTime, backgroundAbundance, backgroundAbundance, true);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private float getLowestTIC(IChromatogram chromatogram, int startScan, int stopScan) {

		float lowestTIC = Float.MAX_VALUE;
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			float totalSignal = scan.getTotalSignal();
			if(totalSignal < lowestTIC) {
				lowestTIC = totalSignal;
			}
		}
		return lowestTIC;
	}
}
