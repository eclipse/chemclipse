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

	public static String[] getOverlayTypes() {

		return new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TSC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_TSC};
	}
}
