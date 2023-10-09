/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

public interface IPreferenceSupplier {

	IPreferenceStore getPreferences();

	String getPostfix();

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

		IPreferenceStore preferences = this.getPreferences();
		return preferences.getBoolean(getKey(key));
	}

	default void setBoolean(String key, boolean value) {

		IPreferenceStore preferences = this.getPreferences();
		preferences.setValue(getKey(key), value);
	}

	default int getInteger(String key) {

		IPreferenceStore preferences = this.getPreferences();
		return preferences.getInt(getKey(key));
	}

	default void setInteger(String key, int value) {

		IPreferenceStore preferences = this.getPreferences();
		preferences.setValue(getKey(key), value);
	}

	default float getFloat(String key) {

		IPreferenceStore preferences = this.getPreferences();
		return preferences.getFloat(getKey(key));
	}

	default void setFloat(String key, float value) {

		IPreferenceStore preferences = this.getPreferences();
		preferences.setValue(getKey(key), value);
	}

	default double getDouble(String key) {

		IPreferenceStore preferences = this.getPreferences();
		return preferences.getDouble(getKey(key));
	}

	default void setDouble(String key, double value) {

		IPreferenceStore preferences = this.getPreferences();
		preferences.setValue(getKey(key), value);
	}

	default String get(String key) {

		IPreferenceStore preferences = this.getPreferences();
		return preferences.getString(getKey(key));
	}

	default void set(String key, String value) {

		IPreferenceStore preferences = this.getPreferences();
		preferences.setValue(getKey(key), value);
	}
}