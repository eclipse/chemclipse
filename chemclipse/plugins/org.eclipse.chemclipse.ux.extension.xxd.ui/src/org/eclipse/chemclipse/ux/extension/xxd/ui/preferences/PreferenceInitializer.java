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
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		/*
		 * General
		 */
		store.setDefault(PreferenceConstants.P_STACK_POSITION_OVERLAY, PreferenceConstants.DEF_STACK_POSITION_OVERLAY);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_TARGETS, PreferenceConstants.DEF_STACK_POSITION_TARGETS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SCAN_CHART, PreferenceConstants.DEF_STACK_POSITION_SCAN_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE, PreferenceConstants.DEF_STACK_POSITION_SCAN_TABLE);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_CHART, PreferenceConstants.DEF_STACK_POSITION_PEAK_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS, PreferenceConstants.DEF_STACK_POSITION_PEAK_DETAILS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR, PreferenceConstants.DEF_STACK_POSITION_PEAK_DETECTOR);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_LIST, PreferenceConstants.DEF_STACK_POSITION_PEAK_LIST);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_SUBTRACT_SCAN_PART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_COMBINED_SCAN_PART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART, PreferenceConstants.DEF_STACK_POSITION_COMPARISON_SCAN_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANTITATION, PreferenceConstants.DEF_STACK_POSITION_QUANTITATION);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA, PreferenceConstants.DEF_STACK_POSITION_INTEGRATION_AREA);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_INTERNAL_STANDARDS, PreferenceConstants.DEF_STACK_POSITION_INTERNAL_STANDARDS);
		/*
		 * Overlay
		 */
		store.setDefault(PreferenceConstants.P_MINUTES_SHIFT_X, PreferenceConstants.DEF_MINUTES_SHIFT_X);
		store.setDefault(PreferenceConstants.P_ABSOLUTE_SHIFT_Y, PreferenceConstants.DEF_ABSOLUTE_SHIFT_Y);
		/*
		 * Scans
		 */
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_FONT_NAME, PreferenceConstants.DEF_SCAN_LABEL_FONT_NAME);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_FONT_SIZE, PreferenceConstants.DEF_SCAN_LABEL_FONT_SIZE);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_FONT_STYLE, PreferenceConstants.DEF_SCAN_LABEL_FONT_STYLE);
		store.setDefault(PreferenceConstants.P_COLOR_SCAN_1, PreferenceConstants.DEF_COLOR_SCAN_1);
		store.setDefault(PreferenceConstants.P_COLOR_SCAN_2, PreferenceConstants.DEF_COLOR_SCAN_2);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_HIGHEST_INTENSITIES, PreferenceConstants.DEF_SCAN_LABEL_HIGHEST_INTENSITIES);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_MODULO_INTENSITIES, PreferenceConstants.DEF_SCAN_LABEL_MODULO_INTENSITIES);
		store.setDefault(PreferenceConstants.P_AUTOFOCUS_SUBTRACT_SCAN_PART, PreferenceConstants.DEF_AUTOFOCUS_SUBTRACT_SCAN_PART);
		/*
		 * Peaks
		 */
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_BACKGROUND, PreferenceConstants.DEF_SHOW_PEAK_BACKGROUND);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_BACKGROUND, PreferenceConstants.DEF_COLOR_PEAK_BACKGROUND);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_BASELINE, PreferenceConstants.DEF_SHOW_PEAK_BASELINE);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_BASELINE, PreferenceConstants.DEF_COLOR_PEAK_BASELINE);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK, PreferenceConstants.DEF_SHOW_PEAK);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_1, PreferenceConstants.DEF_COLOR_PEAK_1);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_2, PreferenceConstants.DEF_COLOR_PEAK_2);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_TANGENTS, PreferenceConstants.DEF_SHOW_PEAK_TANGENTS);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_TANGENTS, PreferenceConstants.DEF_COLOR_PEAK_TANGENTS);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_WIDTH_0, PreferenceConstants.DEF_SHOW_PEAK_WIDTH_0);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_WIDTH_0, PreferenceConstants.DEF_COLOR_PEAK_WIDTH_0);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_WIDTH_50, PreferenceConstants.DEF_SHOW_PEAK_WIDTH_50);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_WIDTH_50, PreferenceConstants.DEF_COLOR_PEAK_WIDTH_50);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_WIDTH_CONDAL_BOSH, PreferenceConstants.DEF_SHOW_PEAK_WIDTH_CONDAL_BOSH);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_WIDTH_CONDAL_BOSH, PreferenceConstants.DEF_COLOR_PEAK_WIDTH_CONDAL_BOSH);
		store.setDefault(PreferenceConstants.P_COLOR_PEAK_DETECTOR_CHROMATOGRAM, PreferenceConstants.DEF_COLOR_PEAK_DETECTOR_CHROMATOGRAM);
		store.setDefault(PreferenceConstants.P_SHOW_PEAK_DETECTOR_CHROMATOGRAM_AREA, PreferenceConstants.DEF_SHOW_PEAK_DETECTOR_CHROMATOGRAM_AREA);
		store.setDefault(PreferenceConstants.P_PEAK_DETECTOR_SCAN_MARKER_SIZE, PreferenceConstants.DEF_PEAK_DETECTOR_SCAN_MARKER_SIZE);
		store.setDefault(PreferenceConstants.P_PEAK_DETECTOR_SCAN_MARKER_COLOR, PreferenceConstants.DEF_PEAK_DETECTOR_SCAN_MARKER_COLOR);
		store.setDefault(PreferenceConstants.P_PEAK_DETECTOR_SCAN_MARKER_TYPE, PreferenceConstants.DEF_PEAK_DETECTOR_SCAN_MARKER_TYPE);
		/*
		 * Targets
		 */
		store.setDefault(PreferenceConstants.P_USE_TARGET_LIST, PreferenceConstants.DEF_USE_TARGET_LIST);
		store.setDefault(PreferenceConstants.P_TARGET_LIST, PreferenceConstants.DEF_TARGET_LIST);
		store.setDefault(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE, PreferenceConstants.DEF_PROPAGATE_TARGET_ON_UPDATE);
		/*
		 * Chromatogram
		 */
		store.setDefault(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, PreferenceConstants.DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
		store.setDefault(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, PreferenceConstants.DEF_SHOW_AREA_WITHOUT_DECIMALS);
		store.setDefault(PreferenceConstants.P_SHOW_PEAKS_IN_SELECTED_RANGE, PreferenceConstants.DEF_SHOW_PEAKS_IN_SELECTED_RANGE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_NORMAL);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SIC, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_SIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_TIC, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_TIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_BPC, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_BPC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_XIC, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_XIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_SIC, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_SIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_TSC, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_TSC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_DEFAULT, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_DEFAULT);
	}
}
