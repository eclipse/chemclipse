/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.support;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

// TODO JUnit
public class DatabasePathSupport {

	private static final Logger logger = Logger.getLogger(DatabasePathSupport.class);
	public static final String PLUGIN_NAME = "org.eclipse.chemclipse.chromatogram.msd.identifier";
	public static final String EMBEDDED_DATABASES = "embeddedDatabases";

	/**
	 * Returns the user specific file object (directory) where the database
	 * instances can be stored.
	 * 
	 * @return File
	 */
	public static File getDatabaseStoragePath() {

		File file = new File(ApplicationSettings.getSettingsDirectory().getAbsolutePath() + File.separator + PLUGIN_NAME + File.separator + EMBEDDED_DATABASES);
		/*
		 * Create the directory if it not exists.
		 */
		if(!file.exists()) {
			if(!file.mkdirs()) {
				logger.warn("The temporarily database file directory could not be created: " + file.getAbsolutePath());
			}
		}
		return file;
	}
}
