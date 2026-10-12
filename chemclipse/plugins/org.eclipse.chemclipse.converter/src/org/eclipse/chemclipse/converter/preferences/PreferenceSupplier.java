/*******************************************************************************
 * Copyright (c) 2019, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.osgi.framework.FrameworkUtil;

public class PreferenceSupplier extends AbstractPreferenceSupplier {

	public static final String P_CHROMATOGRAM_EXPORT_FOLDER = "chromatogramExportFolder";
	public static final String DEF_CHROMATOGRAM_EXPORT_FOLDER = "";
	public static final String P_MASS_SPECTRUM_EXPORT_FOLDER = "massSpectrumExportFolder";
	public static final String DEF_MASS_SPECTRUM_EXPORT_FOLDER = "";
	public static final String P_METHOD_EXPLORER_PATH_ROOT_FOLDER = "methodExplorerPathRootFolder";
	public static final String DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER = "";
	public static final String P_SELECTED_METHOD_FILENAME = "selectedMethodName";
	public static final String DEF_SELECTED_METHOD_FILENAME = "";

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return FrameworkUtil.getBundle(PreferenceSupplier.class).getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_CHROMATOGRAM_EXPORT_FOLDER, DEF_CHROMATOGRAM_EXPORT_FOLDER);
		putDefault(P_MASS_SPECTRUM_EXPORT_FOLDER, DEF_MASS_SPECTRUM_EXPORT_FOLDER);
		putDefault(P_METHOD_EXPLORER_PATH_ROOT_FOLDER, DEF_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		putDefault(P_SELECTED_METHOD_FILENAME, DEF_SELECTED_METHOD_FILENAME);
	}

	public static String getSettings(String key, String def) {

		return INSTANCE().get(key, def);
	}

	public static String getChromatogramExportFolder() {

		return INSTANCE().get(P_CHROMATOGRAM_EXPORT_FOLDER, DEF_CHROMATOGRAM_EXPORT_FOLDER);
	}

	public static String getMassSpectrumExportFolder() {

		return INSTANCE().get(P_MASS_SPECTRUM_EXPORT_FOLDER, DEF_MASS_SPECTRUM_EXPORT_FOLDER);
	}

	public static String getSelectedMethodFileName() {

		return INSTANCE().get(P_SELECTED_METHOD_FILENAME, DEF_SELECTED_METHOD_FILENAME);
	}

	public static void setSelectedMethodFileName(String methodName) {

		INSTANCE().put(P_SELECTED_METHOD_FILENAME, methodName);
	}

	public static void setMethodExplorerPathRootFolder(String directory) {

		INSTANCE().put(P_METHOD_EXPLORER_PATH_ROOT_FOLDER, directory);
	}
}