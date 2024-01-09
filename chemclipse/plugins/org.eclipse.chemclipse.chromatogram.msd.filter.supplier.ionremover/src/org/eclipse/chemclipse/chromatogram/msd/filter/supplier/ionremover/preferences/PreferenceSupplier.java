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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.MassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.PeakFilterSettings;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IONS_TO_REMOVE = "ionsToRemove";
	public static final String DEF_IONS_TO_REMOVE = "18 28 84 207";
	//
	private static IPreferenceSupplier preferenceSupplier = null;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE);
	}

	public static ChromatogramFilterSettings getFilterSettings() {

		ChromatogramFilterSettings filterSettings = new ChromatogramFilterSettings();
		filterSettings.setIonsToRemove(INSTANCE().get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return filterSettings;
	}

	public static PeakFilterSettings getPeakFilterSettings() {

		PeakFilterSettings peakFilterSettings = new PeakFilterSettings();
		/*
		 * Set the ions that shall be removed in every case.
		 */
		peakFilterSettings.setIonsToRemove(INSTANCE().get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return peakFilterSettings;
	}

	public static IMassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		MassSpectrumFilterSettings massSpectrumFilterSettings = new MassSpectrumFilterSettings();
		massSpectrumFilterSettings.setIonsToRemove(INSTANCE().get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return massSpectrumFilterSettings;
	}
}