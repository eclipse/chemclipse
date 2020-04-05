/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.jface.preference.IPreferenceStore;

public class OverlayChartSupport {

	public static final String OVERLAY_START_MARKER = "_(";
	public static final String OVERLAY_STOP_MARKER = ")";
	public static final String DELIMITER_SIGNAL_DERIVATIVE = ",";
	//
	private String[] overlayTypes;

	public OverlayChartSupport() {
		initialize();
	}

	public String[] getOverlayTypes() {

		return overlayTypes;
	}

	private void initialize() {

		overlayTypes = new String[]{//
				DisplayType.toShortcut(DisplayType.TIC), //
				DisplayType.toShortcut(DisplayType.BPC), //
				DisplayType.toShortcut(DisplayType.XIC), //
				DisplayType.toShortcut(DisplayType.SIC), //
				DisplayType.toShortcut(DisplayType.XWC), //
				DisplayType.toShortcut(DisplayType.SWC), //
				DisplayType.toShortcut(DisplayType.TSC), //
				DisplayType.toShortcut(DisplayType.TIC, DisplayType.BPC), //
				DisplayType.toShortcut(DisplayType.TIC, DisplayType.XIC), //
				DisplayType.toShortcut(DisplayType.TIC, DisplayType.SIC), //
				DisplayType.toShortcut(DisplayType.TIC, DisplayType.TSC)};
	}

	public double getOverlayShiftX() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getDouble(PreferenceConstants.P_OVERLAY_SHIFT_X);
	}

	public void setOverlayShiftX(double value) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.P_OVERLAY_SHIFT_X, value);
	}

	public int getIndexShiftX() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getInt(PreferenceConstants.P_INDEX_SHIFT_X);
	}

	public void setIndexShiftX(int value) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.P_INDEX_SHIFT_X, value);
	}

	public double getOverlayShiftY() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getDouble(PreferenceConstants.P_OVERLAY_SHIFT_Y);
	}

	public void setOverlayShiftY(double value) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.P_OVERLAY_SHIFT_Y, value);
	}

	public int getIndexShiftY() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getInt(PreferenceConstants.P_INDEX_SHIFT_Y);
	}

	public void setIndexShiftY(int value) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.P_INDEX_SHIFT_Y, value);
	}
}
