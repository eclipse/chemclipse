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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative.preferences;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative.Activator;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.peak.detector.core.FilterMode;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.model.DetectorType;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences.AbstractFirstDerivativePreferenceSupplier;
import org.eclipse.chemclipse.support.model.WindowSize;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractFirstDerivativePreferenceSupplier implements IPreferenceSupplier {

	public static final String P_THRESHOLD_MSD = "thresholdMSD";
	public static final String DEF_THRESHOLD_MSD = Threshold.MEDIUM.name();
	public static final String P_DETECTOR_TYPE_MSD = "detectorTypeMSD";
	public static final String DEF_DETECTOR_TYPE_MSD = DetectorType.VV.name();
	public static final String P_MIN_SN_RATIO_MSD = "minSNRatioMSD";
	public static final float DEF_MIN_SN_RATIO_MSD = 0.0f; // 0 = all peaks will be added
	public static final String P_MOVING_AVERAGE_WINDOW_SIZE_MSD = "movingAverageWindowSizeMSD";
	public static final String DEF_MOVING_AVERAGE_WINDOW_SIZE_MSD = String.valueOf(3);
	public static final String P_USE_NOISE_SEGMENTS_MSD = "useNoiseSegmentsMSD";
	public static final boolean DEF_USE_NOISE_SEGMENTS_MSD = false;
	public static final String P_OPTIMIZE_BASELINE_MSD = "optimizeBaselineMSD";
	public static final boolean DEF_OPTIMIZE_BASELINE_MSD = false;
	public static final String P_FILTER_MODE_MSD = "filterModeMSD";
	public static final String DEF_FILTER_MODE_MSD = FilterMode.EXCLUDE.name();
	public static final String P_MZ_VALUES_TO_FILTER_MSD = "mzValuesToFilterMSD";
	public static final String DEF_MZ_VALUES_TO_FILTER_MSD = "";
	public static final String P_USE_INDIVIDUAL_TRACES_MSD = "useIndividualTracesMSD";
	public static final boolean DEF_USE_INDIVIDUAL_TRACES_MSD = false;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_DETECTOR_TYPE_MSD, DEF_DETECTOR_TYPE_MSD);
		putDefault(P_MIN_SN_RATIO_MSD, Float.toString(DEF_MIN_SN_RATIO_MSD));
		putDefault(P_MOVING_AVERAGE_WINDOW_SIZE_MSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_MSD);
		putDefault(P_THRESHOLD_MSD, DEF_THRESHOLD_MSD);
		putDefault(P_USE_NOISE_SEGMENTS_MSD, Boolean.toString(DEF_USE_NOISE_SEGMENTS_MSD));
		putDefault(P_OPTIMIZE_BASELINE_MSD, Boolean.toString(DEF_OPTIMIZE_BASELINE_MSD));
		putDefault(P_FILTER_MODE_MSD, DEF_FILTER_MODE_MSD);
		putDefault(P_MZ_VALUES_TO_FILTER_MSD, DEF_MZ_VALUES_TO_FILTER_MSD);
		putDefault(P_USE_INDIVIDUAL_TRACES_MSD, Boolean.toString(DEF_USE_INDIVIDUAL_TRACES_MSD));
	}

	public static PeakDetectorSettingsMSD getPeakDetectorSettingsMSD() {

		PeakDetectorSettingsMSD settings = new PeakDetectorSettingsMSD();
		settings.setThreshold(Threshold.valueOf(INSTANCE().get(P_THRESHOLD_MSD, DEF_THRESHOLD_MSD)));
		settings.setDetectorType(getDetectorType(P_DETECTOR_TYPE_MSD, DEF_DETECTOR_TYPE_MSD));
		settings.setMinimumSignalToNoiseRatio(INSTANCE().getFloat(P_MIN_SN_RATIO_MSD, DEF_MIN_SN_RATIO_MSD));
		settings.setMovingAverageWindowSize(WindowSize.getAdjustedSetting(INSTANCE().get(P_MOVING_AVERAGE_WINDOW_SIZE_MSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_MSD)));
		settings.setUseNoiseSegments(INSTANCE().getBoolean(P_USE_NOISE_SEGMENTS_MSD, DEF_USE_NOISE_SEGMENTS_MSD));
		settings.setFilterMode(FilterMode.valueOf(INSTANCE().get(P_FILTER_MODE_MSD, DEF_FILTER_MODE_MSD)));
		settings.setFilterIonsString(INSTANCE().get(P_MZ_VALUES_TO_FILTER_MSD, DEF_MZ_VALUES_TO_FILTER_MSD));
		settings.setUseIndividualTraces(INSTANCE().getBoolean(P_USE_INDIVIDUAL_TRACES_MSD, DEF_USE_INDIVIDUAL_TRACES_MSD));
		settings.setOptimizeBaseline(INSTANCE().getBoolean(P_OPTIMIZE_BASELINE_MSD, DEF_OPTIMIZE_BASELINE_MSD));
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