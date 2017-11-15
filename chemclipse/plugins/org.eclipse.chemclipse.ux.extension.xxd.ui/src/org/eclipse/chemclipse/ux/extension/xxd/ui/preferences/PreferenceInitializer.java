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
		/*
		 * Scans
		 */
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_FONT_NAME, PreferenceConstants.DEF_SCAN_LABEL_FONT_NAME);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_FONT_SIZE, PreferenceConstants.DEF_SCAN_LABEL_FONT_SIZE);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_FONT_STYLE, PreferenceConstants.DEF_SCAN_LABEL_FONT_STYLE);
		store.setDefault(PreferenceConstants.P_SCAN_LABEL_HIGHEST_INTENSITIES, PreferenceConstants.DEF_SCAN_LABEL_HIGHEST_INTENSITIES);
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
