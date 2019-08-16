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
package org.eclipse.chemclipse.ux.extension.wsd.ui.preferences;

import org.eclipse.chemclipse.ux.extension.wsd.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceSupplier {

	public static final String P_PATH_OPEN_CHROMATOGRAMS = "pathOpenChromatograms";
	public static final String DEF_PATH_OPEN_CHROMATOGRAMS = "";

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {
	}

	public static String getPathOpenChromatograms() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(P_PATH_OPEN_CHROMATOGRAMS);
	}

	public static void setPathOpenChromatograms(String value) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(P_PATH_OPEN_CHROMATOGRAMS, value);
	}
}
