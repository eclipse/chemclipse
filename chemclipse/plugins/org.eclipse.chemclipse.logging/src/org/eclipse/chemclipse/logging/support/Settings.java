/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add system method folder, create folders if the do not exits
 *******************************************************************************/
package org.eclipse.chemclipse.logging.support;

import java.io.File;
import java.util.Properties;

import org.eclipse.chemclipse.logging.Activator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.osgi.framework.Version;

public class Settings {

	/*
	 * Application name and version are defined in the product definition
	 * or as a parameter when starting the application.
	 * -Dapplication.name=MyApp
	 * -Dapplication.version=1.1.x
	 */
	public static final String D_APPLICATION_NAME = "application.name";
	public static final String D_APPLICATION_VERSION = "application.version";
	//
	private static final String DEFAULT_APPLICATION_NAME = "ChemClipse";
	private static final String DEFAULT_APPLICATION_VERSION = "0.9.x";
	//
	private static final String D_OSGI_INSTANCE_AREA = "osgi.instance.area";
	private static final String D_OSGI_USER_AREA = "osgi.user.area";
	//
	private static File fileSettingsFolder = null; // will be initialized
	private static File fileWorkspaceFolder = null; // will be initialized
	//
	private static final Logger logger = Logger.getLogger(Settings.class);

	/**
	 * Use only static methods.
	 */
	private Settings() {

	}

	/**
	 * Returns the settings folder, e.g.: "/home/user/.myapp/0.9.0"
	 * 
	 * @return File
	 */
	public static final File getSystemDirectory() {

		if(isRunInitialization()) {
			initialize();
		}
		//
		return fileSettingsFolder;
	}

	public static final File getWorkspaceDirectory() {

		if(isRunInitialization()) {
			initialize();
		}
		//
		return fileWorkspaceFolder;
	}

	/**
	 * 
	 * @return the system methods folder
	 */
	public static final File getSystemMethodDirectory() {

		File folder = new File(Settings.getSystemDirectory(), "methods");
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}

	/**
	 * 
	 * @return the system methods folder
	 */
	public static final File getSystemConfigDirectory() {

		File folder = new File(Settings.getSystemDirectory(), "configurations");
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}

	public static final File getSystemPluginDirectory() {

		File folder = new File(Settings.getSystemDirectory(), "plugins");
		if(!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}

	/**
	 * Returns e.g.: 0.9.0
	 * 
	 * @return String
	 */
	public static final String getVersionIdentifier() {

		String applicationVersion;
		//
		Properties properties = System.getProperties();
		Object version = properties.get(D_APPLICATION_VERSION);
		if(version != null && version instanceof String setVersion) {
			applicationVersion = setVersion;
		} else {
			applicationVersion = DEFAULT_APPLICATION_VERSION;
			try {
				Version bundleVersion = Activator.getContext().getBundle().getVersion();
				StringBuilder builder = new StringBuilder();
				builder.append(bundleVersion.getMajor());
				builder.append(".");
				builder.append(bundleVersion.getMinor());
				builder.append(".");
				builder.append("x"); // version.getMicro()
				applicationVersion = builder.toString();
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return applicationVersion;
	}

	/**
	 * Use this method to get the default folder, e.g.:
	 * 
	 * folder = MyApp
	 * folder = .myapp
	 * 
	 * user.home/MyApp/0.9.x
	 * user.home/.myapp/0.9.x
	 * 
	 * @param folder
	 * @return String
	 */
	public static final String getDirectory(String folder) {

		/*
		 * Get the settings dir.
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(System.getProperty("user.home"));
		builder.append(File.separator);
		builder.append(folder);
		builder.append(File.separator);
		builder.append(getVersionIdentifier());
		return builder.toString();
	}

	/**
	 * If the application is not valid, null will be returned.
	 * 
	 * @param applicationName
	 * @return String
	 */
	public static final String getApplicationName() {

		String applicationName;
		/*
		 * Get application name from the JRE prefs.
		 */
		Properties properties = System.getProperties();
		Object name = properties.get(D_APPLICATION_NAME);
		if(name != null && name instanceof String setName) {
			applicationName = setName;
		} else {
			applicationName = DEFAULT_APPLICATION_NAME;
		}
		/*
		 * Replace unallowed characters.
		 * White space and others are not allowed.
		 */
		if(!applicationName.equals("")) {
			applicationName = applicationName.replace("-", "");
			applicationName = applicationName.replace("\\.", "");
			applicationName = applicationName.replace(":", "");
			applicationName = applicationName.replace(" ", "");
			applicationName = applicationName.replace("_", "");
		}
		return applicationName;
	}

	/**
	 * Tests if the values have been initialized.
	 * 
	 * @return boolean
	 */
	private static boolean isRunInitialization() {

		if(fileSettingsFolder == null || fileWorkspaceFolder == null) {
			return true;
		}
		return false;
	}

	/**
	 * Initializes the settings.
	 */
	private static void initialize() {

		/*
		 * Initialize the folder values.
		 */
		Properties properties = System.getProperties();
		Object workspaceFolder = properties.get(D_OSGI_INSTANCE_AREA);
		Object settingsFolder = properties.get(D_OSGI_USER_AREA);
		if(isSetDefaults(workspaceFolder, settingsFolder)) {
			/*
			 * Parse the application name.
			 * Set default folder.
			 * This is needed for JUnit tests in headless mode.
			 */
			String applicationName = getApplicationName();
			String applicationWorkspaceFolder = applicationName;
			String applicationSettingsFolder = "." + applicationName.toLowerCase();
			fileWorkspaceFolder = new File(getDirectory(applicationWorkspaceFolder));
			fileSettingsFolder = new File(getDirectory(applicationSettingsFolder));
		} else {
			/*
			 * Replace the file: prefix
			 */
			String applicationWorkspaceFolder = (String)workspaceFolder;
			applicationWorkspaceFolder = applicationWorkspaceFolder.replace("file:", "");
			String applicationSettingsFolder = (String)settingsFolder;
			applicationSettingsFolder = applicationSettingsFolder.replace("file:", "");
			fileWorkspaceFolder = new File(applicationWorkspaceFolder);
			fileSettingsFolder = new File(applicationSettingsFolder);
		}
	}

	/**
	 * Set default values?
	 * 
	 * @param workspaceFolder
	 * @param settingsFolder
	 * @return boolean
	 */
	private static boolean isSetDefaults(Object workspaceFolder, Object settingsFolder) {

		/*
		 * Null is not allowed.
		 */
		if(workspaceFolder == null || settingsFolder == null) {
			return true;
		}
		/*
		 * Object must be of instance String
		 */
		if(!(workspaceFolder instanceof String) || !(settingsFolder instanceof String)) {
			return true;
		}
		/*
		 * In case no VM argument has been set, create the defaults.
		 */
		String workspace = (String)workspaceFolder;
		String settings = (String)settingsFolder;
		if(workspace.equals("") || settings.equals("")) {
			return true;
		}
		/*
		 * Check if the product is run from the IDE product editor.
		 * Some plug-ins may fail if the workspace and settings path is too long.
		 * file:/home/.../0.9.x/runtime-....product/
		 */
		String regex = ".*runtime.*product/";
		if(workspace.matches(regex) || settings.matches(regex)) {
			return true;
		}
		/*
		 * Return false if no default shall be calculated.
		 */
		return false;
	}
}
