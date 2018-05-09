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

	public static String getStringPreference(PreferenceProperty preferenceProperty, String def) {

		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getString(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static int getIntPreference(PreferenceProperty preferenceProperty, int def) {

		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getInt(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static double getDoublePreference(PreferenceProperty preferenceProperty, double def) {

		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getDouble(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}

	public static float getFloatPreference(PreferenceProperty preferenceProperty, float def) {

		IPreferencesService preferencesService = Platform.getPreferencesService();
		return preferencesService.getFloat(preferenceProperty.qualifier(), preferenceProperty.key(), def, null);
	}
}