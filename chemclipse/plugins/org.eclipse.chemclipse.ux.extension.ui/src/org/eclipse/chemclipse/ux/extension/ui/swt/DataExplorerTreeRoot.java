/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.settings.ApplicationSettings;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.core.resources.ResourcesPlugin;

/*
 * https://docs.oracle.com/javase/tutorial/essential/io/fileAttr.html
 */
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
				if(OperatingSystemUtils.isWindows()) {
					/*
					 * Windows
					 */
					if(PreferenceSupplier.isWindowsListDrivesByType()) {
						String windowsDriveType = PreferenceSupplier.getWindowsDriveType();
						roots = listRootWindowsByDriveType(windowsDriveType);
					} else {
						roots = File.listRoots();
					}
				} else {
					/*
					 * macOS, Linux
					 */
					roots = File.listRoots();
				}
				break;
			case HOME:
				roots = new File[]{new File(UserManagement.getUserHome())};
				break;
			case WORKSPACE:
				File root = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
				if(root.exists()) {
					roots = root.listFiles();
				} else {
					/*
					 * Fallback solution
					 */
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

	private static File[] listRootWindowsByDriveType(String driveType) {

		List<File> rootFiles = new ArrayList<>();
		File[] roots = File.listRoots();
		/*
		 * Check, if the drive is a local root.
		 */
		File rootDriveC = null;
		for(File root : roots) {
			try {
				/*
				 * Default C:
				 */
				if(root.toString().toLowerCase().startsWith("c:\\")) {
					rootDriveC = root;
				}
				/*
				 * Collect matching types.
				 */
				FileStore fileStore = Files.getFileStore(root.toPath());
				String type = (fileStore != null) ? fileStore.type() : "";
				if(type.contains(driveType)) {
					rootFiles.add(root);
				}
			} catch(IOException e) {
			}
		}
		/*
		 * If the list is of selected drives is empty,
		 * add C:\ by default if available.
		 */
		if(rootFiles.isEmpty() && rootDriveC != null) {
			rootFiles.add(rootDriveC);
		}
		//
		return rootFiles.toArray(new File[rootFiles.size()]);
	}
}