/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.peak.detector.supplier.firstderivative.preferences;

import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.supplier.firstderivative.Activator;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.AbstractFirstDerivativePreferenceSupplier;
import org.eclipse.chemclipse.support.model.WindowSize;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractFirstDerivativePreferenceSupplier implements IPreferenceSupplier {

	public static final String P_THRESHOLD_WSD = "thresholdWSD";
	public static final String DEF_THRESHOLD_WSD = Threshold.MEDIUM.name();
	public static final String P_DETECTOR_TYPE_WSD = "detectorTypeWSD";
	public static final String DEF_DETECTOR_TYPE_WSD = DetectorType.VV.name();
	public static final String P_MIN_SN_RATIO_WSD = "minSNRatioWSD";
	public static final float DEF_MIN_SN_RATIO_WSD = 0.0f; // 0 = all peaks will be added
	public static final String P_MOVING_AVERAGE_WINDOW_SIZE_WSD = "movingAverageWindowSizeWSD";
	public static final String DEF_MOVING_AVERAGE_WINDOW_SIZE_WSD = String.valueOf(3);

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_DETECTOR_TYPE_WSD, DEF_DETECTOR_TYPE_WSD);
		putDefault(P_MIN_SN_RATIO_WSD, Float.toString(DEF_MIN_SN_RATIO_WSD));
		putDefault(P_MOVING_AVERAGE_WINDOW_SIZE_WSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_WSD);
		putDefault(P_THRESHOLD_WSD, DEF_THRESHOLD_WSD);
	}

	public static PeakDetectorSettingsWSD getPeakDetectorSettingsWSD() {

		PeakDetectorSettingsWSD settings = new PeakDetectorSettingsWSD();
		settings.setThreshold(Threshold.valueOf(INSTANCE().get(P_THRESHOLD_WSD, DEF_THRESHOLD_WSD)));
		settings.setDetectorType(getDetectorType(P_DETECTOR_TYPE_WSD, DEF_DETECTOR_TYPE_WSD));
		settings.setMinimumSignalToNoiseRatio(INSTANCE().getFloat(P_MIN_SN_RATIO_WSD, DEF_MIN_SN_RATIO_WSD));
		settings.setMovingAverageWindowSize(WindowSize.getAdjustedSetting(INSTANCE().get(P_MOVING_AVERAGE_WINDOW_SIZE_WSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_WSD)));
		return settings;
	}

	private static DetectorType getDetectorType(String key, String def) {

		try {
			return DetectorType.valueOf(INSTANCE().get(key, def));
		} catch(Exception e) {
			return DetectorType.VV;
		}
	}
}