/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileSettingUtil implements IStringSerialization<File> {

	private ObjectMapper objectMapper;

	public FileSettingUtil() {

		objectMapper = new ObjectMapper();
	}

	@Override
	public String serialize(List<File> data) {

		String[] savaData = data.stream().map(File::getAbsolutePath).toArray(String[]::new);
		try {
			return objectMapper.writeValueAsString(savaData);
		} catch(JsonProcessingException e) {
		}
		return "";
	}

	@Override
	public List<File> deserialize(String data) {

		if(data != null && !data.isEmpty()) {
			try {
				return Arrays.stream(objectMapper.readValue(data, String[].class)).map(File::new).toList();
			} catch(IOException e) {
			}
		}
		return new ArrayList<>();
	}
}
