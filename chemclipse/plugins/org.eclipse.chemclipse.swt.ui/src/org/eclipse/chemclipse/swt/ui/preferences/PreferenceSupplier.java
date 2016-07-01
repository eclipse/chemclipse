/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.preferences;

import org.eclipse.chemclipse.swt.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class PreferenceSupplier {

	private static final String RGB_DELIMITER = ",";
	private static Color positionMarkerBackgroundColor;
	private static Color positionMarkerForegroundColor;
	private static Color chromatogramColor;
	private static Color massSpectrumColor;

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {
	}

	public static boolean showChromatogramPositionMarkerBox() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_POSITION_MARKER_BOX);
	}

	/**
	 * Returns the x offset value.
	 * 
	 * @return int
	 */
	public static Color getPositionMarkerBackgroundColor() throws NumberFormatException {

		Color color = getColor(PreferenceConstants.P_POSITION_MARKER_BACKGROUND_COLOR);
		if(!color.equals(positionMarkerBackgroundColor)) {
			/*
			 * Dispose the color.
			 */
			if(positionMarkerBackgroundColor != null) {
				positionMarkerBackgroundColor.dispose();
			}
			/*
			 * Set the new color.
			 */
			positionMarkerBackgroundColor = color;
		}
		return positionMarkerBackgroundColor;
	}

	public static Color getPositionMarkerForegroundColor() {

		Color color = getColor(PreferenceConstants.P_POSITION_MARKER_FOREGROUND_COLOR);
		if(!color.equals(positionMarkerForegroundColor)) {
			/*
			 * Dispose the color.
			 */
			if(positionMarkerForegroundColor != null) {
				positionMarkerForegroundColor.dispose();
			}
			/*
			 * Set the new color.
			 */
			positionMarkerForegroundColor = color;
		}
		return positionMarkerForegroundColor;
	}

	public static boolean showSelectedPeakInEditor() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_SELECTED_PEAK_IN_EDITOR);
	}

	public static boolean showScansOfSelectedPeak() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_SCANS_OF_SELECTED_PEAK);
	}

	public static int sizeOfPeakScanMarker() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_SIZE_OF_PEAK_SCAN_MARKER);
	}

	public static boolean showBackgroundInChromatogramEditor() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_BACKGROUND_IN_CHROMATOGRAM_EDITOR);
	}

	public static boolean showChromatogramArea() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_AREA);
	}

	public static int getScanDisplayNumberOfIons() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_SCAN_DISPLAY_NUMBER_OF_IONS);
	}

	public static boolean isUseModuloDisplayNumberOfIons() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_USE_MODULO_DISPLAY_OF_IONS);
	}

	public static Color getChromatogramColor() {

		Color color = getColor(PreferenceConstants.P_COLOR_CHROMATOGRAM);
		if(!color.equals(chromatogramColor)) {
			/*
			 * Dispose the color.
			 */
			if(chromatogramColor != null) {
				chromatogramColor.dispose();
			}
			/*
			 * Set the new color.
			 */
			chromatogramColor = color;
		}
		return chromatogramColor;
	}

	public static Color getMassSpectrumColor() {

		Color color = getColor(PreferenceConstants.P_COLOR_MASS_SPECTRUM);
		if(!color.equals(massSpectrumColor)) {
			/*
			 * Dispose the color.
			 */
			if(massSpectrumColor != null) {
				massSpectrumColor.dispose();
			}
			/*
			 * Set the new color.
			 */
			massSpectrumColor = color;
		}
		return massSpectrumColor;
	}

	public static String getColorSchemeOverlay() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(PreferenceConstants.P_COLOR_SCHEME_OVERLAY);
	}

	public static boolean autoAdjustEditorIntensityDisplay() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_AUTO_ADJUST_EDITOR_INTENSITY_DISPLAY);
	}

	public static boolean showChromatogramHairlineDivider() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_HAIRLINE_DIVIDER);
	}

	public static boolean condenseCycleNumberScans() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS);
	}

	public static boolean showRetentionIndexWithoutDecimals() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
	}

	public static boolean showAreaWithoutDecimals() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS);
	}

	public static boolean isMoveRetentionTimeOnPeakSelection() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
	}

	public static boolean autoAdjustViewIntensityDisplay() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_AUTO_ADJUST_VIEW_INTENSITY_DISPLAY);
	}

	public static boolean useAlternateWindowMoveDirection() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION);
	}

	public static boolean isShowMilliseconds() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_AXIS_MILLISECONDS);
	}

	public static boolean isShowRelativeIntensity() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(PreferenceConstants.P_SHOW_AXIS_RELATIVE_INTENSITY);
	}

	private static Color getColor(String preference) throws NumberFormatException {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String colorValues = store.getString(preference);
		String[] rgbValues = colorValues.split(RGB_DELIMITER);
		RGB rgb;
		int red = Integer.valueOf(rgbValues[0].trim());
		int green = Integer.valueOf(rgbValues[1].trim());
		int blue = Integer.valueOf(rgbValues[2].trim());
		rgb = new RGB(red, green, blue);
		return new Color(Display.getCurrent(), rgb);
	}
}
