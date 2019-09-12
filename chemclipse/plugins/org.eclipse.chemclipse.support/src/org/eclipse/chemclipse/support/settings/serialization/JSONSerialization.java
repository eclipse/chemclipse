/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.serialization;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.support.settings.parser.InputValue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONSerialization implements SettingsSerialization {

	@Override
	public Map<InputValue, Object> fromString(Collection<? extends InputValue> inputValues, String content) throws IOException {

		LinkedHashMap<InputValue, Object> result = new LinkedHashMap<>();
		for(InputValue inputValue : inputValues) {
			result.put(inputValue, inputValue.getDefaultValue());
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		if(content != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(content, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				for(InputValue inputValue : inputValues) {
					if(inputValue.getName().equals(entry.getKey())) {
						Object value = entry.getValue();
						if(value != null) {
							result.put(inputValue, value);
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public String toString(Map<InputValue, Object> values) throws IOException {

		Map<String, Object> result = new HashMap<>();
		for(Entry<InputValue, Object> entry : values.entrySet()) {
			result.put(entry.getKey().getName(), entry.getValue());
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(result);
	}

	@Override
	public <Settings> Settings fromString(Class<Settings> settingsClass, String content) throws IOException {

		if(content == null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return objectMapper.readValue(content, settingsClass);
	}

	@Override
	public String toString(Object settingsObject) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(settingsObject);
	}
}
