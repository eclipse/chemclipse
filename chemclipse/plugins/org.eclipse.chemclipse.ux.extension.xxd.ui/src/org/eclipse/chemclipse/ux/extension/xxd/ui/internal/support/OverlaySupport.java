/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

public class OverlaySupport {

	/*
	 * Overlay Type
	 */
	public static final String OVERLAY_TYPE_TIC = "TIC"; // Total Intensity Chromatogram
	public static final String OVERLAY_TYPE_BPC = "BPC"; // Base Peak Chromatogram
	public static final String OVERLAY_TYPE_XIC = "XIC"; // Extracted Ion Chromatogram
	public static final String OVERLAY_TYPE_SIC = "SIC"; // Selected Ion Chromatogram
	public static final String OVERLAY_TYPE_TSC = "TSC"; // Total Substracted Chromatogram
	// public static final String OVERLAY_TYPE_SRM = "SRM"; // Single Reaction Monitoring
	// public static final String OVERLAY_TYPE_MRM = "MRM"; // Single Reaction Monitoring
	/*
	 * Delimiters
	 */
	public static final String OVERLAY_TYPE_CONCATENATOR = "+";
	public static final String ESCAPE_CONCATENATOR = "\\";
	public static final String SELECTED_IONS_CONCATENATOR = " ";
	public static final String EDITOR_TAB = "_EditorTab#";
	public static final String OVERLAY_START_MARKER = "_(";
	public static final String OVERLAY_STOP_MARKER = ")";
	public static final String DELIMITER_ION_DERIVATIVE = ",";
	//
	public static final String DERIVATIVE_NONE = "--";
	public static final String DERIVATIVE_FIRST = "1st";
	public static final String DERIVATIVE_SECOND = "2nd";
	public static final String DERIVATIVE_THIRD = "3rd";
	//
	public static final String SELECTED_IONS_DEFAULT = "18 28 32 84 207";
	public static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	public static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	public static final String SELECTED_IONS_FAME = "FAME";
	public static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	public static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	public static final String DISPLAY_MODUS_NORMAL = "Normal";
	public static final String DISPLAY_MODUS_MIRRORED = "Mirrored";
	//
	private String[] overlayTypes;
	private String[] derivativeTypes;
	private String[] selectedIons;
	private String[] displayModi;
	private Map<String, String> selectedIonsMap;

	public OverlaySupport() {
		initialize();
	}

	public String[] getOverlayTypes() {

		return overlayTypes;
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

	public Map<String, String> getSelectedIonsMap() {

		return selectedIonsMap;
	}

	private void initialize() {

		overlayTypes = new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TSC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_TSC};
		//
		derivativeTypes = new String[]{//
				DERIVATIVE_NONE, //
				DERIVATIVE_FIRST, //
				DERIVATIVE_SECOND, //
				DERIVATIVE_THIRD};
		//
		selectedIons = new String[]{//
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
		//
		selectedIonsMap = new HashMap<String, String>();
		selectedIonsMap.put(SELECTED_IONS_HYDROCARBONS, "57 71 85");
		selectedIonsMap.put(SELECTED_IONS_FATTY_ACIDS, "74 84");
		selectedIonsMap.put(SELECTED_IONS_FAME, "79 81");
		selectedIonsMap.put(SELECTED_IONS_SOLVENT_TAILING, "84");
		selectedIonsMap.put(SELECTED_IONS_COLUMN_BLEED, "207");
	}
}
