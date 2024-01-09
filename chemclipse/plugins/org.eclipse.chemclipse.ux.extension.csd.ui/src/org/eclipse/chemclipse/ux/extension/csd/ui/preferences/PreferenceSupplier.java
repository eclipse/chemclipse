/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.csd.ui.Activator;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final int MIN_X_OFFSET = 0; // = 0.0 minutes
	public static final int MAX_X_OFFSET = 6000000; // = 100.0 minutes;
	//
	public static final String P_OVERLAY_X_OFFSET = "overlayXOffset";
	public static final int DEF_OVERLAY_X_OFFSET = 0;
	public static final String P_OVERLAY_Y_OFFSET = "overlayYOffset";
	public static final int DEF_OVERLAY_Y_OFFSET = 0;
	public static final String P_PATH_OPEN_CHROMATOGRAMS = "pathOpenChromatograms";
	public static final String DEF_PATH_OPEN_CHROMATOGRAMS = "";
	//
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getDefault().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_OVERLAY_X_OFFSET, DEF_OVERLAY_X_OFFSET);
		putDefault(P_OVERLAY_Y_OFFSET, DEF_OVERLAY_Y_OFFSET);
		putDefault(P_PATH_OPEN_CHROMATOGRAMS, DEF_PATH_OPEN_CHROMATOGRAMS);
	}

	public static String getPathOpenChromatograms() {

		return INSTANCE().get(P_PATH_OPEN_CHROMATOGRAMS);
	}

	public static void setPathOpenChromatograms(String value) {

		INSTANCE().set(P_PATH_OPEN_CHROMATOGRAMS, value);
	}
}
