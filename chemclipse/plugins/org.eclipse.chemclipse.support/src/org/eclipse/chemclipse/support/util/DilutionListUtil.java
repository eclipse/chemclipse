/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DilutionListUtil {

	public static final String SEPARATOR_TOKEN = ";";

	public String createList(String[] items) {

		List<String> dilutionList = getDilutions(items);
		String dilutions = "";
		for(String dilution : dilutionList) {
			dilutions = dilutions.concat(dilution + SEPARATOR_TOKEN);
		}
		return dilutions;
	}

	public String[] parseString(String stringList) {

		String[] decodedArray;
		if(stringList.contains(SEPARATOR_TOKEN)) {
			decodedArray = stringList.split(SEPARATOR_TOKEN);
		} else {
			decodedArray = new String[1];
			decodedArray[0] = stringList;
		}
		return decodedArray;
	}

	public List<String> getDilutions(String preferenceEntry) {

		List<String> dilutions = new ArrayList<>();
		if(!"".equals(preferenceEntry)) {
			String[] items = parseString(preferenceEntry);
			dilutions = Arrays.asList(items);
		}
		Collections.sort(dilutions);
		return dilutions;
	}

	private List<String> getDilutions(String[] items) {

		List<String> dilutions = new ArrayList<>();
		if(items != null) {
			dilutions = Arrays.asList(items);
		}
		return dilutions;
	}
}
