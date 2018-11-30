/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsSelection;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.FilterSettingsTargetTransfer;
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
	public static final String P_TRANSFER_CLOSEST_SCAN = "transferClosestScan";
	public static final boolean DEF_TRANSFER_CLOSEST_SCAN = false;
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
		defaultValues.put(P_TRANSFER_CLOSEST_SCAN, Boolean.toString(DEF_TRANSFER_CLOSEST_SCAN));
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

	public static FilterSettingsTargetTransfer getFilterSettingsTargetTransfer() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		FilterSettingsTargetTransfer filterSettings = new FilterSettingsTargetTransfer();
		filterSettings.setTransferClosestScan(preferences.getBoolean(P_TRANSFER_CLOSEST_SCAN, DEF_TRANSFER_CLOSEST_SCAN));
		return filterSettings;
	}
}
