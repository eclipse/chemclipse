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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.preferences;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.Activator;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.settings.FilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.support.model.SegmentWidth;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

	public static final String P_IONS_TO_REMOVE = "ionsToRemove";
	public static final String P_IONS_TO_PRESERVE = "ionsToPreserve";
	public static final String P_SEGMENT_WIDTH = "segmentWidth";
	public static final String P_ADJUST_THRESHOLD_TRANSITIONS = "adjustThresholdTransitions";
	public static final String P_NUMBER_OF_USE_IONS_FOR_COEFFICIENT = "numberOfUsedIonsForCoefficient";
	public static final String DEF_IONS_TO_REMOVE = "18;28;84;207";
	public static final String DEF_IONS_TO_PRESERVE = "103;104";
	public static final int DEF_NUMBER_OF_USE_IONS_FOR_COEFFICIENT = 1;
	public static final int DEF_SEGMENT_WIDTH = 13;
	public static final int SEGMENT_WIDTH_MIN = 5;
	public static final int SEGMENT_WIDTH_MAX = 19;
	public static final int NUMBER_OF_USE_IONS_FOR_COEFFICIENT_MIN = 1;
	public static final int NUMBER_OF_USE_IONS_FOR_COEFFICIENT_MAX = 20;
	public static final boolean DEF_USE_CHROMATOGRAM_SPECIFIC_IONS = false;
	public static final boolean DEF_ADJUST_THRESHOLD_TRANSITIONS = true;
	//
	private static final Logger logger = Logger.getLogger(PreferenceSupplier.class);
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
		putDefault(P_IONS_TO_PRESERVE, DEF_IONS_TO_PRESERVE);
		putDefault(P_SEGMENT_WIDTH, Integer.toString(DEF_SEGMENT_WIDTH));
		putDefault(P_ADJUST_THRESHOLD_TRANSITIONS, Boolean.toString(DEF_ADJUST_THRESHOLD_TRANSITIONS));
		putDefault(P_NUMBER_OF_USE_IONS_FOR_COEFFICIENT, Integer.toString(DEF_NUMBER_OF_USE_IONS_FOR_COEFFICIENT));
	}

	public static FilterSettings getFilterSettings() {

		FilterSettings filterSettings = new FilterSettings();
		filterSettings.setAdjustThresholdTransitions(INSTANCE().getBoolean(P_ADJUST_THRESHOLD_TRANSITIONS, DEF_ADJUST_THRESHOLD_TRANSITIONS));
		filterSettings.setNumberOfUsedIonsForCoefficient(INSTANCE().getInteger(P_NUMBER_OF_USE_IONS_FOR_COEFFICIENT, DEF_NUMBER_OF_USE_IONS_FOR_COEFFICIENT));
		filterSettings.setIonsToRemove(INSTANCE().get(P_IONS_TO_REMOVE, DEF_IONS_TO_REMOVE));
		filterSettings.setIonsToPreserve(INSTANCE().get(P_IONS_TO_PRESERVE, DEF_IONS_TO_PRESERVE));
		filterSettings.setSegmentWidth(INSTANCE().getInteger(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH));
		return filterSettings;
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

		/*
		 * E.g. "18;28;84;207" to 18 28 84 207
		 */
		Set<Integer> ions = new HashSet<Integer>();
		String preferenceEntry = INSTANCE().get(preference, def);
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

		return INSTANCE().getBoolean(P_ADJUST_THRESHOLD_TRANSITIONS, DEF_ADJUST_THRESHOLD_TRANSITIONS);
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
	public static int getSegmentWidth() {

		return INSTANCE().getInteger(P_SEGMENT_WIDTH, DEF_SEGMENT_WIDTH);
	}
}