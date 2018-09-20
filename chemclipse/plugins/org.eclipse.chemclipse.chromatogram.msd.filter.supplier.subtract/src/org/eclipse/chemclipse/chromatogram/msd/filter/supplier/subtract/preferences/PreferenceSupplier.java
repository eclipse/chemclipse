/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.FilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ISubtractFilterSettingsMassSpectrum;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ISubtractFilterSettingsPeak;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.SubtractFilterSettingsMassSpectrum;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.SubtractFilterSettingsPeak;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	/*
	 * See id for extension point in plugin.xml file.
	 */
	public static final String FILTER_ID_CHROMATOGRAM = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.chromatogram";
	public static final String FILTER_ID_PEAK = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.peak";
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
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettings getFilterSettings() {

		FilterSettings subtractFilterSettingsChromatogram = new FilterSettings();
		subtractFilterSettingsChromatogram.setSubtractMassSpectrum(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrumAsString());
		subtractFilterSettingsChromatogram.setUseNominalMasses(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ());
		subtractFilterSettingsChromatogram.setUseNormalize(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan());
		//
		return subtractFilterSettingsChromatogram;
	}

	/**
	 * Returns the default peak filter settings.
	 * 
	 * @return {@link ISupplierFilterSettings}
	 */
	public static ISubtractFilterSettingsPeak getPeakFilterSettings() {

		ISubtractFilterSettingsPeak subtractFilterSettingsPeak = new SubtractFilterSettingsPeak();
		/*
		 * The session mass spectrum is used.
		 * It can be edited by the user.
		 */
		subtractFilterSettingsPeak.setSubtractMassSpectrum(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrum());
		subtractFilterSettingsPeak.setUseNominalMasses(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ());
		subtractFilterSettingsPeak.setUseNormalize(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan());
		//
		return subtractFilterSettingsPeak;
	}

	public static ISubtractFilterSettingsMassSpectrum getMassSpectrumFilterSettings() {

		ISubtractFilterSettingsMassSpectrum subtractFilterSettingsMassSpectrum = new SubtractFilterSettingsMassSpectrum();
		/*
		 * The session mass spectrum is used.
		 * It can be edited by the user.
		 */
		subtractFilterSettingsMassSpectrum.setSubtractMassSpectrum(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrum());
		subtractFilterSettingsMassSpectrum.setUseNominalMasses(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ());
		subtractFilterSettingsMassSpectrum.setUseNormalize(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan());
		//
		return subtractFilterSettingsMassSpectrum;
	}
}
