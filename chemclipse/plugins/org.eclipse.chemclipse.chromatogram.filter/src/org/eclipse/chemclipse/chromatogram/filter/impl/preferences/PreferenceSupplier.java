/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.Activator;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsReset;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsSelection;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.PeakTargetsToReferencesSettings;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.ScanTargetsToPeakSettings;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.ScanTargetsToReferencesSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final double MIN_RETENTION_TIME_MINUTES = 0.0d;
	public static final double MAX_RETENTION_TIME_MINUTES = Double.MAX_VALUE;
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
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_START_RETENTION_TIME_MINUTES, Double.toString(DEF_START_RETENTION_TIME_MINUTES));
		defaultValues.put(P_STOP_RETENTION_TIME_MINUTES, Double.toString(DEF_STOP_RETENTION_TIME_MINUTES));
		//
		defaultValues.put(P_STTP_TRANSFER_CLOSEST_SCAN, Boolean.toString(DEF_STTP_TRANSFER_CLOSEST_SCAN));
		defaultValues.put(P_STTP_USE_BEST_TARGET_ONLY, Boolean.toString(DEF_STTP_USE_BEST_TARGET_ONLY));
		//
		defaultValues.put(P_PTTR_USE_BEST_TARGET_ONLY, Boolean.toString(DEF_PTTR_USE_BEST_TARGET_ONLY));
		defaultValues.put(P_DELTA_RETENTION_TIME_MINUTES, Double.toString(DEF_DELTA_RETENTION_TIME_MINUTES));
		//
		defaultValues.put(P_STTR_USE_BEST_TARGET_ONLY, Boolean.toString(DEF_STTR_USE_BEST_TARGET_ONLY));
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettingsSelection getFilterSettingsSelection() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		FilterSettingsSelection filterSettings = new FilterSettingsSelection();
		filterSettings.setStartRetentionTimeMinutes(preferences.getDouble(P_START_RETENTION_TIME_MINUTES, DEF_START_RETENTION_TIME_MINUTES));
		filterSettings.setStopRetentionTimeMinutes(preferences.getDouble(P_STOP_RETENTION_TIME_MINUTES, DEF_STOP_RETENTION_TIME_MINUTES));
		return filterSettings;
	}

	public static FilterSettingsReset getFilterSettingsReset() {

		return new FilterSettingsReset();
	}

	public static ScanTargetsToPeakSettings getScanToPeakTargetTransferSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ScanTargetsToPeakSettings settings = new ScanTargetsToPeakSettings();
		settings.setTransferClosestScan(preferences.getBoolean(P_STTP_TRANSFER_CLOSEST_SCAN, DEF_STTP_TRANSFER_CLOSEST_SCAN));
		settings.setUseBestTargetOnly(preferences.getBoolean(P_STTP_USE_BEST_TARGET_ONLY, DEF_STTP_USE_BEST_TARGET_ONLY));
		return settings;
	}

	public static PeakTargetsToReferencesSettings getPeaksToReferencesTransferSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		PeakTargetsToReferencesSettings settings = new PeakTargetsToReferencesSettings();
		settings.setUseBestTargetOnly(preferences.getBoolean(P_PTTR_USE_BEST_TARGET_ONLY, DEF_PTTR_USE_BEST_TARGET_ONLY));
		settings.setDeltaRetentionTime(preferences.getDouble(P_DELTA_RETENTION_TIME_MINUTES, DEF_DELTA_RETENTION_TIME_MINUTES));
		return settings;
	}

	public static ScanTargetsToReferencesSettings getScansToReferencesTransferSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ScanTargetsToReferencesSettings settings = new ScanTargetsToReferencesSettings();
		settings.setUseBestTargetOnly(preferences.getBoolean(P_STTR_USE_BEST_TARGET_ONLY, DEF_STTR_USE_BEST_TARGET_ONLY));
		return settings;
	}
}
