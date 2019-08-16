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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.support.model.RangesInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IonSettingUtil implements IStringSerialization<String> {

	public static final String SEPARATOR = ";";
	public static final String RANGE_SEPARATOR = "-";
	private ObjectMapper objectMapper;

	public IonSettingUtil() {
		objectMapper = new ObjectMapper();
	}

	private int[][] parseString(List<String> stringList) {

		int arraySize = stringList.size();
		int[][] decodedArray = new int[arraySize][2];
		try {
			for(int i = 0; i < arraySize; i++) {
				String[] r = stringList.get(i).split(RANGE_SEPARATOR);
				if(r.length == 1) {
					decodedArray[i][0] = Integer.parseInt(r[0].trim());
					decodedArray[i][1] = Integer.parseInt(r[0].trim());
				} else if(r.length == 2) {
					int r1 = Integer.parseInt(r[0].trim());
					int r2 = Integer.parseInt(r[1].trim());
					decodedArray[i][0] = Math.min(r1, r2);
					decodedArray[i][1] = Math.max(r1, r2);
				}
			}
		} catch(NumberFormatException e) {
			// TODO: handle exception
		}
		return decodedArray;
	}

	@Override
	public String serialize(List<String> serialize) {

		try {
			String[] array = new String[serialize.size()];
			return objectMapper.writeValueAsString(serialize.toArray(array));
		} catch(JsonProcessingException e) {
		}
		return "";
	}

	@Override
	public List<String> deserialize(String deserialize) {

		try {
			if(deserialize != null && !deserialize.isEmpty()) {
				String[] decodedArray = objectMapper.readValue(deserialize, String[].class);
				return Arrays.stream(decodedArray).collect(Collectors.toList());
			}
		} catch(IOException e) {
		}
		/*
		 * support original save...
		 */
		String[] decodedArray;
		String stringList = deserialize;
		StringTokenizer stringTokenizer = new StringTokenizer(stringList, ";");
		int arraySize = stringTokenizer.countTokens();
		if(arraySize > 0) {
			/*
			 * There are values stored.
			 */
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
		return Arrays.stream(decodedArray).collect(Collectors.toList());
	}

	public int compare(String s1, String s2) {

		String splitS1 = s1.split("-")[0].trim();
		int i1 = Integer.parseInt(splitS1);
		String splitS2 = s2.split("-")[0].trim();
		int i2 = Integer.parseInt(splitS2);
		return Integer.compare(i1, i2);
	}

	public int[] extractIons(List<String> array) {

		RangesInteger rangesInteger = new RangesInteger();
		int[][] ranges = parseString(array);
		for(int[] r : ranges) {
			rangesInteger.addRange(r[0], r[1]);
		}
		return rangesInteger.getValues();
	}

	public List<String> parseInput(String ions) {

		List<String> newRanges = new ArrayList<>();
		if(ions != null) {
			int[][] ranges = parseString(ions);
			for(int i = 0; i < ranges.length; i++) {
				if(ranges[i][0] == ranges[i][1]) {
					newRanges.add(Integer.toString(ranges[i][0]));
				} else {
					newRanges.add(Integer.toString(ranges[i][0]) + " - " + Integer.toString(ranges[i][1]));
				}
			}
		}
		return newRanges;
	}

	private int[][] parseString(String stringList) {

		StringTokenizer stringTokenizer = new StringTokenizer(stringList, SEPARATOR);
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
					int r1 = Integer.parseInt(r[0].trim());
					int r2 = Integer.parseInt(r[1].trim());
					decodedArray[i][0] = Math.min(r1, r2);
					decodedArray[i][1] = Math.max(r1, r2);
				}
			}
		} catch(NumberFormatException e) {
			// TODO: handle exception
		}
		return decodedArray;
	}
}
