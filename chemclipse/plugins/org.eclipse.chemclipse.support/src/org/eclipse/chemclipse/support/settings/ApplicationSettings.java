/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

import java.io.File;

import org.eclipse.chemclipse.logging.support.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationSettings {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationSettings.class);

	public static File getSystemTmpDirectory() {

		return new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Returns a file object (directory) of the chemclipse settings folder.
	 * 
	 * @return File
	 */
	public static File getSettingsDirectory() {

		/*
		 * Create the directory if it not exists.
		 */
		File file = Settings.getSystemDirectory();
		createDirectoryIfNotExists(file);
		return file;
	}

	public static File getWorkspaceDirectory() {

		/*
		 * Create the directory if it not exists.
		 */
		File file = Settings.getWorkspaceDirectory();
		createDirectoryIfNotExists(file);
		return file;
	}

	private static void createDirectoryIfNotExists(File file) {

		if(!file.exists()) {
			if(!file.mkdir()) {
				logger.warn("The directory could not be created: " + file.getAbsolutePath());
			}
		}
	}
}
