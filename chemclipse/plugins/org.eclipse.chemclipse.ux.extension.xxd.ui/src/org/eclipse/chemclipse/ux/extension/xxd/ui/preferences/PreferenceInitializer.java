/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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
		store.setDefault(PreferenceConstants.P_OVERLAY_X_OFFSET, 0);
		store.setDefault(PreferenceConstants.P_OVERLAY_Y_OFFSET, 0);
		//
		store.setDefault(PreferenceConstants.P_OFFSET_STEP_LEFT, 25000); // milliseconds
		store.setDefault(PreferenceConstants.P_OFFSET_STEP_RIGHT, 25000); // milliseconds
		store.setDefault(PreferenceConstants.P_OFFSET_STEP_UP, 500000); // intensity
		store.setDefault(PreferenceConstants.P_OFFSET_STEP_DOWN, 500000); // intensity
	}
}
