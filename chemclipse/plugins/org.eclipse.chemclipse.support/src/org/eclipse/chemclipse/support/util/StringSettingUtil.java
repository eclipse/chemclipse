/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringSettingUtil implements IStringSerialization<String[]> {

	private ObjectMapper objectMapper;

	public StringSettingUtil() {

		objectMapper = new ObjectMapper();
	}

	@Override
	public String serialize(List<String[]> data) {

		String[][] savaData = data.stream().toArray(String[][]::new);
		try {
			return objectMapper.writeValueAsString(savaData);
		} catch(JsonProcessingException e) {
		}
		return "";
	}

	@Override
	public List<String[]> deserialize(String data) {

		List<String[]> deserialize = new ArrayList<>();
		if(data != null && !data.isEmpty()) {
			try {
				Arrays.stream(objectMapper.readValue(data, String[][].class)).forEach(value -> deserialize.add(value));
			} catch(IOException e) {
			}
		}
		return new ArrayList<>();
	}
}