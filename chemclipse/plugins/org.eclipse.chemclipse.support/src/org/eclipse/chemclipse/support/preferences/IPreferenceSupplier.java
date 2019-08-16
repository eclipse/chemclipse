/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.preferences;

import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

public interface IPreferenceSupplier {

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
}
