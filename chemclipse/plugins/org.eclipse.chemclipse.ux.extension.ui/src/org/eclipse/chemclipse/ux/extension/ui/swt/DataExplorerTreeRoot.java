/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.io.File;

import org.eclipse.chemclipse.support.settings.ApplicationSettings;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.core.resources.ResourcesPlugin;

public enum DataExplorerTreeRoot {
	NONE(""), //
	DRIVES("Drives"), //
	HOME("Home"), //
	WORKSPACE("Workspace"), //
	USER_LOCATION("User Location");

	private String label;

	private DataExplorerTreeRoot(String label) {

		this.label = label;
	}

	@Override
	public String toString() {

		return this != NONE ? label : super.toString();
	}

	public String getPreferenceKeyDefaultPath() {

		switch(this) {
			case DRIVES:
				return PreferenceConstants.P_SELECTED_DRIVE_PATH;
			case HOME:
				return PreferenceConstants.P_SELECTED_HOME_PATH;
			case WORKSPACE:
				return PreferenceConstants.P_SELECTED_WORKSPACE_PATH;
			case USER_LOCATION:
				return PreferenceConstants.P_SELECTED_USER_LOCATION_PATH;
			case NONE:
			default:
				return "selected" + name() + "Path";
		}
	}

	public File[] getRootContent() {

		File[] roots;
		switch(this) {
			case DRIVES:
				roots = File.listRoots();
				break;
			case HOME:
				roots = new File[]{new File(UserManagement.getUserHome())};
				break;
			case WORKSPACE:
				File root = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
				if(root.exists()) {
					roots = root.listFiles();
				} else {
					// Fallback solution
					roots = ApplicationSettings.getWorkspaceDirectory().listFiles();
				}
				break;
			case USER_LOCATION:
				roots = new File[]{getUserLocation()};
				break;
			case NONE:
			default:
				roots = new File[]{};
				break;
		}
		//
		return roots;
	}

	public static DataExplorerTreeRoot[] getDefaultRoots() {

		return new DataExplorerTreeRoot[]{DRIVES, HOME, WORKSPACE, USER_LOCATION};
	}

	private static File getUserLocation() {

		String userLocationPath = PreferenceSupplier.getUserLocationPath();
		File userLocation = new File(userLocationPath);
		if(!userLocation.exists()) {
			userLocation = new File(UserManagement.getUserHome());
		}
		return userLocation;
	}
}
