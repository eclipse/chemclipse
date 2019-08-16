/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

import org.eclipse.chemclipse.support.settings.PreferenceProperty;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class PreferencePropertyUtils {

	public static String getPreference(PreferenceProperty preferenceProperty, String def) {

		if(preferenceProperty == null) {
			return def;
		}
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getString(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static int getPreference(PreferenceProperty preferenceProperty, int def) {

		if(preferenceProperty == null) {
			return def;
		}
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getInt(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static double getPreference(PreferenceProperty preferenceProperty, double def) {

		if(preferenceProperty == null) {
			return def;
		}
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getDouble(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static boolean getPreference(PreferenceProperty preferenceProperty, boolean def) {

		if(preferenceProperty == null) {
			return def;
		}
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getBoolean(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static float getPreference(PreferenceProperty preferenceProperty, float def) {

		if(preferenceProperty == null) {
			return def;
		}
		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getFloat(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}
}