/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.Activator;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.filter.impl.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_START_RETENTION_TIME = "startRetentionTime";
	public static final int DEF_START_RETENTION_TIME = 1;
	public static final String P_STOP_RETENTION_TIME = "stopRetentionTime";
	public static final int DEF_STOP_RETENTION_TIME = 1;
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

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_START_RETENTION_TIME, Integer.toString(DEF_START_RETENTION_TIME));
		defaultValues.put(P_STOP_RETENTION_TIME, Integer.toString(DEF_STOP_RETENTION_TIME));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ISupplierFilterSettings getSupplierFilterSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ISupplierFilterSettings filterSettings = new SupplierFilterSettings();
		filterSettings.setStartRetentionTime(preferences.getInt(P_START_RETENTION_TIME, DEF_START_RETENTION_TIME));
		filterSettings.setStartRetentionTime(preferences.getInt(P_STOP_RETENTION_TIME, DEF_STOP_RETENTION_TIME));
		return filterSettings;
	}
}
