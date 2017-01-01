/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
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
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IIonRemoverMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IIonRemoverPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IonRemoverMassSpectrumFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.IonRemoverPeakFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.util.IonListUtil;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IONS_TO_REMOVE = "ionsToRemove";
	public static final String P_USE_SETTINGS = "useSettings";
	public static final String DEF_IONS_TO_REMOVE = "18;28;84;207";
	public static final boolean DEF_USE_SETTINGS = true;
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
		defaultValues.put(P_USE_SETTINGS, Boolean.toString(DEF_USE_SETTINGS));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	/**
	 * Returns the chromatogram filter settings.
	 * 
	 * @return IChromatogramFilterSettings
	 */
	public static IChromatogramFilterSettings getChromatogramFilterSettings() {

		ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
		/*
		 * Set the ions that shall be removed in every case.
		 */
		IMarkedIons ionsToRemove = chromatogramFilterSettings.getIonsToRemove();
		PreferenceSupplier.setMarkedIons(ionsToRemove, getIons(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return chromatogramFilterSettings;
	}

	public static IPeakFilterSettings getPeakFilterSettings() {

		IIonRemoverPeakFilterSettings peakFilterSettings = new IonRemoverPeakFilterSettings();
		/*
		 * Set the ions that shall be removed in every case.
		 */
		IMarkedIons ionsToRemove = peakFilterSettings.getIonsToRemove();
		PreferenceSupplier.setMarkedIons(ionsToRemove, getIons(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return peakFilterSettings;
	}

	public static IMassSpectrumFilterSettings getMassSpectrumFilterSettings() {

		IIonRemoverMassSpectrumFilterSettings massSpectrumFilterSettings = new IonRemoverMassSpectrumFilterSettings();
		/*
		 * Set the ions that shall be removed in every case.
		 */
		IMarkedIons ionsToRemove = massSpectrumFilterSettings.getIonsToRemove();
		PreferenceSupplier.setMarkedIons(ionsToRemove, getIons(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		return massSpectrumFilterSettings;
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
	 * @return Set<Integer>
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

	/**
	 * Returns whether to use the settings or the chromatogram multi-page
	 * (excluded ion) settings.
	 * 
	 * @return
	 */
	public static boolean useSettings() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_SETTINGS, DEF_USE_SETTINGS);
	}

	/**
	 * Returns a list of ions stored in the settings.
	 * 
	 * @return Set<Integer>
	 */
	public static Set<Integer> getIons() {

		return PreferenceSupplier.getIons(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE);
	}
}
