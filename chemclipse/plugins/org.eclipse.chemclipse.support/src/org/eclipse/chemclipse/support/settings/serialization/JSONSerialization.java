/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - added support for dynamic mapper
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.serialization;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.chemclipse.support.Activator;
import org.eclipse.chemclipse.support.settings.ISettingsMigrator;
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

			Class<?> sourceClass = type.getRawClass();
			Class<?> destinationClass = MAPPINGS.get(sourceClass);
			//
			if(destinationClass == null) {
				return null;
			}
			return config.getTypeFactory().constructSpecializedType(type, destinationClass);
		}
	};

	@Override
	public Map<InputValue, Object> fromObject(Collection<? extends InputValue> inputValues, Object object) throws IOException {

		/*
		 * Set the defaults.
		 */
		LinkedHashMap<InputValue, Object> resultMap = new LinkedHashMap<>();
		for(InputValue inputValue : inputValues) {
			resultMap.put(inputValue, inputValue.getDefaultValue());
		}
		/*
		 * Fetch the used settings.
		 */
		if(object != null) {
			Map<String, InputValue> inputValueMap = getInputValueMap(inputValues);
			Map<?, ?> settingsMap = createMapper().convertValue(object, HashMap.class);
			for(Entry<?, ?> entry : settingsMap.entrySet()) {
				InputValue inputValue = inputValueMap.get(entry.getKey());
				if(inputValue != null) {
					Object value = entry.getValue();
					if(value != null) {
						resultMap.put(inputValue, value);
					}
				}
			}
		}
		//
		return resultMap;
	}

	private Map<String, InputValue> getInputValueMap(Collection<? extends InputValue> inputValues) {

		Map<String, InputValue> inputValueMap = new HashMap<>();
		for(InputValue inputValue : inputValues) {
			inputValueMap.put(inputValue.getName(), inputValue);
		}
		//
		return inputValueMap;
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	private ObjectMapper createMapper() {

		ObjectMapper objectMapper = new ObjectMapper();
		//
		SimpleModule simpleModule = new SimpleModule("ChemClipse", Version.unknownVersion());
		simpleModule.setAbstractTypes(RESOLVER);
		/*
		 * Add additional mapper dynamically.
		 */
		Object[] serializationServices = Activator.getDefault().getSerializationServices();
		if(serializationServices != null) {
			for(Object object : serializationServices) {
				if(object instanceof ISerializationService serializationService) {
					Class clazz = serializationService.getSupportedClass();
					simpleModule.addSerializer(clazz, serializationService.getSerializer());
					simpleModule.addDeserializer(clazz, serializationService.getDeserializer());
				}
			}
		}
		//
		objectMapper.registerModule(simpleModule);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		//
		return objectMapper;
	}

	@Override
	public void updateFromString(Object object, String content) throws IOException {

		if(object != null && content != null && !content.isEmpty()) {
			createMapper().readerForUpdating(object).readValue(content);
			if(object instanceof ISettingsMigrator settingsMigrator) {
				settingsMigrator.transferToLatestVersion(content);
			}
		}
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