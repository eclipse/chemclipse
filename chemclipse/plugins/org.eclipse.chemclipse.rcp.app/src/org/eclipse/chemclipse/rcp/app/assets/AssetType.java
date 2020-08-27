/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.assets;

import java.io.File;

import org.eclipse.chemclipse.logging.support.Settings;

public enum AssetType {
	CONFIGURATION("Configuration", ".cfg", Settings.getSystemConfigDirectory(), "Service Configuration File"), //
	METHOD("Process Method", ".ocm", Settings.getSystemMethodDirectory(), "Process Method File"), //
	PLUGIN("Plugin", ".jar", Settings.getSystemPluginDirectory(), "Plugin Extension");

	private String label;
	private String extension;
	private File directory;
	private String description;

	private AssetType(String label, String extension, File directory, String description) {

		this.label = label;
		this.extension = extension;
		this.directory = directory;
		this.description = description;
	}

	/**
	 * Returns the label.
	 * 
	 * @return {String}
	 */
	public String getLabel() {

		return label;
	}

	/**
	 * Returns the file extension.
	 * 
	 * @return {String}
	 */
	public String getExtension() {

		return extension;
	}

	/**
	 * Return the installation directory.
	 * 
	 * @return {File}
	 */
	public File getDirectory() {

		return directory;
	}

	/**
	 * Returns the description.
	 * 
	 * @return {String}
	 */
	public String getDescription() {

		return description;
	}
}
