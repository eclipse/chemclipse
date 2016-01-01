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
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_POSITION_MARKER_BACKGROUND_COLOR, "255,255,255");
		store.setDefault(PreferenceConstants.P_POSITION_MARKER_FOREGROUND_COLOR, "192,192,192");
		//
		store.setDefault(PreferenceConstants.P_SHOW_SELECTED_PEAK_IN_EDITOR, true);
		store.setDefault(PreferenceConstants.P_SHOW_SCANS_OF_SELECTED_PEAK, true);
		store.setDefault(PreferenceConstants.P_SIZE_OF_PEAK_SCAN_MARKER, 2);
		//
		store.setDefault(PreferenceConstants.P_SHOW_BACKGROUND_IN_CHROMATOGRAM_EDITOR, false);
		store.setDefault(PreferenceConstants.P_SHOW_CHROMATOGRAM_AREA, true);
		//
		store.setDefault(PreferenceConstants.P_SCAN_DISPLAY_NUMBER_OF_IONS, 5);
		store.setDefault(PreferenceConstants.P_USE_MODULO_DISPLAY_OF_IONS, false);
		//
		store.setDefault(PreferenceConstants.P_COLOR_CHROMATOGRAM, "255,0,0");
		store.setDefault(PreferenceConstants.P_COLOR_MASS_SPECTRUM, "255,0,0");
		store.setDefault(PreferenceConstants.P_COLOR_SCHEME_OVERLAY, PreferenceConstants.DEF_COLOR_SCHEME_OVERLAY);
		//
		store.setDefault(PreferenceConstants.P_AUTO_ADJUST_EDITOR_INTENSITY_DISPLAY, false);
		store.setDefault(PreferenceConstants.P_AUTO_ADJUST_VIEW_INTENSITY_DISPLAY, false);
		//
		store.setDefault(PreferenceConstants.P_SHOW_AXIS_MILLISECONDS, false);
		store.setDefault(PreferenceConstants.P_SHOW_AXIS_RELATIVE_INTENSITY, false);
		//
		store.setDefault(PreferenceConstants.P_SHOW_CHROMATOGRAM_HAIRLINE_DIVIDER, false);
		//
		store.setDefault(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, true);
	}
}
