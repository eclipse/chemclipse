/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.Activator;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.WncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.settings.ClassifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_WNC_IONS = "wncIons"; //$NON-NLS-1$
	public static final String DEF_WNC_IONS = Messages.wncIons;
	public static final String ENTRY_DELIMITER = ";"; //$NON-NLS-1$
	public static final String VALUE_DELIMITER = ":"; //$NON-NLS-1$
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

		Map<String, String> defaultValues = new HashMap<>();
		defaultValues.put(P_WNC_IONS, DEF_WNC_IONS);
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}

	public static ClassifierSettings getClassifierSettings() {

		ClassifierSettings classifierSettings = new ClassifierSettings();
		IWncIons wncIons = getWNCIons();
		classifierSettings.getWNCIons().add(wncIons);
		return classifierSettings;
	}

	/**
	 * Returns a list of WNC ions to preserve stored in the settings.
	 * 
	 * @return List<IWNCIon>
	 */
	public static IWncIons getWNCIons() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		/*
		 * E.g. "water:18;nitrogen:28;carbon dioxide:44"
		 */
		IWncIons ions = new WncIons();
		String preferenceEntry = preferences.get(P_WNC_IONS, DEF_WNC_IONS);
		if(preferenceEntry.isEmpty()) {
			String[] items = parseString(preferenceEntry);
			if(items.length > 0) {
				String name;
				Integer ion;
				String[] values;
				for(String item : items) {
					try {
						values = item.split(VALUE_DELIMITER);
						if(values.length > 1) {
							name = values[0];
							ion = Integer.parseInt(values[1]);
							ions.add(new WncIon(ion, name));
						}
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
	 * E.g. "water:18;nitrogen:28;carbon dioxide:44"
	 * 
	 * @param stringList
	 * @return String[]
	 */
	public static String[] parseString(String stringList) {

		String[] decodedArray;
		if(stringList.contains(ENTRY_DELIMITER)) {
			StringTokenizer stringTokenizer = new StringTokenizer(stringList, ENTRY_DELIMITER);
			int arraySize = stringTokenizer.countTokens();
			decodedArray = new String[arraySize];
			for(int i = 0; i < arraySize; i++) {
				decodedArray[i] = stringTokenizer.nextToken(ENTRY_DELIMITER);
			}
		} else {
			decodedArray = new String[1];
			decodedArray[0] = stringList;
		}
		return decodedArray;
	}

	/**
	 * Stores the ions.
	 * 
	 * @param wncIons
	 */
	public static void storeWNCIons(IWncIons wncIons) {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		String values = ""; //$NON-NLS-1$
		if(wncIons != null) {
			StringBuilder builder = new StringBuilder();
			List<Integer> keys = new ArrayList<>(wncIons.getKeys());
			int size = keys.size();
			if(size >= 1) {
				for(int index = 0; index < size; index++) {
					int ion = keys.get(index);
					IWncIon wncIon = wncIons.getWNCIon(ion);
					if(wncIon != null) {
						builder.append(wncIon.getName());
						builder.append(VALUE_DELIMITER);
						builder.append(wncIon.getIon());
						builder.append(ENTRY_DELIMITER);
					}
				}
			}
			values = builder.toString();
		}
		preferences.put(P_WNC_IONS, values);
	}
}
