/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.Activator;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_MISC_SEPARATOR = "miscSeparator";
	public static final String DEF_MISC_SEPARATOR = "!";
	public static final String P_MISC_SEPARATED_DELIMITER = "miscSeparatedDelimiter";
	public static final String DEF_MISC_SEPARATED_DELIMITER = " ";
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getDefault().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_MISC_SEPARATOR, DEF_MISC_SEPARATOR);
		defaultValues.put(P_MISC_SEPARATED_DELIMITER, DEF_MISC_SEPARATED_DELIMITER);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static String getMiscSeparator() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_MISC_SEPARATOR, DEF_MISC_SEPARATOR);
	}

	public static String getMiscSeparatedDelimiter() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.get(P_MISC_SEPARATED_DELIMITER, DEF_MISC_SEPARATED_DELIMITER);
	}
}
