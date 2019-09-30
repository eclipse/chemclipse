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
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.chemclipse.support.settings.parser.InputValue;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JSONSerialization implements SettingsSerialization {

	private static final Map<Class<?>, Class<?>> MAPPINGS = new ConcurrentHashMap<>();
	private static final SimpleAbstractTypeResolver RESOLVER = new SimpleAbstractTypeResolver() {

		private static final long serialVersionUID = 1L;

		@Override
		public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {

			Class<?> src = type.getRawClass();
			Class<?> dst = MAPPINGS.get(src);
			if(dst == null) {
				return null;
			}
			return config.getTypeFactory().constructSpecializedType(type, dst);
		}
	};

	@Override
	public Map<InputValue, Object> fromString(Collection<? extends InputValue> inputValues, String content) throws IOException {

		LinkedHashMap<InputValue, Object> result = new LinkedHashMap<>();
		for(InputValue inputValue : inputValues) {
			result.put(inputValue, inputValue.getDefaultValue());
		}
		if(content != null && !content.isEmpty()) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = createMapper().readValue(content, HashMap.class);
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
		ObjectMapper mapper = createMapper();
		return mapper.writeValueAsString(result);
	}

	private ObjectMapper createMapper() {

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("ChemClipse", Version.unknownVersion());
		module.setAbstractTypes(RESOLVER);
		mapper.registerModule(module);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}

	@Override
	public <Settings> Settings fromString(Class<Settings> settingsClass, String content) throws IOException {

		if(content == null || content.isEmpty()) {
			return null;
		}
		return createMapper().readValue(content, settingsClass);
	}

	@Override
	public String toString(Object settingsObject) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(settingsObject);
	}

	public static <T> void addMapping(Class<T> superType, Class<? extends T> subType) {

		MAPPINGS.put(superType, subType);
	}

	public static <T> void removeMapping(Class<T> superType, Class<? extends T> subType) {

		MAPPINGS.remove(superType, subType);
	}
}
