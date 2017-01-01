/*******************************************************************************
 * Copyright (c) 2011, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.preferences;

import org.eclipse.chemclipse.swt.ui.support.Colors;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String P_SHOW_CHROMATOGRAM_POSITION_MARKER_BOX = "showChromatogramSelectionPositionBox";
	public static final String P_POSITION_MARKER_BACKGROUND_COLOR = "positionMarkerBackgroundColor";
	public static final String P_POSITION_MARKER_FOREGROUND_COLOR = "positionMarkerForegroundColor";
	//
	public static final String P_SHOW_SELECTED_PEAK_IN_EDITOR = "showSelectedPeakInEditor";
	public static final String P_SHOW_SCANS_OF_SELECTED_PEAK = "showScansOfSelectedPeak";
	public static final String P_SIZE_OF_PEAK_SCAN_MARKER = "sizeOfPeakScanMarker";
	//
	public static final String P_SHOW_BACKGROUND_IN_CHROMATOGRAM_EDITOR = "showBackgroundInEditor";
	public static final String P_SHOW_CHROMATOGRAM_AREA = "showChromatogramArea";
	//
	public static final String P_SCAN_DISPLAY_NUMBER_OF_IONS = "scanDisplayNumberOfIons";
	public static final String P_USE_MODULO_DISPLAY_OF_IONS = "useModuloDisplayOfIons";
	//
	public static final String P_COLOR_CHROMATOGRAM = "colorChromatogram";
	public static final String P_COLOR_MASS_SPECTRUM = "colorMassSpectrum";
	public static final String P_COLOR_SCHEME_OVERLAY = "colorSchemeOverlay";
	public static final String DEF_COLOR_SCHEME_OVERLAY = Colors.COLOR_SCHEME_RED;
	//
	public static final String P_SHOW_AXIS_MILLISECONDS = "showAxisMilliseconds";
	public static final String P_SHOW_AXIS_RELATIVE_INTENSITY = "showAxisRelativeIntensity";
	//
	public static final String P_AUTO_ADJUST_EDITOR_INTENSITY_DISPLAY = "autoAdjustEditorIntensityDisplay";
	public static final String P_AUTO_ADJUST_VIEW_INTENSITY_DISPLAY = "autoAdjustViewIntensityDisplay";
	public static final String P_ALTERNATE_WINDOW_MOVE_DIRECTION = "useAlternateWindowMoveDirection";
	//
	public static final String P_SHOW_CHROMATOGRAM_HAIRLINE_DIVIDER = "drawChromatogramHairlineDivider";
	//
	public static final String P_CONDENSE_CYCLE_NUMBER_SCANS = "condenseCycleNumberScans";
	//
	public static final String P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS = "showRetentionIndexWithoutDecimals";
	public static final String P_SHOW_AREA_WITHOUT_DECIMALS = "showAreaWithoutDecimals";
	//
	public static final String P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION = "moveRetentionTimeOnPeakSelection";
	//
	public static final String P_PATH_MASS_SPECTRUM_LIBRARIES = "pathMassSpectrumLibraries";
}
