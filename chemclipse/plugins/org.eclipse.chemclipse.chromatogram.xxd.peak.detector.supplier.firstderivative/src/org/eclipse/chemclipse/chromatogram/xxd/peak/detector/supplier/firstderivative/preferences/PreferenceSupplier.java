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
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.preferences;

import org.eclipse.chemclipse.chromatogram.peak.detector.core.FilterMode;
import org.eclipse.chemclipse.chromatogram.peak.detector.model.Threshold;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsCSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsMSD;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.PeakDetectorSettingsWSD;
import org.eclipse.chemclipse.support.model.WindowSize;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final float MIN_SN_RATIO_MIN = 0.0f; // 0 = all peaks will be added
	public static final float MIN_SN_RATIO_MAX = Float.MAX_VALUE; // 0 = all peaks will be added
	public static final int MIN_WINDOW_SIZE = 0;
	public static final int MAX_WINDOW_SIZE = 45;
	//
	public static final String P_THRESHOLD_CSD = "thresholdCSD";
	public static final String DEF_THRESHOLD_CSD = Threshold.MEDIUM.name();
	public static final String P_INCLUDE_BACKGROUND_CSD = "includeBackgroundCSD";
	public static final boolean DEF_INCLUDE_BACKGROUND_CSD = false; // false will use BV oder VB, if true VV will be used.
	public static final String P_MIN_SN_RATIO_CSD = "minSNRatioCSD";
	public static final float DEF_MIN_SN_RATIO_CSD = 0.0f; // 0 = all peaks will be added
	public static final String P_MOVING_AVERAGE_WINDOW_SIZE_CSD = "movingAverageWindowSizeCSD";
	public static final String DEF_MOVING_AVERAGE_WINDOW_SIZE_CSD = String.valueOf(3);
	public static final String P_USE_NOISE_SEGMENTS_CSD = "useNoiseSegmentsCSD";
	public static final boolean DEF_USE_NOISE_SEGMENTS_CSD = false;
	public static final String P_OPTIMIZE_BASELINE_CSD = "optimizeBaselineCSD";
	public static final boolean DEF_OPTIMIZE_BASELINE_CSD = false;
	//
	public static final String P_THRESHOLD_MSD = "thresholdMSD";
	public static final String DEF_THRESHOLD_MSD = Threshold.MEDIUM.name();
	public static final String P_INCLUDE_BACKGROUND_MSD = "includeBackgroundMSD";
	public static final boolean DEF_INCLUDE_BACKGROUND_MSD = false; // false will use BV oder VB, if true VV will be used.
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
	//
	public static final String P_THRESHOLD_WSD = "thresholdWSD";
	public static final String DEF_THRESHOLD_WSD = Threshold.MEDIUM.name();
	public static final String P_INCLUDE_BACKGROUND_WSD = "includeBackgroundWSD";
	public static final boolean DEF_INCLUDE_BACKGROUND_WSD = false; // false will use BV oder VB, if true VV will be used.
	public static final String P_MIN_SN_RATIO_WSD = "minSNRatioWSD";
	public static final float DEF_MIN_SN_RATIO_WSD = 0.0f; // 0 = all peaks will be added
	public static final String P_MOVING_AVERAGE_WINDOW_SIZE_WSD = "movingAverageWindowSizeWSD";
	public static final String DEF_MOVING_AVERAGE_WINDOW_SIZE_WSD = String.valueOf(3);
	//
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_INCLUDE_BACKGROUND_CSD, Boolean.toString(DEF_INCLUDE_BACKGROUND_CSD));
		putDefault(P_MIN_SN_RATIO_CSD, Float.toString(DEF_MIN_SN_RATIO_CSD));
		putDefault(P_MOVING_AVERAGE_WINDOW_SIZE_CSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_CSD);
		putDefault(P_THRESHOLD_CSD, DEF_THRESHOLD_CSD);
		putDefault(P_USE_NOISE_SEGMENTS_CSD, Boolean.toString(DEF_USE_NOISE_SEGMENTS_CSD));
		putDefault(P_OPTIMIZE_BASELINE_CSD, Boolean.toString(DEF_OPTIMIZE_BASELINE_CSD));
		//
		putDefault(P_INCLUDE_BACKGROUND_MSD, Boolean.toString(DEF_INCLUDE_BACKGROUND_MSD));
		putDefault(P_MIN_SN_RATIO_MSD, Float.toString(DEF_MIN_SN_RATIO_MSD));
		putDefault(P_MOVING_AVERAGE_WINDOW_SIZE_MSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_MSD);
		putDefault(P_THRESHOLD_MSD, DEF_THRESHOLD_MSD);
		putDefault(P_USE_NOISE_SEGMENTS_MSD, Boolean.toString(DEF_USE_NOISE_SEGMENTS_MSD));
		putDefault(P_OPTIMIZE_BASELINE_MSD, Boolean.toString(DEF_OPTIMIZE_BASELINE_MSD));
		putDefault(P_FILTER_MODE_MSD, DEF_FILTER_MODE_MSD);
		putDefault(P_MZ_VALUES_TO_FILTER_MSD, DEF_MZ_VALUES_TO_FILTER_MSD);
		putDefault(P_USE_INDIVIDUAL_TRACES_MSD, Boolean.toString(DEF_USE_INDIVIDUAL_TRACES_MSD));
		//
		putDefault(P_INCLUDE_BACKGROUND_WSD, Boolean.toString(DEF_INCLUDE_BACKGROUND_WSD));
		putDefault(P_MIN_SN_RATIO_WSD, Float.toString(DEF_MIN_SN_RATIO_WSD));
		putDefault(P_MOVING_AVERAGE_WINDOW_SIZE_WSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_WSD);
		putDefault(P_THRESHOLD_WSD, DEF_THRESHOLD_WSD);
	}

	public static PeakDetectorSettingsCSD getPeakDetectorSettingsCSD() {

		PeakDetectorSettingsCSD settings = new PeakDetectorSettingsCSD();
		settings.setThreshold(Threshold.valueOf(INSTANCE().get(P_THRESHOLD_CSD, DEF_THRESHOLD_CSD)));
		settings.setIncludeBackground(INSTANCE().getBoolean(P_INCLUDE_BACKGROUND_CSD, DEF_INCLUDE_BACKGROUND_CSD));
		settings.setMinimumSignalToNoiseRatio(INSTANCE().getFloat(P_MIN_SN_RATIO_CSD, DEF_MIN_SN_RATIO_CSD));
		settings.setMovingAverageWindowSize(WindowSize.getAdjustedSetting(INSTANCE().get(P_MOVING_AVERAGE_WINDOW_SIZE_CSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_CSD)));
		settings.setUseNoiseSegments(INSTANCE().getBoolean(P_USE_NOISE_SEGMENTS_CSD, DEF_USE_NOISE_SEGMENTS_CSD));
		settings.setOptimizeBaseline(INSTANCE().getBoolean(P_OPTIMIZE_BASELINE_CSD, DEF_OPTIMIZE_BASELINE_CSD));
		return settings;
	}

	public static PeakDetectorSettingsMSD getPeakDetectorSettingsMSD() {

		PeakDetectorSettingsMSD settings = new PeakDetectorSettingsMSD();
		settings.setThreshold(Threshold.valueOf(INSTANCE().get(P_THRESHOLD_MSD, DEF_THRESHOLD_MSD)));
		settings.setIncludeBackground(INSTANCE().getBoolean(P_INCLUDE_BACKGROUND_MSD, DEF_INCLUDE_BACKGROUND_MSD));
		settings.setMinimumSignalToNoiseRatio(INSTANCE().getFloat(P_MIN_SN_RATIO_MSD, DEF_MIN_SN_RATIO_MSD));
		settings.setMovingAverageWindowSize(WindowSize.getAdjustedSetting(INSTANCE().get(P_MOVING_AVERAGE_WINDOW_SIZE_MSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_MSD)));
		settings.setUseNoiseSegments(INSTANCE().getBoolean(P_USE_NOISE_SEGMENTS_MSD, DEF_USE_NOISE_SEGMENTS_MSD));
		settings.setFilterMode(FilterMode.valueOf(INSTANCE().get(P_FILTER_MODE_MSD, DEF_FILTER_MODE_MSD)));
		settings.setFilterIonsString(INSTANCE().get(P_MZ_VALUES_TO_FILTER_MSD, DEF_MZ_VALUES_TO_FILTER_MSD));
		settings.setUseIndividualTraces(INSTANCE().getBoolean(P_USE_INDIVIDUAL_TRACES_MSD, DEF_USE_INDIVIDUAL_TRACES_MSD));
		settings.setOptimizeBaseline(INSTANCE().getBoolean(P_OPTIMIZE_BASELINE_MSD, DEF_OPTIMIZE_BASELINE_MSD));
		return settings;
	}

	public static PeakDetectorSettingsWSD getPeakDetectorSettingsWSD() {

		PeakDetectorSettingsWSD settings = new PeakDetectorSettingsWSD();
		settings.setThreshold(Threshold.valueOf(INSTANCE().get(P_THRESHOLD_WSD, DEF_THRESHOLD_WSD)));
		settings.setIncludeBackground(INSTANCE().getBoolean(P_INCLUDE_BACKGROUND_WSD, DEF_INCLUDE_BACKGROUND_WSD));
		settings.setMinimumSignalToNoiseRatio(INSTANCE().getFloat(P_MIN_SN_RATIO_WSD, DEF_MIN_SN_RATIO_WSD));
		settings.setMovingAverageWindowSize(WindowSize.getAdjustedSetting(INSTANCE().get(P_MOVING_AVERAGE_WINDOW_SIZE_WSD, DEF_MOVING_AVERAGE_WINDOW_SIZE_WSD)));
		return settings;
	}
}