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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IONS_TO_REMOVE = "ionsToRemove";
	public static final String P_IONS_TO_PRESERVE = "ionsToPreserve";
	public static final String P_SEGMENT_WIDTH = "segmentWidth";
	public static final String P_USE_CHROMATOGRAM_SPECIFIC_IONS = "useChromatogramSpecificIons";
	public static final String P_ADJUST_THRESHOLD_TRANSITIONS = "adjustThresholdTransitions";
	public static final String DEF_IONS_TO_REMOVE = "18;28;84;207";
	public static final String DEF_IONS_TO_PRESERVE = "103;104";
	public static final String DEF_SEGMENT_WIDTH = SegmentWidth.WIDTH_13.toString();
	public static final boolean DEF_USE_CHROMATOGRAM_SPECIFIC_IONS = false;
	public static final boolean DEF_ADJUST_THRESHOLD_TRANSITIONS = true;
	//
	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
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
		defaultValues.put(P_IONS_TO_PRESERVE, DEF_IONS_TO_PRESERVE);
		defaultValues.put(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH);
		defaultValues.put(P_USE_CHROMATOGRAM_SPECIFIC_IONS, Boolean.toString(DEF_USE_CHROMATOGRAM_SPECIFIC_IONS));
		defaultValues.put(P_ADJUST_THRESHOLD_TRANSITIONS, Boolean.toString(DEF_ADJUST_THRESHOLD_TRANSITIONS));
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

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		ISupplierFilterSettings chromatogramFilterSettings = new SupplierFilterSettings();
		/*
		 * Get the actual preference.
		 * If it's not available, a default value will be returned.
		 */
		chromatogramFilterSettings.setAdjustThresholdTransitions(preferences.getBoolean(P_ADJUST_THRESHOLD_TRANSITIONS, DEF_ADJUST_THRESHOLD_TRANSITIONS));
		chromatogramFilterSettings.setNumberOfUsedIonsForCoefficient(1);
		/*
		 * Set the ions that shall be removed in every case.
		 */
		IMarkedIons ionsToRemove = chromatogramFilterSettings.getIonsToRemove();
		PreferenceSupplier.setMarkedIons(ionsToRemove, getIons(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		/*
		 * Set the ions that shall be preserved in every case.
		 */
		IMarkedIons ionsToPreserve = chromatogramFilterSettings.getIonsToPreserve();
		PreferenceSupplier.setMarkedIons(ionsToPreserve, getIons(P_IONS_TO_PRESERVE, DEF_IONS_TO_PRESERVE));
		/*
		 * Set the segment width.
		 */
		SegmentWidth segmentWidth = SegmentWidth.valueOf(preferences.get(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH));
		chromatogramFilterSettings.setSegmentWidth(segmentWidth);
		return chromatogramFilterSettings;
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
		Set<Integer> ions = new HashSet<Integer>();
		String preferenceEntry = preferences.get(preference, def);
		if(preferenceEntry != "") {
			String[] items = parseString(preferenceEntry);
			if(items.length > 0) {
				Integer ion;
				for(String item : items) {
					try {
						ion = Integer.parseInt(item);
						ions.add(ion);
					} catch(NumberFormatException e) {
						logger.warn(e);
					}
				}
			}
		}
		return ions;
	}

	/**
	 * Returns a string array.<br/>
	 * E.g. "18;28;84;207" to 18 28 84 207
	 * 
	 * @param stringList
	 * @return String[]
	 */
	public static String[] parseString(String stringList) {

		String[] decodedArray;
		if(stringList.contains(";")) {
			StringTokenizer stringTokenizer = new StringTokenizer(stringList, ";");
			int arraySize = stringTokenizer.countTokens();
			decodedArray = new String[arraySize];
			for(int i = 0; i < arraySize; i++) {
				decodedArray[i] = stringTokenizer.nextToken(";");
			}
		} else {
			decodedArray = new String[1];
			decodedArray[0] = stringList;
		}
		return decodedArray;
	}

	/**
	 * Creates the settings strings.<br/>
	 * E.g. 18 28 84 207 to "18;28;84;207"
	 * 
	 * @param items
	 * @return String
	 */
	public static String createList(String[] items) {

		String ions = "";
		if(items != null) {
			int size = items.length;
			for(int i = 0; i < size; i++) {
				ions = ions.concat(items[i] + ";");
			}
		}
		return ions;
	}

	/**
	 * Returns whether to adjust threshold transitions or not.
	 * 
	 * @return boolean
	 */
	public static boolean adjustThresholdTransitions() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_ADJUST_THRESHOLD_TRANSITIONS, DEF_ADJUST_THRESHOLD_TRANSITIONS);
	}

	/**
	 * Returns whether to use the settings or the chromatogram multi-page
	 * (selected, excluded ions).
	 * 
	 * @return boolean
	 */
	public static boolean useChromatogramSpecificIons() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_CHROMATOGRAM_SPECIFIC_IONS, DEF_USE_CHROMATOGRAM_SPECIFIC_IONS);
	}

	/**
	 * Returns a list of ions to remove stored in the settings.
	 * 
	 * @return Set<Integer>
	 */
	public static Set<Integer> getIonsToRemove() {

		return PreferenceSupplier.getIons(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE);
	}

	/**
	 * Returns a list of ions to preserve stored in the settings.
	 * 
	 * @return Set<Integer>
	 */
	public static Set<Integer> getIonsToPreserve() {

		return PreferenceSupplier.getIons(P_IONS_TO_PRESERVE, DEF_IONS_TO_PRESERVE);
	}

	/**
	 * Returns the segment width enum.
	 * 
	 * @return {@link SegmentWidth}
	 */
	public static SegmentWidth getSegmentWidth() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return SegmentWidth.valueOf(preferences.get(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH));
	}
}
