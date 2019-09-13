/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

public enum SystemSettingsStrategy {
	/**
	 * The settingsclass has no definition of system settings at all
	 */
	NONE,
	/**
	 * passing <code>null</code> to a processor using this settings will trigger the usage of the system-settings
	 */
	NULL,
	/**
	 * creating a new instance of the settingsclass will be initialized with the system-settings
	 */
	NEW_INSTANCE,
	/**
	 * There is some dynamic way to determine if system settings are available or not
	 */
	DYNAMIC;
};