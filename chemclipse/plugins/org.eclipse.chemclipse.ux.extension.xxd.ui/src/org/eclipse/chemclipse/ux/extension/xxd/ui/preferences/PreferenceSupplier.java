/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_FILTER_PATH_CHROMATOGRAM_MSD = "filterPathChromatogramMSD";
	public static final String DEF_FILTER_PATH_CHROMATOGRAM_MSD = "";
	public static final String P_FILTER_PATH_CHROMATOGRAM_CSD = "filterPathChromatogramCSD";
	public static final String DEF_FILTER_PATH_CHROMATOGRAM_CSD = "";
	public static final String P_FILTER_PATH_CHROMATOGRAM_WSD = "filterPathChromatogramWSD";
	public static final String DEF_FILTER_PATH_CHROMATOGRAM_WSD = "";
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getDefault().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		//
		defaultValues.put(P_FILTER_PATH_CHROMATOGRAM_MSD, DEF_FILTER_PATH_CHROMATOGRAM_MSD);
		defaultValues.put(P_FILTER_PATH_CHROMATOGRAM_CSD, DEF_FILTER_PATH_CHROMATOGRAM_CSD);
		defaultValues.put(P_FILTER_PATH_CHROMATOGRAM_WSD, DEF_FILTER_PATH_CHROMATOGRAM_WSD);
		//
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}
}