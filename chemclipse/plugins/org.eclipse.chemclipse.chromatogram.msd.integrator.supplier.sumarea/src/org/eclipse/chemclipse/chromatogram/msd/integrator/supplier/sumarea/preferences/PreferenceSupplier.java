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
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.Activator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.sumarea.settings.SumareaIntegrationSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.IonListUtil;

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
	public static IChromatogramIntegrationSettings getIntegrationSettings() {

		SumareaIntegrationSettings integrationSettings = new SumareaIntegrationSettings();
		IMarkedIons selectedIons = integrationSettings.getSelectedIons();
		setMarkedIons(selectedIons, getIons(P_SELECTED_IONS, DEF_SELECTED_IONS));
		return integrationSettings;
	}

	/**
	 * Sets the ions stored in the list to the marked ions
	 * instance.
	 * 
	 * @param markedIons
	 * @param ions
	 */
	public static void setMarkedIons(IMarkedIons markedIons, Set<Integer> ions) {

		for(int ion : ions) {
			markedIons.add(new MarkedIon(ion));
		}
	}

	/**
	 * Returns a list of ions to preserve stored in the settings.
	 * 
	 * @return List<Integer>
	 */
	public static Set<Integer> getIons(String preference, String def) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		/*
		 * E.g. "18;28;84;207" to 18 28 84 207
		 */
		IonListUtil ionListUtil = new IonListUtil();
		String preferenceEntry = preferences.get(preference, def);
		return ionListUtil.getIons(preferenceEntry);
	}
}
