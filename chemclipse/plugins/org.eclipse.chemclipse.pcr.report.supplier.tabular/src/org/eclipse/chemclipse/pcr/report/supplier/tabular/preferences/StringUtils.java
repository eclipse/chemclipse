/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.preferences;

import java.util.StringTokenizer;

public class StringUtils {

	public final static String SEPARATOR_TOKEN = ";";

	public static String[] parseString(String stringList) {

		String[] decodedArray;
		if(stringList.contains(SEPARATOR_TOKEN)) {
			StringTokenizer stringTokenizer = new StringTokenizer(stringList, SEPARATOR_TOKEN);
			int arraySize = stringTokenizer.countTokens();
			decodedArray = new String[arraySize];
			for(int i = 0; i < arraySize; i++) {
				decodedArray[i] = stringTokenizer.nextToken(SEPARATOR_TOKEN);
			}
		} else {
			decodedArray = new String[1];
			decodedArray[0] = stringList;
		}
		return decodedArray;
	}

	public static String createList(String[] items) {

		String entry = "";
		if(items != null) {
			int size = items.length;
			for(int i = 0; i < size; i++) {
				entry = entry.concat(items[i] + SEPARATOR_TOKEN);
			}
		}
		return entry;
	}
}
