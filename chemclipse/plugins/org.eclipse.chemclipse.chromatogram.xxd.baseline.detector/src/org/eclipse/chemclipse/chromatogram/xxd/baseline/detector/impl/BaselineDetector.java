/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.impl;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.AbstractBaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("rawtypes")
public class BaselineDetector extends AbstractBaselineDetector {

	@Override
	public IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IBaselineDetectorSettings baselineDetectorSettings, IProgressMonitor monitor) {

		IProcessingInfo processingInfo = super.validate(chromatogramSelection, baselineDetectorSettings, monitor);
		if(!processingInfo.hasErrorMessages()) {
			if(baselineDetectorSettings instanceof DetectorSettings) {
				float intensity = extractLowestIntensity(chromatogramSelection);
				if(intensity != 0) {
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					IBaselineModel baselineModel = chromatogram.getBaselineModel();
					int startRetentionTime = chromatogramSelection.getStartRetentionTime();
					int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
					float startBackgroundAbundance = intensity;
					float stopBackgroundAbundance = intensity;
					baselineModel.addBaseline(startRetentionTime, stopRetentionTime, startBackgroundAbundance, stopBackgroundAbundance, false);
				}
			}
		}
		return processingInfo;
	}

	@Override
	public IProcessingInfo setBaseline(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		DetectorSettings settings = new DetectorSettings();
		return setBaseline(chromatogramSelection, settings, monitor);
	}

	private float extractLowestIntensity(IChromatogramSelection chromatogramSelection) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int scanStart = chromatogramSelection.getStartScan();
		int scanStop = chromatogramSelection.getStopScan();
		//
		float intensity = 0;
		if(scanStart > 0 && scanStop > 0) {
			intensity = Float.MAX_VALUE;
			for(int i = scanStart; i <= scanStop; i++) {
				IScan scan = chromatogram.getScan(i);
				intensity = Math.min(intensity, scan.getTotalSignal());
			}
		}
		//
		return intensity;
	}
}
