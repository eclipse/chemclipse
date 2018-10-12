/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.Activator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SELECTED_IONS = "selectedIons";
	public static final String DEF_SELECTED_IONS = ""; // none selected means integrate all ion
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
		defaultValues.put(P_SELECTED_IONS, DEF_SELECTED_IONS);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns a settings instance.
	 */
	public static ChromatogramIntegrationSettings getIntegrationSettings() {

		ChromatogramIntegrationSettings integrationSettings = new ChromatogramIntegrationSettings();
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String ions = preferences.get(P_SELECTED_IONS, DEF_SELECTED_IONS);
		integrationSettings.setSelectedIons(ions);
		return integrationSettings;
	}
}
