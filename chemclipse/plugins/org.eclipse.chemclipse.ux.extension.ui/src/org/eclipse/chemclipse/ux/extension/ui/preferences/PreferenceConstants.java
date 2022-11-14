/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.settings.UserManagement;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

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
}