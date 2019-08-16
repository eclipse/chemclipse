/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsShift;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsStretch;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ShiftDirection;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SHIFT_ALL_SCANS = "shiftAllScans";
	public static final boolean DEF_SHIFT_ALL_SCANS = true;
	//
	public static final String P_MILLISECONDS_BACKWARD = "millisecondsBackward";
	public static final int DEF_MILLISECONDS_BACKWARD = 500; // IntegerFieldEditor doesn't allow negative values
	public static final String P_MILLISECONDS_FAST_BACKWARD = "millisecondsFastBackward";
	public static final int DEF_MILLISECONDS_FAST_BACKWARD = 1500; // IntegerFieldEditor doesn't allow negative values
	public static final String P_MILLISECONDS_FORWARD = "millisecondsForward";
	public static final int DEF_MILLISECONDS_FORWARD = 500;
	public static final String P_MILLISECONDS_FAST_FORWARD = "millisecondsFastForward";
	public static final int DEF_MILLISECONDS_FAST_FORWARD = 1500;
	public static final int SHIFT_MILLISECONDS_MIN = 0;
	public static final int SHIFT_MILLISECONDS_MAX = Integer.MAX_VALUE;
	//
	public static final String P_DEFAULT_SHIFT_DIRECTION = "defaultShiftDirection";
	public static final String DEF_DEFAULT_SHIFT_DIRECTION = ShiftDirection.FORWARD.toString();
	//
	public static final int MIN_X_OFFSET = -6000000; // = -100.0 minutes
	public static final int MIN_RETENTION_TIME = 0; // = 0.0 minutes
	public static final int MAX_RETENTION_TIME = 6000000; // = 100.0 minutes;
	//
	public static final String P_OVERLAY_X_OFFSET = "overlayXOffset";
	public static final int DEF_OVERLAY_X_OFFSET = 0;
	public static final String P_OVERLAY_Y_OFFSET = "overlayYOffset";
	public static final int DEF_OVERLAY_Y_OFFSET = 0;
	//
	public static final String P_OFFSET_STEP_DOWN = "offsetStepUp";
	public static final int DEF_OFFSET_STEP_DOWN = 500000;
	public static final String P_OFFSET_STEP_UP = "offsetStepDown";
	public static final int DEF_OFFSET_STEP_UP = 500000;
	//
	public static final String P_IS_LOCK_OFFSET = "isLockOffset";
	public static final boolean DEF_IS_LOCK_OFFSET = false;
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
		defaultValues.put(P_DEFAULT_SHIFT_DIRECTION, DEF_DEFAULT_SHIFT_DIRECTION);
		defaultValues.put(P_MILLISECONDS_BACKWARD, Integer.toString(DEF_MILLISECONDS_BACKWARD));
		defaultValues.put(P_MILLISECONDS_FAST_BACKWARD, Integer.toString(DEF_MILLISECONDS_FAST_BACKWARD));
		defaultValues.put(P_MILLISECONDS_FORWARD, Integer.toString(DEF_MILLISECONDS_FORWARD));
		defaultValues.put(P_MILLISECONDS_FAST_FORWARD, Integer.toString(DEF_MILLISECONDS_FAST_FORWARD));
		//
		defaultValues.put(P_OVERLAY_X_OFFSET, Integer.toString(DEF_OVERLAY_X_OFFSET));
		defaultValues.put(P_OVERLAY_Y_OFFSET, Integer.toString(DEF_OVERLAY_Y_OFFSET));
		//
		defaultValues.put(P_OFFSET_STEP_DOWN, Integer.toString(DEF_OFFSET_STEP_DOWN));
		defaultValues.put(P_OFFSET_STEP_UP, Integer.toString(DEF_OFFSET_STEP_UP));
		//
		defaultValues.put(P_IS_LOCK_OFFSET, Boolean.toString(DEF_IS_LOCK_OFFSET));
		//
		defaultValues.put(P_STRETCH_MILLISECONDS_SCAN_DELAY, Integer.toString(DEF_STRETCH_MILLISECONDS_SCAN_DELAY));
		defaultValues.put(P_STRETCH_MILLISECONDS_LENGTH, Integer.toString(DEF_STRETCH_MILLISECONDS_LENGTH));
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettingsShift getFilterSettingsShift() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ShiftDirection defaultShiftDirection = ShiftDirection.valueOf(preferences.get(P_DEFAULT_SHIFT_DIRECTION, DEF_DEFAULT_SHIFT_DIRECTION));
		boolean isShiftAllScans = getIsShiftAllScans();
		int millisecondsToShift = getMillisecondsToShift(preferences, defaultShiftDirection);
		FilterSettingsShift filterSettings = new FilterSettingsShift(millisecondsToShift, isShiftAllScans);
		return filterSettings;
	}

	public static FilterSettingsStretch getFilterSettingsStretch() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		FilterSettingsStretch filterSettings = new FilterSettingsStretch(preferences.getInt(P_STRETCH_MILLISECONDS_LENGTH, DEF_STRETCH_MILLISECONDS_LENGTH));
		filterSettings.setScanDelay(preferences.getInt(P_STRETCH_MILLISECONDS_SCAN_DELAY, DEF_STRETCH_MILLISECONDS_SCAN_DELAY));
		return filterSettings;
	}

	public static boolean getIsShiftAllScans() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_SHIFT_ALL_SCANS, DEF_SHIFT_ALL_SCANS);
	}

	private static int getMillisecondsToShift(IEclipsePreferences preferences, ShiftDirection defaultShiftDirection) {

		int millisecondsToShift;
		/*
		 * Better use another solution, see Open/Close principle, but it's ok for now.
		 */
		switch(defaultShiftDirection) {
			case BACKWARD:
				millisecondsToShift = preferences.getInt(P_MILLISECONDS_BACKWARD, DEF_MILLISECONDS_BACKWARD);
				/*
				 * IntegerFieldEditor doesn't allow negative values
				 */
				if(millisecondsToShift > 0) {
					millisecondsToShift *= -1;
				}
				break;
			case FAST_BACKWARD:
				millisecondsToShift = preferences.getInt(P_MILLISECONDS_FAST_BACKWARD, DEF_MILLISECONDS_FAST_BACKWARD);
				/*
				 * IntegerFieldEditor doesn't allow negative values
				 */
				if(millisecondsToShift > 0) {
					millisecondsToShift *= -1;
				}
				break;
			case FORWARD:
				millisecondsToShift = preferences.getInt(P_MILLISECONDS_FORWARD, DEF_MILLISECONDS_FORWARD);
				/*
				 * Avoid negative values.
				 */
				if(millisecondsToShift < 0) {
					millisecondsToShift *= -1;
				}
				break;
			case FAST_FORWARD:
				millisecondsToShift = preferences.getInt(P_MILLISECONDS_FAST_FORWARD, DEF_MILLISECONDS_FAST_FORWARD);
				/*
				 * Avoid negative values.
				 */
				if(millisecondsToShift < 0) {
					millisecondsToShift *= -1;
				}
				break;
			default:
				millisecondsToShift = preferences.getInt(P_MILLISECONDS_FORWARD, DEF_MILLISECONDS_FORWARD);
		}
		return millisecondsToShift;
	}

	public static int getMillisecondsToShiftBackward() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int millisecondsToShift = preferences.getInt(P_MILLISECONDS_BACKWARD, DEF_MILLISECONDS_BACKWARD);
		/*
		 * IntegerFieldEditor doesn't allow negative values
		 */
		if(millisecondsToShift > 0) {
			millisecondsToShift *= -1;
		}
		return millisecondsToShift;
	}

	public static int getMillisecondsToShiftFastBackward() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int millisecondsToShift = preferences.getInt(P_MILLISECONDS_FAST_BACKWARD, DEF_MILLISECONDS_FAST_BACKWARD);
		/*
		 * IntegerFieldEditor doesn't allow negative values
		 */
		if(millisecondsToShift > 0) {
			millisecondsToShift *= -1;
		}
		return millisecondsToShift;
	}

	public static int getMillisecondsToShiftForward() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int millisecondsToShift = preferences.getInt(P_MILLISECONDS_FORWARD, DEF_MILLISECONDS_FORWARD);
		/*
		 * Avoid negative values.
		 */
		if(millisecondsToShift < 0) {
			millisecondsToShift *= -1;
		}
		return millisecondsToShift;
	}

	public static int getMillisecondsToShiftFastForward() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int millisecondsToShift = preferences.getInt(P_MILLISECONDS_FAST_FORWARD, DEF_MILLISECONDS_FAST_FORWARD);
		/*
		 * Avoid negative values.
		 */
		if(millisecondsToShift < 0) {
			millisecondsToShift *= -1;
		}
		return millisecondsToShift;
	}

	/**
	 * Returns the x offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayXOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET);
	}

	/**
	 * Returns the y offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayYOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getInt(P_OVERLAY_Y_OFFSET, DEF_OVERLAY_Y_OFFSET);
	}

	public static void resetOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.putInt(P_OVERLAY_X_OFFSET, 0);
		preferences.putInt(P_OVERLAY_Y_OFFSET, 0);
	}

	public static void decreaseXOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET) - preferences.getInt(P_MILLISECONDS_BACKWARD, DEF_MILLISECONDS_BACKWARD);
		preferences.putInt(P_OVERLAY_X_OFFSET, value);
	}

	public static void decreaseXOffsetFast() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET) - preferences.getInt(P_MILLISECONDS_FAST_BACKWARD, DEF_MILLISECONDS_FAST_BACKWARD);
		preferences.putInt(P_OVERLAY_X_OFFSET, value);
	}

	public static void increaseXOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET) + preferences.getInt(P_MILLISECONDS_FORWARD, DEF_MILLISECONDS_FORWARD);
		preferences.putInt(P_OVERLAY_X_OFFSET, value);
	}

	public static void increaseXOffsetFast() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET) + preferences.getInt(P_MILLISECONDS_FAST_FORWARD, DEF_MILLISECONDS_FAST_FORWARD);
		preferences.putInt(P_OVERLAY_X_OFFSET, value);
	}

	public static void decreaseYOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_Y_OFFSET, DEF_OVERLAY_Y_OFFSET) - preferences.getInt(P_OFFSET_STEP_DOWN, DEF_OFFSET_STEP_DOWN);
		preferences.putInt(P_OVERLAY_Y_OFFSET, value);
	}

	public static void increaseYOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_Y_OFFSET, DEF_OVERLAY_Y_OFFSET) + preferences.getInt(P_OFFSET_STEP_UP, DEF_OFFSET_STEP_UP);
		preferences.putInt(P_OVERLAY_Y_OFFSET, value);
	}

	public static boolean isLockOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_IS_LOCK_OFFSET, DEF_IS_LOCK_OFFSET);
	}

	public static void toggleLockOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		boolean value = preferences.getBoolean(P_IS_LOCK_OFFSET, DEF_IS_LOCK_OFFSET);
		preferences.putBoolean(P_IS_LOCK_OFFSET, !value);
	}

	public static void setStretchScanDelay(int scanDelay) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.putInt(P_STRETCH_MILLISECONDS_SCAN_DELAY, scanDelay);
	}

	public static void setStretchLength(int length) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		preferences.putInt(P_STRETCH_MILLISECONDS_LENGTH, length);
	}
}
