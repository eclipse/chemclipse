/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
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
		//
		store.setDefault(PreferenceConstants.P_ALTERNATE_WINDOW_MOVE_DIRECTION, false);
		store.setDefault(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS, true);
		store.setDefault(PreferenceConstants.P_SHOW_RETENTION_INDEX_WITHOUT_DECIMALS, true);
		store.setDefault(PreferenceConstants.P_SHOW_AREA_WITHOUT_DECIMALS, true);
		store.setDefault(PreferenceConstants.P_SORT_CASE_SENSITIVE, true);
		//
		store.setDefault(PreferenceConstants.P_SEARCH_CASE_SENSITIVE, PreferenceConstants.DEF_SEARCH_CASE_SENSITIVE);
	}
}
