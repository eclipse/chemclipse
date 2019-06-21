/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
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

	@Override
	public void initializeDefaultPreferences() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		/*
		 * General
		 */
		store.setDefault(PreferenceConstants.P_STACK_POSITION_HEADER_DATA, PreferenceConstants.DEF_STACK_POSITION_MEASUREMENT_HEADER);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_OVERVIEW, PreferenceConstants.DEF_STACK_POSITION_CHROMATOGRAM_OVERVIEW);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_MISCELLANEOUS_INFO, PreferenceConstants.DEF_STACK_POSITION_MISCELLANEOUS_INFO);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_SCAN_INFO, PreferenceConstants.DEF_STACK_POSITION_CHROMATOGRAM_SCAN_INFO);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_DEFAULT, PreferenceConstants.DEF_STACK_POSITION_OVERLAY_CHROMATOGRAM_DEFAULT);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA, PreferenceConstants.DEF_STACK_POSITION_OVERLAY_CHROMATOGRAM_EXTRA);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_OVERLAY_NMR, PreferenceConstants.DEF_STACK_POSITION_OVERLAY_NMR);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_OVERLAY_XIR, PreferenceConstants.DEF_STACK_POSITION_OVERLAY_XIR);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_BASELINE, PreferenceConstants.DEF_STACK_POSITION_BASELINE);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_TARGETS, PreferenceConstants.DEF_STACK_POSITION_TARGETS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SCAN_CHART, PreferenceConstants.DEF_STACK_POSITION_SCAN_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SCAN_TABLE, PreferenceConstants.DEF_STACK_POSITION_SCAN_TABLE);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_CHART, PreferenceConstants.DEF_STACK_POSITION_PEAK_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_DETAILS, PreferenceConstants.DEF_STACK_POSITION_PEAK_DETAILS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_DETECTOR, PreferenceConstants.DEF_STACK_POSITION_PEAK_DETECTOR);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_SCAN_LIST, PreferenceConstants.DEF_STACK_POSITION_PEAK_SCAN_LIST);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_LIST, PreferenceConstants.DEF_STACK_POSITION_PEAK_QUANTITATION_LIST);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_SUBTRACT_SCAN_PART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_COMBINED_SCAN_PART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_CHART, PreferenceConstants.DEF_STACK_POSITION_COMPARISON_SCAN_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANTITATION, PreferenceConstants.DEF_STACK_POSITION_QUANTITATION);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_INTEGRATION_AREA, PreferenceConstants.DEF_STACK_POSITION_INTEGRATION_AREA);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_INTERNAL_STANDARDS, PreferenceConstants.DEF_STACK_POSITION_INTERNAL_STANDARDS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_MEASUREMENT_RESULTS, PreferenceConstants.DEF_STACK_POSITION_MEASUREMENT_RESULTS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_CHROMATOGRAM_HEATMAP, PreferenceConstants.DEF_STACK_POSITION_CHROMATOGRAM_HEATMAP);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PEAK_QUANTITATION_REFERENCES, PreferenceConstants.DEF_STACK_POSITION_PEAK_QUANTITATION_REFERENCES);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PLATE_CHARTS, PreferenceConstants.DEF_STACK_POSITION_PLATE_CHARTS);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_WELL_DATA, PreferenceConstants.DEF_STACK_POSITION_WELL_DATA);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_WELL_CHART, PreferenceConstants.DEF_STACK_POSITION_WELL_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_PLATE_DATA, PreferenceConstants.DEF_STACK_POSITION_PLATE_DATA);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_CHART, PreferenceConstants.DEF_STACK_POSITION_QUANT_RESPONSE_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANT_RESPONSE_LIST, PreferenceConstants.DEF_STACK_POSITION_QUANT_RESPONSE_LIST);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_CHART, PreferenceConstants.DEF_STACK_POSITION_QUANT_PEAKS_CHART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANT_PEAKS_LIST, PreferenceConstants.DEF_STACK_POSITION_QUANT_PEAKS_LIST);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_QUANT_SIGNALS_LIST, PreferenceConstants.DEF_STACK_POSITION_QUANT_SIGNALS_LIST);
		/*
		 * Overlay
		 */
		store.setDefault(PreferenceConstants.P_OVERLAY_CHART_COMPRESSION_TYPE, PreferenceConstants.DEF_OVERLAY_CHART_COMPRESSION_TYPE);
		store.setDefault(PreferenceConstants.P_SHOW_REFERENCED_CHROMATOGRAMS, PreferenceConstants.DEF_SHOW_REFERENCED_CHROMATOGRAMS);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_NORMAL_OVERLAY);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SIC_OVERLAY, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_SIC_OVERLAY);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SWC_OVERLAY, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_SWC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_TIC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_TIC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_BPC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_BPC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_XIC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_XIC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_SIC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_SIC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_TSC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_TSC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_XWC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_XWC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_SWC_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_SWC_OVERLAY);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_DISPLAY_DEFAULT_OVERLAY, PreferenceConstants.DEF_LINE_STYLE_DISPLAY_DEFAULT_OVERLAY);
		//
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SELECTION, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_SELECTION);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_USERS_CHOICE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_WAVELENGTHS_USERS_CHOICE, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_WAVELENGTHS_USERS_CHOICE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_HYDROCARBONS);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_FATTY_ACIDS);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_FAME, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_FAME);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_SOLVENT_TAILING);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED, PreferenceConstants.DEF_CHROMATOGRAM_OVERLAY_IONS_COLUMN_BLEED);
		//
		store.setDefault(PreferenceConstants.P_OVERLAY_SHIFT_X, PreferenceConstants.DEF_OVERLAY_SHIFT_X);
		store.setDefault(PreferenceConstants.P_INDEX_SHIFT_X, PreferenceConstants.DEF_INDEX_SHIFT_X);
		store.setDefault(PreferenceConstants.P_OVERLAY_SHIFT_Y, PreferenceConstants.DEF_OVERLAY_SHIFT_Y);
		store.setDefault(PreferenceConstants.P_INDEX_SHIFT_Y, PreferenceConstants.DEF_INDEX_SHIFT_Y);
		//
		store.setDefault(PreferenceConstants.P_OVERLAY_SHOW_AREA, PreferenceConstants.DEF_OVERLAY_SHOW_AREA);
		store.setDefault(PreferenceConstants.P_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS, PreferenceConstants.DEF_OVERLAY_AUTOFOCUS_PROFILE_SETTINGS);
		store.setDefault(PreferenceConstants.P_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS, PreferenceConstants.DEF_OVERLAY_AUTOFOCUS_SHIFT_SETTINGS);
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
		 * Peak(s) Chart
		 */
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_PEAKS, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_PEAKS);
		store.setDefault(PreferenceConstants.P_SHOW_AREA_DISPLAY_PEAKS, PreferenceConstants.DEF_SHOW_AREA_DISPLAY_PEAKS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS_PEAKS, PreferenceConstants.DEF_SHOW_X_AXIS_MILLISECONDS_PEAKS);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS_PEAKS, PreferenceConstants.DEF_POSITION_X_AXIS_MILLISECONDS_PEAKS);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS_PEAKS, PreferenceConstants.DEF_COLOR_X_AXIS_MILLISECONDS_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS_PEAKS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_MILLISECONDS_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS_PEAKS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_MILLISECONDS_PEAKS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_MINUTES_PEAKS, PreferenceConstants.DEF_SHOW_X_AXIS_MINUTES_PEAKS);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_MINUTES_PEAKS, PreferenceConstants.DEF_POSITION_X_AXIS_MINUTES_PEAKS);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_MINUTES_PEAKS, PreferenceConstants.DEF_COLOR_X_AXIS_MINUTES_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES_PEAKS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_MINUTES_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES_PEAKS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_MINUTES_PEAKS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY_PEAKS, PreferenceConstants.DEF_SHOW_Y_AXIS_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY_PEAKS, PreferenceConstants.DEF_POSITION_Y_AXIS_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY_PEAKS, PreferenceConstants.DEF_COLOR_Y_AXIS_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY_PEAKS, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY_PEAKS, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_INTENSITY_PEAKS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY_PEAKS, PreferenceConstants.DEF_SHOW_Y_AXIS_RELATIVE_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY_PEAKS, PreferenceConstants.DEF_POSITION_Y_AXIS_RELATIVE_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY_PEAKS, PreferenceConstants.DEF_COLOR_Y_AXIS_RELATIVE_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY_PEAKS, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY_PEAKS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY_PEAKS, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY_PEAKS);
		/*
		 * Targets
		 */
		store.setDefault(PreferenceConstants.P_USE_TARGET_LIST, PreferenceConstants.DEF_USE_TARGET_LIST);
		store.setDefault(PreferenceConstants.P_TARGET_LIST, PreferenceConstants.DEF_TARGET_LIST);
		store.setDefault(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE, PreferenceConstants.DEF_PROPAGATE_TARGET_ON_UPDATE);
		store.setDefault(PreferenceConstants.P_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER, PreferenceConstants.DEF_TARGET_TEMPLATE_LIBRARY_IMPORT_FOLDER);
		/*
		 * Chromatogram
		 */
		store.setDefault(PreferenceConstants.P_LEGACY_UPDATE_CHROMATOGRAM_MODUS, PreferenceConstants.DEF_LEGACY_UPDATE_CHROMATOGRAM_MODUS);
		store.setDefault(PreferenceConstants.P_LEGACY_UPDATE_PEAK_MODUS, PreferenceConstants.DEF_LEGACY_UPDATE_PEAK_MODUS);
		store.setDefault(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, PreferenceConstants.DEF_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS);
		store.setDefault(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, PreferenceConstants.DEF_SHOW_AREA_WITHOUT_DECIMALS);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		store.setDefault(PreferenceConstants.P_COLOR_CHROMATOGRAM, PreferenceConstants.DEF_COLOR_CHROMATOGRAM);
		store.setDefault(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA, PreferenceConstants.DEF_ENABLE_CHROMATOGRAM_AREA);
		store.setDefault(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK, PreferenceConstants.DEF_COLOR_CHROMATOGRAM_SELECTED_PEAK);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_SIZE, PreferenceConstants.DEF_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_SIZE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_SELECTED_PEAK_SCAN_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN, PreferenceConstants.DEF_COLOR_CHROMATOGRAM_SELECTED_SCAN);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE, PreferenceConstants.DEF_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_PEAK_LABELS);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_NAME, PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_NAME);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE, PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_SIZE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE, PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_FONT_STYLE);
		store.setDefault(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_BASELINE);
		store.setDefault(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE, PreferenceConstants.DEF_COLOR_CHROMATOGRAM_BASELINE);
		store.setDefault(PreferenceConstants.P_ENABLE_BASELINE_AREA, PreferenceConstants.DEF_ENABLE_BASELINE_AREA);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE, PreferenceConstants.DEF_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_PEAKS_ACTIVE_NORMAL_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_PEAKS_INACTIVE_NORMAL_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_PEAKS_ACTIVE_ISTD_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE, PreferenceConstants.DEF_CHROMATOGRAM_PEAKS_INACTIVE_ISTD_MARKER_TYPE);
		store.setDefault(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, PreferenceConstants.DEF_SHOW_CHROMATOGRAM_SCAN_LABELS);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE, PreferenceConstants.DEF_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_NAME, PreferenceConstants.DEF_CHROMATOGRAM_SCAN_LABEL_FONT_NAME);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE, PreferenceConstants.DEF_CHROMATOGRAM_SCAN_LABEL_FONT_SIZE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE, PreferenceConstants.DEF_CHROMATOGRAM_SCAN_LABEL_FONT_STYLE);
		store.setDefault(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED, PreferenceConstants.DEF_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED);
		store.setDefault(PreferenceConstants.P_MOVE_RETENTION_TIME_ON_PEAK_SELECTION, PreferenceConstants.DEF_MOVE_RETENTION_TIME_ON_PEAK_SELECTION);
		store.setDefault(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION, PreferenceConstants.DEF_ALTERNATE_WINDOW_MOVE_DIRECTION);
		store.setDefault(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, PreferenceConstants.DEF_CONDENSE_CYCLE_NUMBER_SCANS);
		store.setDefault(PreferenceConstants.P_SET_CHROMATOGRAM_INTENSITY_RANGE, PreferenceConstants.DEF_SET_CHROMATOGRAM_INTENSITY_RANGE);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME, PreferenceConstants.DEF_CHROMATOGRAM_TRANSFER_DELTA_RETENTION_TIME);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY, PreferenceConstants.DEF_CHROMATOGRAM_TRANSFER_BEST_TARGET_ONLY);
		store.setDefault(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY, PreferenceConstants.DEF_STRETCH_CHROMATOGRAM_MILLISECONDS_SCAN_DELAY);
		store.setDefault(PreferenceConstants.P_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH, PreferenceConstants.DEF_STRETCH_CHROMATOGRAM_MILLISECONDS_LENGTH);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_EXTEND_X, PreferenceConstants.DEF_CHROMATOGRAM_EXTEND_X);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_X_ZOOM_ONLY, PreferenceConstants.DEF_CHROMATOGRAM_X_ZOOM_ONLY);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_Y_ZOOM_ONLY, PreferenceConstants.DEF_CHROMATOGRAM_Y_ZOOM_ONLY);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_MILLISECONDS);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_POSITION_X_AXIS_MILLISECONDS);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_COLOR_X_AXIS_MILLISECONDS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_MILLISECONDS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MILLISECONDS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_MILLISECONDS);
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MILLISECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_MILLISECONDS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_SECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_SECONDS);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_SECONDS, PreferenceConstants.DEF_POSITION_X_AXIS_SECONDS);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_SECONDS, PreferenceConstants.DEF_COLOR_X_AXIS_SECONDS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SECONDS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_SECONDS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SECONDS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_SECONDS);
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SECONDS, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_SECONDS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_MINUTES, PreferenceConstants.DEF_SHOW_X_AXIS_MINUTES);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_MINUTES, PreferenceConstants.DEF_POSITION_X_AXIS_MINUTES);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_MINUTES, PreferenceConstants.DEF_COLOR_X_AXIS_MINUTES);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_MINUTES, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_MINUTES);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_MINUTES, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_MINUTES);
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_MINUTES, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_MINUTES);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_SCANS, PreferenceConstants.DEF_SHOW_X_AXIS_SCANS);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_SCANS, PreferenceConstants.DEF_POSITION_X_AXIS_SCANS);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_SCANS, PreferenceConstants.DEF_COLOR_X_AXIS_SCANS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_SCANS);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_SCANS);
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SCANS, PreferenceConstants.DEF_SHOW_X_AXIS_TITLE_SCANS);
		//
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_INTENSITY);
		store.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_INTENSITY, PreferenceConstants.DEF_POSITION_Y_AXIS_INTENSITY);
		store.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_INTENSITY, PreferenceConstants.DEF_COLOR_Y_AXIS_INTENSITY);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_INTENSITY, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_INTENSITY);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_INTENSITY, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_INTENSITY);
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_TITLE_INTENSITY);
		//
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_RELATIVE_INTENSITY);
		store.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_POSITION_Y_AXIS_RELATIVE_INTENSITY);
		store.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_COLOR_Y_AXIS_RELATIVE_INTENSITY);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_RELATIVE_INTENSITY);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_RELATIVE_INTENSITY);
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY, PreferenceConstants.DEF_SHOW_Y_AXIS_TITLE_RELATIVE_INTENSITY);
		//
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SELECTED_ACTION_ID, PreferenceConstants.DEF_CHROMATOGRAM_SELECTED_ACTION_ID);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_SAVE_AS_FOLDER, PreferenceConstants.DEF_CHROMATOGRAM_SAVE_AS_FOLDER);
		store.setDefault(PreferenceConstants.P_CHROMATOGRAM_LOAD_PROCESS_METHOD, PreferenceConstants.DEF_CHROMATOGRAM_LOAD_PROCESS_METHOD);
		store.setDefault(PreferenceConstants.P_DELTA_MILLISECONDS_PEAK_SELECTION, PreferenceConstants.DEF_DELTA_MILLISECONDS_PEAK_SELECTION);
		/*
		 * Calibration Chart
		 */
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_CALIBRATION, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_CALIBRATION);
		//
		store.setDefault(PreferenceConstants.P_SHOW_X_AXIS_CONCENTRATION_CALIBRATION, PreferenceConstants.DEF_SHOW_X_AXIS_CONCENTRATION_CALIBRATION);
		store.setDefault(PreferenceConstants.P_POSITION_X_AXIS_CONCENTRATION_CALIBRATION, PreferenceConstants.DEF_POSITION_X_AXIS_CONCENTRATION_CALIBRATION);
		store.setDefault(PreferenceConstants.P_COLOR_X_AXIS_CONCENTRATION_CALIBRATION, PreferenceConstants.DEF_COLOR_X_AXIS_CONCENTRATION_CALIBRATION);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_CONCENTRATION_CALIBRATION, PreferenceConstants.DEF_GRIDLINE_STYLE_X_AXIS_CONCENTRATION_CALIBRATION);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_CONCENTRATION_CALIBRATION, PreferenceConstants.DEF_GRIDLINE_COLOR_X_AXIS_CONCENTRATION_CALIBRATION);
		//
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_RESPONSE_CALIBRATION, PreferenceConstants.DEF_SHOW_Y_AXIS_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_RESPONSE_CALIBRATION, PreferenceConstants.DEF_POSITION_Y_AXIS_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_RESPONSE_CALIBRATION, PreferenceConstants.DEF_COLOR_Y_AXIS_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RESPONSE_CALIBRATION, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RESPONSE_CALIBRATION, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_RESPONSE_CALIBRATION);
		//
		store.setDefault(PreferenceConstants.P_SHOW_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION, PreferenceConstants.DEF_SHOW_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_POSITION_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION, PreferenceConstants.DEF_POSITION_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_COLOR_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION, PreferenceConstants.DEF_COLOR_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_GRIDLINE_STYLE_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION, PreferenceConstants.DEF_GRIDLINE_STYLE_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION);
		store.setDefault(PreferenceConstants.P_GRIDLINE_COLOR_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION, PreferenceConstants.DEF_GRIDLINE_COLOR_Y_AXIS_RELATIVE_RESPONSE_CALIBRATION);
		/*
		 * File Explorer
		 */
		store.setDefault(PreferenceConstants.P_SHOW_DATA_MSD, PreferenceConstants.DEF_SHOW_DATA_MSD);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_CSD, PreferenceConstants.DEF_SHOW_DATA_CSD);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_WSD, PreferenceConstants.DEF_SHOW_DATA_WSD);
		store.setDefault(PreferenceConstants.P_SHOW_LIBRARY_MSD, PreferenceConstants.DEF_SHOW_LIBRARY_MSD);
		store.setDefault(PreferenceConstants.P_SHOW_SCANS_MSD, PreferenceConstants.DEF_SHOW_SCANS_MSD);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_XIR, PreferenceConstants.DEF_SHOW_DATA_XIR);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_NMR, PreferenceConstants.DEF_SHOW_DATA_NMR);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_CAL, PreferenceConstants.DEF_SHOW_DATA_CAL);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_PCR, PreferenceConstants.DEF_SHOW_DATA_PCR);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_SEQUENCE, PreferenceConstants.DEF_SHOW_DATA_SEQUENCE);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_METHOD, PreferenceConstants.DEF_SHOW_DATA_METHOD);
		store.setDefault(PreferenceConstants.P_SHOW_DATA_QUANT_DB, PreferenceConstants.DEF_SHOW_DATA_QUANT_DB);
		/*
		 * Lists
		 */
		store.setDefault(PreferenceConstants.P_SHOW_PEAKS_IN_LIST, PreferenceConstants.DEF_SHOW_PEAKS_IN_LIST);
		store.setDefault(PreferenceConstants.P_SHOW_PEAKS_IN_SELECTED_RANGE, PreferenceConstants.DEF_SHOW_PEAKS_IN_SELECTED_RANGE);
		store.setDefault(PreferenceConstants.P_SHOW_SCANS_IN_LIST, PreferenceConstants.DEF_SHOW_SCANS_IN_LIST);
		store.setDefault(PreferenceConstants.P_SHOW_SCANS_IN_SELECTED_RANGE, PreferenceConstants.DEF_SHOW_SCANS_IN_SELECTED_RANGE);
		store.setDefault(PreferenceConstants.P_COLUMN_ORDER_TARGET_LIST, PreferenceConstants.DEF_COLUMN_ORDER_TARGET_LIST);
		/*
		 * Baseline
		 */
		store.setDefault(PreferenceConstants.P_BASELINE_CHART_COMPRESSION_TYPE, PreferenceConstants.DEF_BASELINE_CHART_COMPRESSION_TYPE);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_BASELINE, PreferenceConstants.DEF_COLOR_SCHEME_DISPLAY_BASELINE);
		/*
		 * Sequences
		 */
		store.setDefault(PreferenceConstants.P_SEQUENCE_EXPLORER_USE_SUBFOLDER, PreferenceConstants.DEF_SEQUENCE_EXPLORER_USE_SUBFOLDER);
		store.setDefault(PreferenceConstants.P_SEQUENCE_EXPLORER_SORT_DATA, PreferenceConstants.DEF_SEQUENCE_EXPLORER_SORT_DATA);
		store.setDefault(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER, PreferenceConstants.DEF_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER);
		store.setDefault(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER, PreferenceConstants.DEF_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER);
		store.setDefault(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_SUB_FOLDER, PreferenceConstants.DEF_SEQUENCE_EXPLORER_PATH_SUB_FOLDER);
		store.setDefault(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER, PreferenceConstants.DEF_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER);
		/*
		 * Methods
		 */
		store.setDefault(PreferenceConstants.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, PreferenceConstants.DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		store.setDefault(PreferenceConstants.P_SELECTED_METHOD_NAME, PreferenceConstants.DEF_SELECTED_METHOD_NAME);
		store.setDefault(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD, PreferenceConstants.DEF_METHOD_PROCESSOR_SELECTION_CSD);
		store.setDefault(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD, PreferenceConstants.DEF_METHOD_PROCESSOR_SELECTION_MSD);
		store.setDefault(PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD, PreferenceConstants.DEF_METHOD_PROCESSOR_SELECTION_WSD);
		/*
		 * Quantitation
		 */
		store.setDefault(PreferenceConstants.P_USE_QUANTITATION_REFERENCE_LIST, PreferenceConstants.DEF_USE_QUANTITATION_REFERENCE_LIST);
		store.setDefault(PreferenceConstants.P_QUANTITATION_REFERENCE_LIST, PreferenceConstants.DEF_QUANTITATION_REFERENCE_LIST);
		/*
		 * PCR
		 */
		store.setDefault(PreferenceConstants.P_PCR_DEFAULT_COLOR, PreferenceConstants.DEF_PCR_DEFAULT_COLOR);
		store.setDefault(PreferenceConstants.P_PCR_COLOR_CODES, PreferenceConstants.DEF_PCR_COLOR_CODES);
	}
}
