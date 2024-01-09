/*******************************************************************************
 * Copyright (c) 2014, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

public interface IPreferenceSupplier {

	static final Logger logger = Logger.getLogger(IPreferenceSupplier.class);

	/**
	 * Via the postfix, the key is modified dynamically.
	 * 
	 * @return {@link String}
	 */
	String getPostfix();

	/**
	 * Returns the scope context, e.g.:
	 * InstanceScope.INSTANCE
	 * 
	 * @return {@link IScopeContext}
	 */
	IScopeContext getScopeContext();

	/**
	 * Returns the preference node, e.g.:
	 * Activator.getContext().getBundle().getSymbolicName();
	 * 
	 * @return String
	 */
	String getPreferenceNode();

	/**
	 * Initialize the default values.
	 */
	void initializeDefaults();

	/**
	 * Puts the default value to the map.
	 * 
	 * @param key
	 * @param def
	 */
	void putDefault(String key, Object def);

	/**
	 * Returns a map of default values
	 * that can be used to initialize the values for the preference page.
	 * 
	 * public static final String P_VERSION = "version";
	 * public static final String DEF_VERSION = "1.0.0.0";
	 * 
	 * Map<String, String> defaultValues = new HashMap<String, String>();
	 * defaultValues.put(P_VERSION, DEF_VERSION);
	 * 
	 * @return {@link Map<String, String>}
	 */
	Map<String, String> getDefaultValues();

	/**
	 * Returns the eclipse preferences instance.
	 * Use the preferences as follows:
	 * 
	 * IEclipsePreferences preferences = SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
	 * String myPreference = preferences.get(P_STRING , DEF_STRING);
	 * 
	 * @return {@link IEclipsePreferences}
	 */
	IEclipsePreferences getPreferences();

	/**
	 * Call this method via AbstractPreferenceInitializer when initializeDefaultPreferences()
	 * is executed.
	 */
	default void persistDefaults() {

		IEclipsePreferences preferences = this.getPreferences();
		Map<String, String> defaultValues = getDefaultValues();
		for(Map.Entry<String, String> entry : defaultValues.entrySet()) {
			/*
			 * Add if the entry doesn't exists already.
			 */
			String key = getKey(entry.getKey());
			if(null == preferences.get(key, null)) {
				preferences.put(key, entry.getValue());
			}
		}
		/*
		 * Flush the preferences.
		 */
		try {
			preferences.flush();
		} catch(BackingStoreException e) {
			// can't flush then
		}
	}

	/*
	 * Handle separate settings
	 */
	default String getKey(String key) {

		String postfix = getPostfix();
		if(postfix.isEmpty()) {
			return key;
		} else {
			return key + postfix;
		}
	}

	default boolean getBoolean(String key) {

		String def = getDefaultValues().getOrDefault(key, "false");
		return getBoolean(key, Boolean.valueOf(def));
	}

	default boolean getBoolean(String key, boolean def) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getBoolean(getKey(key), def);
	}

	default void setBoolean(String key, boolean value) {

		putBoolean(key, value);
	}

	default void putBoolean(String key, boolean value) {

		put(getKey(key), Boolean.toString(value));
	}

	default byte getByte(String key) {

		try {
			String def = getDefaultValues().getOrDefault(key, "0");
			return getByte(key, Byte.valueOf(def));
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	default byte getByte(String key, int def) {

		IEclipsePreferences preferences = this.getPreferences();
		return (byte)preferences.getInt(getKey(key), def);
	}

	default void setByte(String key, byte value) {

		putByte(key, value);
	}

	default void putByte(String key, byte value) {

		put(getKey(key), Byte.toString(value));
	}

	default int getInteger(String key) {

		try {
			String def = getDefaultValues().getOrDefault(key, "0");
			return getInteger(key, Integer.valueOf(def));
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	default int getInteger(String key, int def) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getInt(getKey(key), def);
	}

	default void setInteger(String key, int value) {

		putInteger(key, value);
	}

	default void putInteger(String key, int value) {

		put(getKey(key), Integer.toString(value));
	}

	default float getFloat(String key) {

		try {
			String def = getDefaultValues().getOrDefault(key, "0");
			return getFloat(key, Float.valueOf(def));
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	default float getFloat(String key, float def) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getFloat(getKey(key), def);
	}

	default void setFloat(String key, float value) {

		putFloat(key, value);
	}

	default void putFloat(String key, float value) {

		put(getKey(key), Float.toString(value));
	}

	default double getDouble(String key) {

		try {
			String def = getDefaultValues().getOrDefault(key, "0");
			return getDouble(key, Double.valueOf(def));
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	default double getDouble(String key, double def) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getDouble(getKey(key), def);
	}

	default void setDouble(String key, double value) {

		putDouble(key, value);
	}

	default void putDouble(String key, double value) {

		put(getKey(key), Double.toString(value));
	}

	default String get(String key) {

		String def = getDefaultValues().getOrDefault(key, "");
		return get(key, def);
	}

	default String get(String key, String def) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.get(getKey(key), def);
	}

	default void set(String key, String value) {

		put(key, value);
	}

	default void put(String key, String value) {

		try {
			IEclipsePreferences preferences = this.getPreferences();
			preferences.put(getKey(key), value);
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}