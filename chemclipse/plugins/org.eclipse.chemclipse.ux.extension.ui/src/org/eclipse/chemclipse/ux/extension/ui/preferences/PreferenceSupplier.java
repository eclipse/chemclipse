/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.ux.extension.ui.Activator;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SELECTED_DRIVE_PATH = "selectedDrivePath";
	public static final String DEF_SELECTED_DRIVE_PATH = "";
	public static final String P_SELECTED_HOME_PATH = "selectedHomePath";
	public static final String DEF_SELECTED_HOME_PATH = "";
	public static final String P_SELECTED_WORKSPACE_PATH = "selectedWorkspacePath";
	public static final String DEF_SELECTED_WORKSPACE_PATH = "";
	public static final String P_SELECTED_USER_LOCATION_PATH = "selectedUserLocationPath";
	public static final String DEF_SELECTED_USER_LOCATION_PATH = "";
	//
	public static final String P_USER_LOCATION_PATH = "userLocation";
	public static final String DEF_USER_LOCATION_PATH = UserManagement.getUserHome();
	public static final String P_SHOW_NETWORK_SHARES = "showWindowsNetworkDrive";
	public static final boolean DEF_SHOW_NETWORK_SHARES = true;
	//
	public static final String P_OPEN_FIRST_DATA_MATCH_ONLY = "openFirstDataMatchOnly";
	public static final boolean DEF_OPEN_FIRST_DATA_MATCH_ONLY = true;
	public static final String P_OPEN_EDITOR_MULTIPLE_TIMES = "openEditorMultipleTimes";
	public static final boolean DEF_OPEN_EDITOR_MULTIPLE_TIMES = true;

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getDefault().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_SELECTED_DRIVE_PATH, DEF_SELECTED_DRIVE_PATH);
		putDefault(P_SELECTED_HOME_PATH, DEF_SELECTED_HOME_PATH);
		putDefault(P_SELECTED_WORKSPACE_PATH, DEF_SELECTED_WORKSPACE_PATH);
		putDefault(P_SELECTED_USER_LOCATION_PATH, DEF_SELECTED_USER_LOCATION_PATH);
		putDefault(P_USER_LOCATION_PATH, DEF_USER_LOCATION_PATH);
		putDefault(P_SHOW_NETWORK_SHARES, DEF_SHOW_NETWORK_SHARES);
		putDefault(P_OPEN_FIRST_DATA_MATCH_ONLY, DEF_OPEN_FIRST_DATA_MATCH_ONLY);
		putDefault(P_OPEN_EDITOR_MULTIPLE_TIMES, DEF_OPEN_EDITOR_MULTIPLE_TIMES);
	}

	public static String getSelectedDrivePath() {

		return INSTANCE().get(P_SELECTED_DRIVE_PATH);
	}

	public static void setSelectedDrivePath(String directoryPath) {

		INSTANCE().set(P_SELECTED_DRIVE_PATH, directoryPath);
	}

	public static String getSelectedHomePath() {

		return INSTANCE().get(P_SELECTED_HOME_PATH);
	}

	public static void setSelectedHomePath(String directoryPath) {

		INSTANCE().set(P_SELECTED_HOME_PATH, directoryPath);
	}

	public static String getSelectedWorkspacePath() {

		return INSTANCE().get(P_SELECTED_WORKSPACE_PATH);
	}

	public static void setSelectedWorkspaceath(String directoryPath) {

		INSTANCE().set(P_SELECTED_WORKSPACE_PATH, directoryPath);
	}

	public static String getSelectedUserLocationPath() {

		return INSTANCE().get(P_SELECTED_USER_LOCATION_PATH);
	}

	public static void setSelectedUserLocationPath(String directoryPath) {

		INSTANCE().set(P_SELECTED_USER_LOCATION_PATH, directoryPath);
	}

	public static String getUserLocationPath() {

		return INSTANCE().get(P_USER_LOCATION_PATH);
	}

	public static void setUserLocationPath(String directoryPath) {

		INSTANCE().set(P_USER_LOCATION_PATH, directoryPath);
	}

	public static boolean isOpenFirstDataMatchOnly() {

		return INSTANCE().getBoolean(P_OPEN_FIRST_DATA_MATCH_ONLY);
	}

	public static boolean isOpenEditorMultipleTimes() {

		return INSTANCE().getBoolean(P_OPEN_EDITOR_MULTIPLE_TIMES);
	}

	public static boolean showNetworkShares() {

		return INSTANCE().getBoolean(P_SHOW_NETWORK_SHARES);
	}
}