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
	default void initialize() {

		IEclipsePreferences preferences = this.getPreferences();
		Map<String, String> defaultValues = getDefaultValues();
		for(Map.Entry<String, String> entry : defaultValues.entrySet()) {
			/*
			 * Add if the doesn't exists already.
			 */
			if(null == preferences.get(entry.getKey(), null)) {
				preferences.put(entry.getKey(), entry.getValue());
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

	default boolean getBoolean(String key, boolean value) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getBoolean(key, value);
	}

	default void putBoolean(String key, boolean value) {

		put(key, Boolean.toString(value));
	}

	default int getInteger(String key, int value) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getInt(key, value);
	}

	default void putInteger(String key, int value) {

		put(key, Integer.toString(value));
	}

	default float getFloat(String key, float value) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getFloat(key, value);
	}

	default void putFloat(String key, float value) {

		put(key, Float.toString(value));
	}

	default double getDouble(String key, double value) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.getDouble(key, value);
	}

	default void putDouble(String key, double value) {

		put(key, Double.toString(value));
	}

	default String get(String key, String value) {

		IEclipsePreferences preferences = this.getPreferences();
		return preferences.get(key, value);
	}

	default void put(String key, String value) {

		try {
			IEclipsePreferences preferences = this.getPreferences();
			preferences.put(key, value);
			preferences.flush();
		} catch(BackingStoreException e) {
			logger.warn(e);
		}
	}
}