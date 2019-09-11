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
package org.eclipse.chemclipse.xxd.process.support;

import java.io.IOException;

import org.eclipse.chemclipse.support.settings.serialization.SettingsSerialization;

/**
 * Represents user preference for a processor
 * 
 * @author Christoph Läubrich
 *
 */
public interface ProcessorPreferences<SettingType> {

	/**
	 * 
	 * @return <code>true</code> if the user should be queried for the settings each time
	 */
	boolean isAskForSettings();

	void setAskForSettings(boolean askForSettings);

	void setUserSettings(String settings);

	/**
	 * 
	 * @return <code>true</code> if this processor want to use system settings by default
	 */
	boolean isUseSystemDefaults();

	void setUseSystemDefaults(boolean useSystemDefaults);

	/**
	 * reset this preferences deleting all saved values
	 */
	void reset();

	/**
	 * 
	 * @return the serialization used for the user settings
	 */
	SettingsSerialization getSerialization();

	/**
	 * constructs a new settings instance from the current user settings
	 * 
	 * @param settingsClass
	 * @return the currently stored usersettings for this processor
	 * @throws IOException
	 */
	SettingType getUserSettings() throws IOException;

	/**
	 * 
	 * @return the corresponding supplier
	 */
	IProcessSupplier<SettingType> getSupplier();

	/**
	 * 
	 * @return the user settings serialized as a string suitable for use with {@link #getSettings(String)}
	 */
	String getUserSettingsAsString();

	/**
	 * 
	 * @param serializedString
	 * @return the instance of settings representing the given string
	 * @throws IOException
	 */
	SettingType getSettings(String serializedString) throws IOException;
}
