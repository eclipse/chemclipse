/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.profiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

public class Profiles {

	private static final Logger logger = Logger.getLogger(Profiles.class);
	private static final String PACKAGE = "org.eclipse.chemclipse.rcp.app";
	private static final String PROFILES = "profiles";

	private Profiles() {
	}

	public static File[] getAvailableProfiles() {

		/*
		 * Create the directory if it not exists.
		 */
		File directory = new File(getProfileDirectory());
		if(!directory.exists()) {
			if(!directory.mkdirs()) {
				logger.warn("The temporarily identifier directory could not be created: " + directory.getAbsolutePath());
			}
		}
		//
		return directory.listFiles();
	}

	public static void createProfile(String name, boolean override) throws FileNotFoundException, CoreException {

		File file = new File(getProfileDirectory() + File.separator + name);
		if(file.exists()) {
			if(override) {
				exportProfile(file);
			}
		} else {
			exportProfile(file);
		}
	}

	public static void loadProfile(String name) throws FileNotFoundException, CoreException {

		File file = new File(getProfileDirectory() + File.separator + name);
		importProfile(file);
	}

	public static void deleteProfile(String name) {

		File file = new File(getProfileDirectory() + File.separator + name);
		file.delete();
	}

	/**
	 * Exports the current profile to the given file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
	public static void exportProfile(File file) throws FileNotFoundException, CoreException {

		IPreferenceFilter[] preferenceFilters = new IPreferenceFilter[1];
		preferenceFilters[0] = new IPreferenceFilter() {

			@Override
			public String[] getScopes() {

				return new String[]{InstanceScope.SCOPE, ConfigurationScope.SCOPE};
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Map getMapping(String scope) {

				return null;
			}
		};
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		IPreferencesService preferencesService = Platform.getPreferencesService();
		preferencesService.exportPreferences(preferencesService.getRootNode(), preferenceFilters, fileOutputStream);
	}

	/**
	 * Imports the profile given by the file.
	 * 
	 * @param file
	 * @return IStatus
	 * @throws FileNotFoundException
	 * @throws CoreException
	 */
	public static IStatus importProfile(File file) throws FileNotFoundException, CoreException {

		FileInputStream fileInputStream = new FileInputStream(file);
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.importPreferences(fileInputStream);
	}

	private static String getProfileDirectory() {

		return ApplicationSettings.getSettingsDirectory().getAbsolutePath() + File.separator + PACKAGE + File.separator + PROFILES;
	}
}
