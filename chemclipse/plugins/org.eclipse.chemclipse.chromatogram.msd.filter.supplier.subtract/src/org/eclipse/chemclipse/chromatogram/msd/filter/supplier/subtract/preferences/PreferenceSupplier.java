/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.PeakFilterSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

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

	public static ChromatogramFilterSettings getFilterSettings() {

		ChromatogramFilterSettings subtractFilterSettingsChromatogram = new ChromatogramFilterSettings();
		subtractFilterSettingsChromatogram.setSubtractMassSpectrum(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrumAsString());
		subtractFilterSettingsChromatogram.setUseNominalMasses(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ());
		subtractFilterSettingsChromatogram.setUseNormalize(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan());
		//
		return subtractFilterSettingsChromatogram;
	}

	public static PeakFilterSettings getPeakFilterSettings() {

		PeakFilterSettings peakFilterSettings = new PeakFilterSettings();
		peakFilterSettings.setSubtractMassSpectrum(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrumAsString());
		peakFilterSettings.setUseNominalMasses(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ());
		peakFilterSettings.setUseNormalize(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan());
		//
		return peakFilterSettings;
	}

	public static MassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		MassSpectrumFilterSettings massSpectrumFilterSettings = new MassSpectrumFilterSettings();
		massSpectrumFilterSettings.setSubtractMassSpectrum(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.getSessionSubtractMassSpectrumAsString());
		massSpectrumFilterSettings.setUseNominalMasses(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNominalMZ());
		massSpectrumFilterSettings.setUseNormalize(org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier.isUseNormalizedScan());
		//
		return massSpectrumFilterSettings;
	}
}