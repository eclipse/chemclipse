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

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.internal.preferences.PreferenceSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.settings.PeakFilterSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	/*
	 * See id for extension point in plugin.xml file.
	 */
	public static final String FILTER_ID_CHROMATOGRAM = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.chromatogram";
	public static final String FILTER_ID_PEAK = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.peak";

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

	}

	public static ChromatogramFilterSettings getFilterSettings() {

		ChromatogramFilterSettings subtractFilterSettingsChromatogram = new ChromatogramFilterSettings();
		subtractFilterSettingsChromatogram.setSubtractMassSpectrum(PreferenceSupplierMSD.getSessionSubtractMassSpectrumAsString());
		subtractFilterSettingsChromatogram.setUseNominalMasses(PreferenceSupplierMSD.isUseNominalMZ());
		subtractFilterSettingsChromatogram.setUseNormalize(PreferenceSupplierMSD.isUseNormalizedScan());
		//
		return subtractFilterSettingsChromatogram;
	}

	public static PeakFilterSettings getPeakFilterSettings() {

		PeakFilterSettings peakFilterSettings = new PeakFilterSettings();
		peakFilterSettings.setSubtractMassSpectrum(PreferenceSupplierMSD.getSessionSubtractMassSpectrumAsString());
		peakFilterSettings.setUseNominalMasses(PreferenceSupplierMSD.isUseNominalMZ());
		peakFilterSettings.setUseNormalize(PreferenceSupplierMSD.isUseNormalizedScan());
		//
		return peakFilterSettings;
	}

	public static MassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		MassSpectrumFilterSettings massSpectrumFilterSettings = new MassSpectrumFilterSettings();
		massSpectrumFilterSettings.setSubtractMassSpectrum(PreferenceSupplierMSD.getSessionSubtractMassSpectrumAsString());
		massSpectrumFilterSettings.setUseNominalMasses(PreferenceSupplierMSD.isUseNominalMZ());
		massSpectrumFilterSettings.setUseNormalize(PreferenceSupplierMSD.isUseNormalizedScan());
		//
		return massSpectrumFilterSettings;
	}
}