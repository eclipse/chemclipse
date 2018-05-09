/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

import java.util.StringTokenizer;

import org.eclipse.chemclipse.support.model.RangesInteger;

public class IonSettingUtils {

	public static final String SEPATRATOR = ";";
	public static final String RANGE_SEPARATOR = "-";

	private int[][] parseString(String stringList) {

		StringTokenizer stringTokenizer = new StringTokenizer(stringList, SEPATRATOR);
		int arraySize = stringTokenizer.countTokens();
		int[][] decodedArray = new int[arraySize][2];
		try {
			for(int i = 0; i < arraySize; i++) {
				String token = stringTokenizer.nextToken();
				String[] r = token.split(RANGE_SEPARATOR);
				if(r.length == 1) {
					decodedArray[i][0] = Integer.parseInt(r[0].trim());
					decodedArray[i][1] = Integer.parseInt(r[0].trim());
				} else if(r.length == 2) {
					decodedArray[i][0] = Integer.parseInt(r[0].trim());
					decodedArray[i][1] = Integer.parseInt(r[1].trim());
				}
			}
		} catch(NumberFormatException e) {
			// TODO: handle exception
		}
		return decodedArray;
	}

	public int[] extractIntegerArray(String array) {

		RangesInteger rangesInteger = new RangesInteger();
		int[][] ranges = parseString(array);
		for(int[] r : ranges) {
			rangesInteger.addRange(r[0], r[1]);
		}
		return rangesInteger.getValues();
	}
}
