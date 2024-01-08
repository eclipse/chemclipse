/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsGapFiller;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsShift;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsStretch;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	//
	public static final int MIN_MILLISECONDS_SHIFT = Integer.MIN_VALUE;
	public static final int MAX_MILLISECONDS_SHIFT = Integer.MAX_VALUE;
	//
	public static final String P_MILLISECONDS_SHIFT = "millisecondsShift";
	public static final int DEF_MILLISECONDS_SHIFT = 0;
	public static final String P_SHIFT_ALL_SCANS = "shiftAllScans";
	public static final boolean DEF_SHIFT_ALL_SCANS = true;
	//
	public static final int MIN_LIMIT_FACTOR = 4;
	public static final int MAX_LIMIT_FACTOR = 10000000;
	//
	public static final String P_STRETCH_MILLISECONDS_SCAN_DELAY = "stretchMillisecondsScanDelay";
	public static final int DEF_STRETCH_MILLISECONDS_SCAN_DELAY = 0;
	public static final int STRETCH_MILLISECONDS_SCAN_DELAY_MIN = 0;
	public static final int STRETCH_MILLISECONDS_SCAN_DELAY_MAX = Integer.MAX_VALUE;
	//
	public static final String P_STRETCH_MILLISECONDS_LENGTH = "stretchMillisecondsLength";
	public static final int DEF_STRETCH_MILLISECONDS_LENGTH = 6000000; // = 100.0 minutes;
	public static final int STRETCH_MILLISECONDS_LENGTH_MIN = 0;
	public static final int STRETCH_MILLISECONDS_LENGTH_MAX = Integer.MAX_VALUE;
	//
	public static final String P_LIMIT_FACTOR = "limitFactor";
	public static final int DEF_LIMIT_FACTOR = 4;
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
		defaultValues.put(P_SHIFT_ALL_SCANS, Boolean.toString(DEF_SHIFT_ALL_SCANS));
		defaultValues.put(P_MILLISECONDS_SHIFT, Integer.toString(DEF_MILLISECONDS_SHIFT));
		defaultValues.put(P_STRETCH_MILLISECONDS_SCAN_DELAY, Integer.toString(DEF_STRETCH_MILLISECONDS_SCAN_DELAY));
		defaultValues.put(P_STRETCH_MILLISECONDS_LENGTH, Integer.toString(DEF_STRETCH_MILLISECONDS_LENGTH));
		defaultValues.put(P_LIMIT_FACTOR, Integer.toString(DEF_LIMIT_FACTOR));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettingsShift getFilterSettingsShift() {

		boolean isShiftAllScans = isShiftAllScans();
		int millisecondsShift = getMillisecondsShift();
		FilterSettingsShift settings = new FilterSettingsShift(millisecondsShift, isShiftAllScans);
		return settings;
	}

	public static FilterSettingsGapFiller getFilterSettingsFillGaps() {

		FilterSettingsGapFiller settings = new FilterSettingsGapFiller();
		settings.setLimitFactor(INSTANCE().getInteger(P_LIMIT_FACTOR, DEF_LIMIT_FACTOR));
		return settings;
	}

	public static FilterSettingsStretch getFilterSettingsStretch() {

		FilterSettingsStretch settings = new FilterSettingsStretch(INSTANCE().getInteger(P_STRETCH_MILLISECONDS_LENGTH, DEF_STRETCH_MILLISECONDS_LENGTH));
		settings.setScanDelay(INSTANCE().getInteger(P_STRETCH_MILLISECONDS_SCAN_DELAY, DEF_STRETCH_MILLISECONDS_SCAN_DELAY));
		return settings;
	}

	public static boolean isShiftAllScans() {

		return INSTANCE().getBoolean(P_SHIFT_ALL_SCANS, DEF_SHIFT_ALL_SCANS);
	}

	private static int getMillisecondsShift() {

		return INSTANCE().getInteger(P_MILLISECONDS_SHIFT, DEF_MILLISECONDS_SHIFT);
	}

	public static void setStretchScanDelay(int scanDelay) {

		INSTANCE().putInteger(P_STRETCH_MILLISECONDS_SCAN_DELAY, scanDelay);
	}

	public static void setStretchLength(int length) {

		INSTANCE().putInteger(P_STRETCH_MILLISECONDS_LENGTH, length);
	}
}