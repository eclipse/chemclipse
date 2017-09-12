/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.chemclipse.logging.core.Logger;

public class IonListUtil {

	private static final Logger logger = Logger.getLogger(IonListUtil.class);

	/**
	 * Creates the settings strings.<br/>
	 * E.g. 18 28 84 207 to "18;28;84;207"
	 * 
	 * @param items
	 * @return String
	 */
	public String createList(String[] items) {

		Set<String> ionSet = getIonSet(items);
		String ions = "";
		for(String ion : ionSet) {
			ions = ions.concat(ion + ";");
		}
		return ions;
	}

	/**
	 * Returns a string array.<br/>
	 * E.g. "18;28;84;207" to 18 28 84 207
	 * The string array is empty,
	 * If there's no value stored.
	 * 
	 * @param stringList
	 * @return String[]
	 */
	public String[] parseString(String stringList) {

		String[] decodedArray;
		if(stringList.contains(";")) {
			/*
			 * There are values stored.
			 */
			StringTokenizer stringTokenizer = new StringTokenizer(stringList, ";");
			int arraySize = stringTokenizer.countTokens();
			decodedArray = new String[arraySize];
			for(int i = 0; i < arraySize; i++) {
				decodedArray[i] = stringTokenizer.nextToken(";");
			}
		} else {
			/*
			 * No value is stored.
			 */
			decodedArray = new String[0];
		}
		return decodedArray;
	}

	/**
	 * E.g. preferenceEntry = "18;28;84;207" to 18 28 84 207
	 */
	public Set<Integer> getIons(String stringList) {

		Set<Integer> ions = new HashSet<Integer>();
		if(stringList != "") {
			String[] items = parseString(stringList);
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

	private Set<String> getIonSet(String[] items) {

		/*
		 * Set to remove equal entries.
		 */
		Set<String> ionSet = new HashSet<String>();
		if(items != null) {
			int size = items.length;
			for(int i = 0; i < size; i++) {
				ionSet.add(items[i]);
			}
		}
		return ionSet;
	}
}
