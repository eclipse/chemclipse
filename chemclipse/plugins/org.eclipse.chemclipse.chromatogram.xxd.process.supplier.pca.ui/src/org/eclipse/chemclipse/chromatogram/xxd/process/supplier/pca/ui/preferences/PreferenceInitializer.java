/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_ALGORITHM_TYPE, PreferenceConstants.DEF_ALGORITHM_TYPE);
		store.setDefault(PreferenceConstants.P_NUMBER_OF_COMPONENTS, PreferenceConstants.DEF_NUMBER_OF_COMPONENTS);
		store.setDefault(PreferenceConstants.P_AUTO_REEVALUATE, PreferenceConstants.DEF_AUTO_REEVALUATE);
		store.setDefault(PreferenceConstants.P_RETENTION_TIME_WINDOW_PEAKS, PreferenceConstants.DEF_RETENTION_TIME_WINDOW_PEAKS);
		//
		store.setDefault(PreferenceConstants.P_SCORE_PLOT_2D_SYMBOL_SIZE, PreferenceConstants.DEF_SCORE_PLOT_2D_SYMBOL_SIZE);
		store.setDefault(PreferenceConstants.P_SCORE_PLOT_2D_SYMBOL_TYPE, PreferenceConstants.DEF_SCORE_PLOT_2D_SYMBOL_TYPE);
		//
		store.setDefault(PreferenceConstants.P_LOADING_PLOT_2D_SYMBOL_SIZE, PreferenceConstants.DEF_LOADING_PLOT_2D_SYMBOL_SIZE);
		store.setDefault(PreferenceConstants.P_LOADING_PLOT_2D_SYMBOL_TYPE, PreferenceConstants.DEF_LOADING_PLOT_2D_SYMBOL_TYPE);
	}
}