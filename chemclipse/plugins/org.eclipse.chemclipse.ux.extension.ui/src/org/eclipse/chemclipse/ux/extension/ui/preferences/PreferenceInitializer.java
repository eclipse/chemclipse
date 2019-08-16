/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.preferences;

import org.eclipse.chemclipse.ux.extension.ui.Activator;
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
		store.setDefault(PreferenceConstants.P_SELECTED_DRIVE_PATH, PreferenceConstants.DEF_SELECTED_DRIVE_PATH);
		store.setDefault(PreferenceConstants.P_SELECTED_HOME_PATH, PreferenceConstants.DEF_SELECTED_HOME_PATH);
		store.setDefault(PreferenceConstants.P_SELECTED_USER_LOCATION_PATH, PreferenceConstants.DEF_SELECTED_USER_LOCATION_PATH);
		//
		store.setDefault(PreferenceConstants.P_USER_LOCATION_PATH, PreferenceConstants.DEF_USER_LOCATION_PATH);
		//
		store.setDefault(PreferenceConstants.P_OPEN_FIRST_DATA_MATCH_ONLY, PreferenceConstants.DEF_OPEN_FIRST_DATA_MATCH_ONLY);
	}
}
