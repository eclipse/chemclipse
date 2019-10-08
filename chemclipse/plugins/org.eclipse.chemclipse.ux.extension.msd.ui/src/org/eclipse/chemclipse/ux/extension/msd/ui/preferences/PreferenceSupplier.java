/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.preferences;

import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.ux.extension.msd.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceSupplier {

	public static final String P_SELECTED_ORGANIC_COMPOUND = "selectedOrganicCompound";
	//
	public static final String P_ORGANIC_COMPOUND_HYDROCARBONS = "organicCompoundHydrocarbons";
	public static final String P_ORGANIC_COMPOUND_FATTY_ACIDS = "organicCompoundFattyAcids";
	public static final String P_ORGANIC_COMPOUND_FAME = "organicCompoundFattyAcidsAsMethylEsters";
	//
	public static final String P_MAGNIFICATION_FACTOR = "magnificationFactor";
	public static final int DEF_MAGNIFICATION_FACTOR = 1;
	public static final int DEF_MAGNIFICATION_FACTOR_MIN = 1;
	public static final int DEF_MAGNIFICATION_FACTOR_MAX = 50;
	//
	private static IMarkedIons compoundIonsEmpty = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
	private static int magnificationFactor = -1;
	private static IMarkedIons compoundIonsHydrocarbons = null;
	private static IMarkedIons compoundIonsFattyAcids = null;
	private static IMarkedIons compoundIonsFame = null;
	//
	public static final String P_USE_PROFILE_MASS_SPECTRUM_VIEW = "useProfileMassSpectrumView";
	public static final boolean DEF_USE_PROFILE_MASS_SPECTRUM_VIEW = false;
	//
	public static final String P_PATH_OPEN_CHROMATOGRAMS = "pathOpenChromatograms";
	public static final String DEF_PATH_OPEN_CHROMATOGRAMS = "";

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {
	}

	public static String[][] getOrganicCompoundPresets() {

		String[][] organicCompoundPresets = new String[3][2];
		organicCompoundPresets[0] = new String[]{"Hydrocarbons", P_ORGANIC_COMPOUND_HYDROCARBONS};
		organicCompoundPresets[1] = new String[]{"Fatty Acids", P_ORGANIC_COMPOUND_FATTY_ACIDS};
		organicCompoundPresets[2] = new String[]{"FAME", P_ORGANIC_COMPOUND_FAME};
		return organicCompoundPresets;
	}

	public static IMarkedIons getOrganicCompoundIons() {

		IMarkedIons compoundIons;
		//
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String selectedOrganicCompound = store.getString(PreferenceSupplier.P_SELECTED_ORGANIC_COMPOUND);
		/*
		 * Set the compounds to null if the magnification factor
		 * has been edited.
		 */
		if(magnificationFactor != store.getInt(PreferenceSupplier.P_MAGNIFICATION_FACTOR)) {
			compoundIonsHydrocarbons = null;
			compoundIonsFattyAcids = null;
			compoundIonsFame = null;
			magnificationFactor = store.getInt(PreferenceSupplier.P_MAGNIFICATION_FACTOR);
		}
		//
		if(selectedOrganicCompound.equals(P_ORGANIC_COMPOUND_HYDROCARBONS)) {
			/*
			 * Hydrocarbons
			 */
			if(compoundIonsHydrocarbons == null) {
				compoundIonsHydrocarbons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
				compoundIonsHydrocarbons.add(new MarkedIon(57, magnificationFactor));
				compoundIonsHydrocarbons.add(new MarkedIon(71, magnificationFactor));
				compoundIonsHydrocarbons.add(new MarkedIon(85, magnificationFactor));
			}
			compoundIons = compoundIonsHydrocarbons;
		} else if(selectedOrganicCompound.equals(P_ORGANIC_COMPOUND_FATTY_ACIDS)) {
			/*
			 * Fatty acids
			 */
			if(compoundIonsFattyAcids == null) {
				compoundIonsFattyAcids = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
				compoundIonsFattyAcids.add(new MarkedIon(74, magnificationFactor));
				compoundIonsFattyAcids.add(new MarkedIon(87, magnificationFactor));
			}
			compoundIons = compoundIonsFattyAcids;
		} else if(selectedOrganicCompound.equals(P_ORGANIC_COMPOUND_FAME)) {
			/*
			 * FAME
			 */
			if(compoundIonsFame == null) {
				compoundIonsFame = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
				compoundIonsFame.add(new MarkedIon(79, magnificationFactor));
				compoundIonsFame.add(new MarkedIon(81, magnificationFactor));
			}
			compoundIons = compoundIonsFame;
		} else {
			/*
			 * No ions.
			 */
			compoundIons = compoundIonsEmpty;
		}
		return compoundIons;
	}

	/**
	 * Returns the x offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayXOffset() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_OVERLAY_X_OFFSET);
	}

	/**
	 * Returns the y offset value.
	 * 
	 * @return int
	 */
	public static int getOverlayYOffset() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(PreferenceConstants.P_OVERLAY_Y_OFFSET);
	}

	public static boolean useProfileMassSpectrumView() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getBoolean(P_USE_PROFILE_MASS_SPECTRUM_VIEW);
	}

	public static String getPathOpenChromatograms() {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(P_PATH_OPEN_CHROMATOGRAMS);
	}

	public static void setPathOpenChromatograms(String value) {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(P_PATH_OPEN_CHROMATOGRAMS, value);
	}
}
