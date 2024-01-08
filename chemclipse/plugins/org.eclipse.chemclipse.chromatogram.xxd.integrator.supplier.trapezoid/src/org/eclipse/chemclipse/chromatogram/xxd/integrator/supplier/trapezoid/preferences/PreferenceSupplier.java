/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.ChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.CombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.TraceSettingUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SELECTED_IONS = "selectedIons";
	public static final String DEF_SELECTED_IONS = "0"; // 103;104 | 0 = TIC
	public static final String P_PEAK_AREA_INCLUDE_BACKGROUND = "peakAreaIncludeBackground";
	public static final boolean DEF_PEAK_AREA_INCLUDE_BACKGROUND = false; // The background will be not calculated
	public static final String P_USE_AREA_CONSTRAINT = "useAreaConstraint";
	public static final boolean DEF_USE_AREA_CONSTRAINT = true;
	//
	public static final int MIN_RETENTION_TIME = 0; // = 0.0 minutes
	public static final int MAX_RETENTION_TIME = 60000; // = 1.0 minutes
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

		Map<String, String> defaultValues = new HashMap<>();
		defaultValues.put(P_SELECTED_IONS, DEF_SELECTED_IONS);
		defaultValues.put(P_PEAK_AREA_INCLUDE_BACKGROUND, Boolean.toString(DEF_PEAK_AREA_INCLUDE_BACKGROUND));
		defaultValues.put(P_USE_AREA_CONSTRAINT, Boolean.toString(DEF_USE_AREA_CONSTRAINT));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ChromatogramIntegrationSettings getChromatogramIntegrationSettings() {

		return new ChromatogramIntegrationSettings();
	}

	public static PeakIntegrationSettings getPeakIntegrationSettings() {

		PeakIntegrationSettings integrationSettings = new PeakIntegrationSettings();
		integrationSettings.setIncludeBackground(getPeakAreaIncludeBackground());
		integrationSettings.setUseAreaConstraint(isUseAreaConstraint());
		/*
		 * Integration Support
		 */
		IMarkedTraces<IMarkedTrace> markedTraces = integrationSettings.getMarkedTraces();
		String ions = PreferenceSupplier.getIons(P_SELECTED_IONS, DEF_SELECTED_IONS);
		TraceSettingUtil settingIon = new TraceSettingUtil();
		for(int trace : settingIon.extractTraces(settingIon.deserialize(ions))) {
			markedTraces.add(new MarkedIon(trace));
		}
		//
		return integrationSettings;
	}

	public static CombinedIntegrationSettings getCombinedIntegrationSettings() {

		return new CombinedIntegrationSettings(getChromatogramIntegrationSettings(), getPeakIntegrationSettings());
	}

	public static boolean getPeakAreaIncludeBackground() {

		return INSTANCE().getBoolean(P_PEAK_AREA_INCLUDE_BACKGROUND, DEF_PEAK_AREA_INCLUDE_BACKGROUND);
	}

	public static boolean isUseAreaConstraint() {

		return INSTANCE().getBoolean(P_USE_AREA_CONSTRAINT, DEF_USE_AREA_CONSTRAINT);
	}

	/**
	 * Returns a list of ions to preserve stored in the settings.
	 * 
	 * @return List<Integer>
	 */
	public static String getIons(String preference, String def) {

		/*
		 * E.g. "18;28;84;207" to 18 28 84 207
		 */
		return INSTANCE().get(preference, def);
	}
}