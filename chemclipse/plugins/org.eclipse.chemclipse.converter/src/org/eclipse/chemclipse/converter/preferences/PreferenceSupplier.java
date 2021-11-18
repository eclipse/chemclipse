/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.converter.Activator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferenceSupplier implements IPreferenceSupplier {

	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
	//
	public static final String P_CHROMATOGRAM_EXPORT_FOLDER = "chromatogramExportFolder";
	public static final String DEF_CHROMATOGRAM_EXPORT_FOLDER = "";
	public static final String P_METHOD_EXPLORER_PATH_ROOT_FOLDER = "methodExplorerPathRootFolder";
	public static final String DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER = "";
	public static final String P_SELECTED_METHOD_NAME = "selectedMethodName";
	public static final String DEF_SELECTED_METHOD_NAME = "";
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

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_CHROMATOGRAM_EXPORT_FOLDER, DEF_CHROMATOGRAM_EXPORT_FOLDER);
		defaultValues.put(P_METHOD_EXPLORER_PATH_ROOT_FOLDER, DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		defaultValues.put(P_SELECTED_METHOD_NAME, DEF_SELECTED_METHOD_NAME);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getSettings(String key, String def) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(key, def);
	}

	public static String getChromatogramExportFolder() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_CHROMATOGRAM_EXPORT_FOLDER, DEF_CHROMATOGRAM_EXPORT_FOLDER);
	}

	public static String getSelectedMethodName() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_SELECTED_METHOD_NAME, DEF_SELECTED_METHOD_NAME);
	}

	public static void setSelectedMethodName(String methodName) {

		setSettings(P_SELECTED_METHOD_NAME, methodName);
	}

	public static void setSettings(String key, String value) {

		try {
			IEclipsePreferences preferences = INSTANCE().getPreferences();
			preferences.put(key, value);
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}