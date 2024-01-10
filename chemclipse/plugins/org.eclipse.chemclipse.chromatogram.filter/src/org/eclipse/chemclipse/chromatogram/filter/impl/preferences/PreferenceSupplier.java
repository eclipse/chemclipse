/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.preferences;

import org.eclipse.chemclipse.chromatogram.filter.Activator;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsReset;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsSelection;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsTransform;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.PeakTargetsToReferencesSettings;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.ScanTargetsToPeakSettings;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.ScanTargetsToReferencesSettings;
import org.eclipse.chemclipse.chromatogram.filter.settings.MaxDetectorFilterSettings;
import org.eclipse.chemclipse.chromatogram.filter.system.SettingsIonRounding;
import org.eclipse.chemclipse.chromatogram.filter.system.SettingsRetentionIndexQC;
import org.eclipse.chemclipse.model.math.IonRoundMethod;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final double MIN_RETENTION_TIME_MINUTES = 0.0d;
	public static final double MAX_RETENTION_TIME_MINUTES = Double.MAX_VALUE;
	public static final float MIN_FACTOR = 0.0f;
	public static final float MAX_FACTOR = 100.0f;
	public static final int MIN_COUNT_MARKER = 0;
	public static final int MAX_COUNT_MARKER = Integer.MAX_VALUE;
	public static final int MIN_MZ = 18;
	public static final int MAX_MZ = 1000;
	//
	public static final String P_START_RETENTION_TIME_MINUTES = "startRetentionTimeMinutes";
	public static final double DEF_START_RETENTION_TIME_MINUTES = 1;
	public static final String P_STOP_RETENTION_TIME_MINUTES = "stopRetentionTimeMinutes";
	public static final double DEF_STOP_RETENTION_TIME_MINUTES = 10;
	//
	public static final String P_STTP_TRANSFER_CLOSEST_SCAN = "scanTargetsToPeakTransferClosestScan";
	public static final boolean DEF_STTP_TRANSFER_CLOSEST_SCAN = false;
	public static final String P_STTP_USE_BEST_TARGET_ONLY = "scanTargetsToPeakUseBestTargetOnly";
	public static final boolean DEF_STTP_USE_BEST_TARGET_ONLY = false;
	//
	public static final String P_PTTR_USE_BEST_TARGET_ONLY = "peakTargetsToReferencesUseBestTargetOnly";
	public static final boolean DEF_PTTR_USE_BEST_TARGET_ONLY = false;
	public static final String P_DELTA_RETENTION_TIME_MINUTES = "peakTargetsToReferencesDeltaRetentionTimeMinutes";
	public static final double DEF_DELTA_RETENTION_TIME_MINUTES = 0.1;
	//
	public static final String P_STTR_USE_BEST_TARGET_ONLY = "scanTargetsToReferencesUseBestTargetOnly";
	public static final boolean DEF_STTR_USE_BEST_TARGET_ONLY = false;
	//
	public static final String P_USE_RETENTION_INDEX_QC = "qcUseRetentionIndex";
	public static final boolean DEF_USE_RETENTION_INDEX_QC = false;
	public static final String P_ION_ROUND_METHOD = "ionRoundMethod";
	public static final String DEF_ION_ROUND_METHOD = IonRoundMethod.DEFAULT.name();
	/*
	 * Max Detector UI
	 */
	public static final String P_MAX_DETECTOR_TARGET_NAME = "maxDetectorTargetName";
	public static final String DEF_MAX_DETECTOR_TARGET_NAME = "M";
	public static final String P_MAX_DETECTOR_MATCH_FACTOR = "maxDetectorMatchFactor";
	public static final float DEF_MAX_DETECTOR_MATCH_FACTOR = 80.0f;
	public static final String P_MAX_DETECTOR_MINIMA = "maxDetectorMinima";
	public static final boolean DEF_MAX_DETECTOR_MINIMA = false;
	public static final String P_MAX_DETECTOR_COUNT = "maxDetectorCount";
	public static final int DEF_MAX_DETECTOR_COUNT = 0;
	/*
	 * Transform
	 */
	public static final String P_TRANSFORM_MZ = "transformMZ";
	public static final int DEF_TRANSFORM_MZ = 28;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_START_RETENTION_TIME_MINUTES, Double.toString(DEF_START_RETENTION_TIME_MINUTES));
		putDefault(P_STOP_RETENTION_TIME_MINUTES, Double.toString(DEF_STOP_RETENTION_TIME_MINUTES));
		//
		putDefault(P_STTP_TRANSFER_CLOSEST_SCAN, Boolean.toString(DEF_STTP_TRANSFER_CLOSEST_SCAN));
		putDefault(P_STTP_USE_BEST_TARGET_ONLY, Boolean.toString(DEF_STTP_USE_BEST_TARGET_ONLY));
		//
		putDefault(P_PTTR_USE_BEST_TARGET_ONLY, Boolean.toString(DEF_PTTR_USE_BEST_TARGET_ONLY));
		putDefault(P_DELTA_RETENTION_TIME_MINUTES, Double.toString(DEF_DELTA_RETENTION_TIME_MINUTES));
		//
		putDefault(P_STTR_USE_BEST_TARGET_ONLY, Boolean.toString(DEF_STTR_USE_BEST_TARGET_ONLY));
		//
		putDefault(P_USE_RETENTION_INDEX_QC, Boolean.toString(DEF_USE_RETENTION_INDEX_QC));
		putDefault(P_ION_ROUND_METHOD, DEF_ION_ROUND_METHOD);
		//
		putDefault(P_MAX_DETECTOR_TARGET_NAME, DEF_MAX_DETECTOR_TARGET_NAME);
		putDefault(P_MAX_DETECTOR_MATCH_FACTOR, Float.toString(DEF_MAX_DETECTOR_MATCH_FACTOR));
		putDefault(P_MAX_DETECTOR_MINIMA, Boolean.toString(DEF_MAX_DETECTOR_MINIMA));
		putDefault(P_MAX_DETECTOR_COUNT, Integer.toString(DEF_MAX_DETECTOR_COUNT));
		//
		putDefault(P_TRANSFORM_MZ, Integer.toString(DEF_TRANSFORM_MZ));
		//
	}

	public static FilterSettingsSelection getFilterSettingsSelection() {

		FilterSettingsSelection filterSettings = new FilterSettingsSelection();
		filterSettings.setStartRetentionTimeMinutes(INSTANCE().getDouble(P_START_RETENTION_TIME_MINUTES, DEF_START_RETENTION_TIME_MINUTES));
		filterSettings.setStopRetentionTimeMinutes(INSTANCE().getDouble(P_STOP_RETENTION_TIME_MINUTES, DEF_STOP_RETENTION_TIME_MINUTES));
		return filterSettings;
	}

	public static FilterSettingsReset getFilterSettingsReset() {

		return new FilterSettingsReset();
	}

	public static FilterSettingsTransform getFilterSettingsTransform() {

		FilterSettingsTransform filterSettings = new FilterSettingsTransform();
		filterSettings.setMz(INSTANCE().getInteger(P_TRANSFORM_MZ, DEF_TRANSFORM_MZ));
		//
		return filterSettings;
	}

	public static SettingsIonRounding getFilterSettingsIonRounding() {

		SettingsIonRounding settings = new SettingsIonRounding();
		try {
			settings.setIonRoundMethod(IonRoundMethod.valueOf(INSTANCE().get(P_ION_ROUND_METHOD, DEF_ION_ROUND_METHOD)));
		} catch(Exception e) {
			settings.setIonRoundMethod(IonRoundMethod.DEFAULT);
		}
		return settings;
	}

	public static SettingsRetentionIndexQC getFilterSettingsQC() {

		SettingsRetentionIndexQC settings = new SettingsRetentionIndexQC();
		settings.setUseRetentionIndexQC(INSTANCE().getBoolean(P_USE_RETENTION_INDEX_QC, DEF_USE_RETENTION_INDEX_QC));
		return settings;
	}

	public static ScanTargetsToPeakSettings getScanToPeakTargetTransferSettings() {

		ScanTargetsToPeakSettings settings = new ScanTargetsToPeakSettings();
		settings.setTransferClosestScan(INSTANCE().getBoolean(P_STTP_TRANSFER_CLOSEST_SCAN, DEF_STTP_TRANSFER_CLOSEST_SCAN));
		settings.setUseBestTargetOnly(INSTANCE().getBoolean(P_STTP_USE_BEST_TARGET_ONLY, DEF_STTP_USE_BEST_TARGET_ONLY));
		return settings;
	}

	public static PeakTargetsToReferencesSettings getPeaksToReferencesTransferSettings() {

		PeakTargetsToReferencesSettings settings = new PeakTargetsToReferencesSettings();
		settings.setUseBestTargetOnly(INSTANCE().getBoolean(P_PTTR_USE_BEST_TARGET_ONLY, DEF_PTTR_USE_BEST_TARGET_ONLY));
		settings.setDeltaRetentionTime(INSTANCE().getDouble(P_DELTA_RETENTION_TIME_MINUTES, DEF_DELTA_RETENTION_TIME_MINUTES));
		return settings;
	}

	public static ScanTargetsToReferencesSettings getScansToReferencesTransferSettings() {

		ScanTargetsToReferencesSettings settings = new ScanTargetsToReferencesSettings();
		settings.setUseBestTargetOnly(INSTANCE().getBoolean(P_STTR_USE_BEST_TARGET_ONLY, DEF_STTR_USE_BEST_TARGET_ONLY));
		return settings;
	}

	public static MaxDetectorFilterSettings getMaxDetectorFilterSettings() {

		MaxDetectorFilterSettings settings = new MaxDetectorFilterSettings();
		settings.setTargetName(INSTANCE().get(P_MAX_DETECTOR_TARGET_NAME, DEF_MAX_DETECTOR_TARGET_NAME));
		settings.setMatchFactor(INSTANCE().getFloat(P_MAX_DETECTOR_MATCH_FACTOR, DEF_MAX_DETECTOR_MATCH_FACTOR));
		settings.setDetectMinima(INSTANCE().getBoolean(P_MAX_DETECTOR_MINIMA, DEF_MAX_DETECTOR_MINIMA));
		settings.setCount(INSTANCE().getInteger(P_MAX_DETECTOR_COUNT, DEF_MAX_DETECTOR_COUNT));
		//
		return settings;
	}
}