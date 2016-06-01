/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ShiftDirection;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SHIFT_ALL_SCANS = "shiftAllScans";
	public static final boolean DEF_SHIFT_ALL_SCANS = true;
	//
	public static final String P_MILLISECONDS_BACKWARD = "millisecondsBackward";
	public static final String P_MILLISECONDS_FAST_BACKWARD = "millisecondsFastBackward";
	public static final String P_MILLISECONDS_FORWARD = "millisecondsForward";
	public static final String P_MILLISECONDS_FAST_FORWARD = "millisecondsFastForward";
	public static final String P_DEFAULT_SHIFT_DIRECTION = "defaultShiftDirection";
	public static final int DEF_MILLISECONDS_BACKWARD = 500; // IntegerFieldEditor doesn't allow negative values
	public static final int DEF_MILLISECONDS_FAST_BACKWARD = 1500; // IntegerFieldEditor doesn't allow negative values
	public static final int DEF_MILLISECONDS_FORWARD = 500;
	public static final int DEF_MILLISECONDS_FAST_FORWARD = 1500;
	public static final String DEF_DEFAULT_SHIFT_DIRECTION = ShiftDirection.FORWARD.toString();
	//
	public static final int MIN_RETENTION_TIME = 0; // = 0.0 minutes
	public static final int MAX_RETENTION_TIME = 6000000; // = 100.0 minutes;
	//
	public static final String P_OVERLAY_X_OFFSET = "overlayXOffset";
	public static final int DEF_OVERLAY_X_OFFSET = 0;
	public static final String P_OVERLAY_Y_OFFSET = "overlayYOffset";
	public static final int DEF_OVERLAY_Y_OFFSET = 0;
	//
	public static final String P_OFFSET_STEP_LEFT = "offsetStepLeft";
	public static final int DEF_OFFSET_STEP_LEFT = 25000;
	public static final String P_OFFSET_STEP_RIGHT = "offsetStepRight";
	public static final int DEF_OFFSET_STEP_RIGHT = 25000;
	public static final String P_OFFSET_STEP_DOWN = "offsetStepUp";
	public static final int DEF_OFFSET_STEP_DOWN = 500000;
	public static final String P_OFFSET_STEP_UP = "offsetStepDown";
	public static final int DEF_OFFSET_STEP_UP = 500000;
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
		defaultValues.put(P_OFFSET_STEP_LEFT, Integer.toString(DEF_OFFSET_STEP_LEFT));
		defaultValues.put(P_OFFSET_STEP_RIGHT, Integer.toString(DEF_OFFSET_STEP_RIGHT));
		defaultValues.put(P_OFFSET_STEP_DOWN, Integer.toString(DEF_OFFSET_STEP_DOWN));
		defaultValues.put(P_OFFSET_STEP_UP, Integer.toString(DEF_OFFSET_STEP_UP));
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns the chromatogram filter settings.
	 * 
	 * @return IChromatogramFilterSettings
	 */
	public static ISupplierFilterSettings getChromatogramFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		/*
		 * Set the properties.
		 */
		ShiftDirection defaultShiftDirection = ShiftDirection.valueOf(preferences.get(P_DEFAULT_SHIFT_DIRECTION, DEF_DEFAULT_SHIFT_DIRECTION));
		boolean isShiftAllScans = getIsShiftAllScans();
		int millisecondsToShift = getMillisecondsToShift(preferences, defaultShiftDirection);
		ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings(millisecondsToShift, isShiftAllScans);
		return chromatogramFilterSettings;
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
		int value = preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET) - preferences.getInt(P_OFFSET_STEP_LEFT, DEF_OFFSET_STEP_LEFT);
		preferences.putInt(P_OVERLAY_X_OFFSET, value);
	}

	public static void increaseXOffset() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		int value = preferences.getInt(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET) + preferences.getInt(P_OFFSET_STEP_RIGHT, DEF_OFFSET_STEP_RIGHT);
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
}
