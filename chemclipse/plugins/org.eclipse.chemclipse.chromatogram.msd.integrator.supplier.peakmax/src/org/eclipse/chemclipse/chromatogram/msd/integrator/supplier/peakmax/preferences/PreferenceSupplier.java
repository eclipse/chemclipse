/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.preferences;

import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.Activator;
import org.eclipse.chemclipse.chromatogram.msd.integrator.supplier.peakmax.settings.PeakIntegrationSettings;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.TraceSettingUtil;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_SELECTED_IONS = "selectedIons";
	public static final String DEF_SELECTED_IONS = "0"; // 103;104 | 0 = TIC
	public static final String P_USE_AREA_CONSTRAINT = "useAreaConstraint";
	public static final boolean DEF_USE_AREA_CONSTRAINT = true;
	//
	public static final int MIN_RETENTION_TIME = 0; // = 0.0 minutes
	public static final int MAX_RETENTION_TIME = 60000; // = 1.0 minutes

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_SELECTED_IONS, DEF_SELECTED_IONS);
		putDefault(P_USE_AREA_CONSTRAINT, Boolean.toString(DEF_USE_AREA_CONSTRAINT));
	}

	public static PeakIntegrationSettings getPeakIntegrationSettings() {

		PeakIntegrationSettings peakIntegrationSettings = new PeakIntegrationSettings();
		peakIntegrationSettings.setUseAreaConstraint(isUseAreaConstraint());
		// baselineSupport.setBaselineHoldOn(5000, 100000);
		/*
		 * Integration Support
		 */
		IMarkedTraces<IMarkedTrace> markedTraces = peakIntegrationSettings.getMarkedTraces();
		String ions = PreferenceSupplier.getIons(P_SELECTED_IONS, DEF_SELECTED_IONS);
		TraceSettingUtil settingIon = new TraceSettingUtil();
		for(int trace : settingIon.extractTraces(settingIon.deserialize(ions))) {
			markedTraces.add(new MarkedIon(trace));
		}
		//
		return peakIntegrationSettings;
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

	public static boolean isUseAreaConstraint() {

		return INSTANCE().getBoolean(P_USE_AREA_CONSTRAINT, DEF_USE_AREA_CONSTRAINT);
	}
}