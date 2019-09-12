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

import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.serialization.JSONSerialization;
import org.eclipse.chemclipse.support.settings.serialization.SettingsSerialization;

/**
 * Represents user preference for a processor
 * 
 * @author Christoph Läubrich
 *
 */
public interface ProcessorPreferences<SettingType> {

	public static final SettingsSerialization DEFAULT_SETTINGS_SERIALIZATION = new JSONSerialization();

	public enum DialogBehavior {
		/**
		 * The user should be queried each time
		 */
		SHOW,
		/**
		 * Saved defaults should be used
		 */
		SAVED_DEFAULTS,
		/**
		 * Dialogs should only appear on explicit request
		 */
		NONE;
	}

	/**
	 * 
	 * @return the dialog behavior
	 */
	DialogBehavior getDialogBehaviour();

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
	default SettingsSerialization getSerialization() {

		return DEFAULT_SETTINGS_SERIALIZATION;
	}

	/**
	 * constructs a new settings instance from the current user settings
	 * 
	 * @param settingsClass
	 * @return the currently stored usersettings for this processor
	 * @throws IOException
	 */
	default SettingType getUserSettings() throws IOException {

		String serializedString = getUserSettingsAsString();
		Class<SettingType> settingsClass = getSupplier().getSettingsClass();
		if(serializedString == null || settingsClass == null) {
			return null;
		}
		return getSerialization().fromString(settingsClass, serializedString);
	}

	default SettingType getSystemSettings() throws IOException {

		IProcessSupplier<SettingType> supplier = getSupplier();
		SystemSettingsStrategy strategy = supplier.getSettingsParser().getSystemSettingsStrategy();
		if(strategy == SystemSettingsStrategy.NEW_INSTANCE) {
			Class<SettingType> settingsClass = supplier.getSettingsClass();
			if(settingsClass != null) {
				try {
					return settingsClass.newInstance();
				} catch(InstantiationException | IllegalAccessException e) {
					throw new IOException("can't create settings instance: " + e.getMessage(), e);
				}
			}
		}
		return null;
	}

	default SettingType getSettings() throws IOException {

		if(isUseSystemDefaults()) {
			return getSystemSettings();
		} else {
			return getUserSettings();
		}
	}

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
}
