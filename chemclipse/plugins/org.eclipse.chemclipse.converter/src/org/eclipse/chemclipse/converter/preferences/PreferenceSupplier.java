/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.preferences;

import org.eclipse.chemclipse.converter.Activator;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_CHROMATOGRAM_EXPORT_FOLDER = "chromatogramExportFolder";
	public static final String DEF_CHROMATOGRAM_EXPORT_FOLDER = "";
	public static final String P_METHOD_EXPLORER_PATH_ROOT_FOLDER = "methodExplorerPathRootFolder";
	public static final String DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER = "";
	public static final String P_SELECTED_METHOD_NAME = "selectedMethodName";
	public static final String DEF_SELECTED_METHOD_NAME = "";

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_CHROMATOGRAM_EXPORT_FOLDER, DEF_CHROMATOGRAM_EXPORT_FOLDER);
		putDefault(P_METHOD_EXPLORER_PATH_ROOT_FOLDER, DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		putDefault(P_SELECTED_METHOD_NAME, DEF_SELECTED_METHOD_NAME);
	}

	public static String getSettings(String key, String def) {

		return INSTANCE().get(key, def);
	}

	public static String getChromatogramExportFolder() {

		return INSTANCE().get(P_CHROMATOGRAM_EXPORT_FOLDER, DEF_CHROMATOGRAM_EXPORT_FOLDER);
	}

	public static String getSelectedMethodName() {

		return INSTANCE().get(P_SELECTED_METHOD_NAME, DEF_SELECTED_METHOD_NAME);
	}

	public static void setSelectedMethodName(String methodName) {

		INSTANCE().put(P_SELECTED_METHOD_NAME, methodName);
	}

	public static void setMethodExplorerPathRootFolder(String directory) {

		INSTANCE().put(P_METHOD_EXPLORER_PATH_ROOT_FOLDER, directory);
	}
}