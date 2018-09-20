/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.FilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IIonRemoverMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IIonRemoverPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IonRemoverMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IonRemoverPeakFilterSettings;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IONS_TO_REMOVE = "ionsToRemove";
	public static final String DEF_IONS_TO_REMOVE = "18;28;84;207";
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
		defaultValues.put(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static FilterSettings getFilterSettings() {

		FilterSettings filterSettings = new FilterSettings();
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		filterSettings.setIonsToRemove(preferences.get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return filterSettings;
	}

	public static IPeakFilterSettings getPeakFilterSettings() {

		IIonRemoverPeakFilterSettings peakFilterSettings = new IonRemoverPeakFilterSettings();
		/*
		 * Set the ions that shall be removed in every case.
		 */
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		peakFilterSettings.setIonsToRemove(preferences.get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return peakFilterSettings;
	}

	public static IMassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		IIonRemoverMassSpectrumFilterSettings massSpectrumFilterSettings = new IonRemoverMassSpectrumFilterSettings();
		/*
		 * Set the ions that shall be removed in every case.
		 */
		IEclipsePreferences preferences = INSTANCE().getPreferences();
		massSpectrumFilterSettings.setIonsToRemove(preferences.get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return massSpectrumFilterSettings;
	}
}
