/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.jface.preference.IPreferenceStore;

public class OverlayChartSupport {

	public static final String ESCAPE_CONCATENATOR = "\\";
	public static final String SELECTED_IONS_CONCATENATOR = " ";
	public static final String SELECTED_WAVELENGTH_CONCATENATOR = " ";
	public static final String OVERLAY_START_MARKER = "_(";
	public static final String OVERLAY_STOP_MARKER = ")";
	public static final String DELIMITER_SIGNAL_DERIVATIVE = ",";
	//
	public static final String SELECTED_IONS_USERS_CHOICE = "Users Choice";
	public static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	public static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	public static final String SELECTED_IONS_FAME = "FAME";
	public static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	public static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	public static String[][] SELECTED_IONS_CHOICES = new String[][]{//
			{"Users Choice", SELECTED_IONS_USERS_CHOICE}, //
			{"Hydrocarbons", SELECTED_IONS_HYDROCARBONS}, //
			{"Fatty Acids", SELECTED_IONS_FATTY_ACIDS}, //
			{"FAME", SELECTED_IONS_FAME}, //
			{"Solvent Tailing", SELECTED_IONS_SOLVENT_TAILING}, //
			{"Column Bleed", SELECTED_IONS_COLUMN_BLEED}//
	};
	//
	public static final String DISPLAY_MODUS_NORMAL = "Normal";
	public static final String DISPLAY_MODUS_MIRRORED = "Mirrored";
	//
	private String[] overlayTypes;
	private Map<String, String> overlayTypeTooltips;
	private String[] derivativeTypes;
	private String[] selectedIons;
	private String[] displayModi;
	//

	public OverlayChartSupport() {
		initialize();
	}

	public String[] getOverlayTypes() {

		return overlayTypes;
	}

	public String getOverlayTypeTooltips(String overlayType) {

		return overlayTypeTooltips.getOrDefault(overlayType, "");
	}

	public String[] getDerivativeTypes() {

		return derivativeTypes;
	}

	public String[] getSelectedIons() {

		return selectedIons;
	}

	public String[] getDisplayModi() {

		return displayModi;
	}

	private void initialize() {

		overlayTypes = new String[]{//
				ChromatogramChartSupport.DISPLAY_TYPE_TIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_BPC, //
				ChromatogramChartSupport.DISPLAY_TYPE_XIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_SIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_XWC, //
				ChromatogramChartSupport.DISPLAY_TYPE_SWC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TSC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC_BPC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC_XIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC_SIC, //
				ChromatogramChartSupport.DISPLAY_TYPE_TIC_TSC};
		//
		overlayTypeTooltips = new HashMap<String, String>();
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_TIC, ChromatogramChartSupport.DISPLAY_TYPE_TIC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_BPC, ChromatogramChartSupport.DISPLAY_TYPE_BPC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_XIC, ChromatogramChartSupport.DISPLAY_TYPE_XIC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_SIC, ChromatogramChartSupport.DISPLAY_TYPE_SIC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_XWC, ChromatogramChartSupport.DISPLAY_TYPE_XWC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_SWC, ChromatogramChartSupport.DISPLAY_TYPE_SWC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_TSC, ChromatogramChartSupport.DISPLAY_TYPE_TSC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_TIC_BPC, ChromatogramChartSupport.DISPLAY_TYPE_TIC_BPC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_TIC_XIC, ChromatogramChartSupport.DISPLAY_TYPE_TIC_XIC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_TIC_SIC, ChromatogramChartSupport.DISPLAY_TYPE_TIC_SIC_DESCRIPTION);
		overlayTypeTooltips.put(ChromatogramChartSupport.DISPLAY_TYPE_TIC_TSC, ChromatogramChartSupport.DISPLAY_TYPE_TIC_TSC_DESCRIPTION);
		//
		derivativeTypes = new String[]{//
				ChromatogramChartSupport.DERIVATIVE_NONE, //
				ChromatogramChartSupport.DERIVATIVE_FIRST, //
				ChromatogramChartSupport.DERIVATIVE_SECOND, //
				ChromatogramChartSupport.DERIVATIVE_THIRD};
		//
		selectedIons = new String[]{//
				SELECTED_IONS_USERS_CHOICE, //
				SELECTED_IONS_HYDROCARBONS, //
				SELECTED_IONS_FATTY_ACIDS, //
				SELECTED_IONS_FAME, //
				SELECTED_IONS_SOLVENT_TAILING, //
				SELECTED_IONS_COLUMN_BLEED};
		//
		displayModi = new String[]{//
				DISPLAY_MODUS_NORMAL, //
				DISPLAY_MODUS_MIRRORED //
		};
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
