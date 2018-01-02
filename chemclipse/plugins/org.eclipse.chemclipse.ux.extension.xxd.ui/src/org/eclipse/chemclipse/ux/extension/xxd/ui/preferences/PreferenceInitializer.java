/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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
		store.setDefault(PreferenceConstants.P_STACK_POSITION_SUBTRACT_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_SUBTRACT_SCAN_PART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_COMBINED_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_COMBINED_SCAN_PART);
		store.setDefault(PreferenceConstants.P_STACK_POSITION_COMPARISON_SCAN_PART, PreferenceConstants.DEF_STACK_POSITION_COMPARISON_SCAN_PART);
		/*
		 * Overlay
		 */
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_NORMAL, PreferenceConstants.DEF_COLOR_SCHEME_OVERLAY_NORMAL);
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_SIC, PreferenceConstants.DEF_COLOR_SCHEME_OVERLAY_SIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_OVERLAY_TIC, PreferenceConstants.DEF_LINE_STYLE_OVERLAY_TIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_OVERLAY_BPC, PreferenceConstants.DEF_LINE_STYLE_OVERLAY_BPC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_OVERLAY_XIC, PreferenceConstants.DEF_LINE_STYLE_OVERLAY_XIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_OVERLAY_SIC, PreferenceConstants.DEF_LINE_STYLE_OVERLAY_SIC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_OVERLAY_TSC, PreferenceConstants.DEF_LINE_STYLE_OVERLAY_TSC);
		store.setDefault(PreferenceConstants.P_LINE_STYLE_OVERLAY_DEFAULT, PreferenceConstants.DEF_LINE_STYLE_OVERLAY_DEFAULT);
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
		/*
		 * Targets
		 */
		store.setDefault(PreferenceConstants.P_USE_TARGET_LIST, PreferenceConstants.DEF_USE_TARGET_LIST);
		store.setDefault(PreferenceConstants.P_TARGET_LIST, PreferenceConstants.DEF_TARGET_LIST);
	}
}
