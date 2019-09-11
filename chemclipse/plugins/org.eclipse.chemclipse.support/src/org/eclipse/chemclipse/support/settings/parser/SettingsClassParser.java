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
package org.eclipse.chemclipse.support.settings.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntegerValidation;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**
 * parses {@link InputValue}s from class files
 *
 */
public class SettingsClassParser implements SettingsParser {

	private Class<?> settingclass;
	private List<InputValue> inputValues;

	public SettingsClassParser(Class<?> settingclass) {
		this.settingclass = settingclass;
	}

	public Class<?> getSettingclass() {

		return settingclass;
	}

	/**
	 * parses a given class for annotations and generate {@link InputValue}s from it
	 * 
	 * @param clazz
	 * @return
	 */
	@Override
	public List<InputValue> getInputValues() {

		if(inputValues == null) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			inputValues = new ArrayList<>();
			JavaType javaType = objectMapper.getSerializationConfig().constructType(getSettingclass());
			BeanDescription beanDescription = objectMapper.getSerializationConfig().introspect(javaType);
			List<BeanPropertyDefinition> properties = beanDescription.findProperties();
			//
			for(BeanPropertyDefinition property : properties) {
				AnnotatedField annotatedField = property.getField();
				if(annotatedField != null) {
					//
					InputValue inputValue = new InputValue();
					inputValue.setRawType(annotatedField.getRawType());
					inputValue.setName((property.getName() == null) ? "" : property.getName());
					PropertyMetadata propertyMetadata = property.getMetadata();
					inputValue.setDescription((propertyMetadata.getDescription() == null) ? "" : propertyMetadata.getDescription());
					inputValue.setDefaultValue((propertyMetadata.getDefaultValue() == null) ? "" : propertyMetadata.getDefaultValue());
					inputValues.add(inputValue);
					/*
					 * SettingsProperty ...
					 */
					@SuppressWarnings("deprecation")
					Iterable<Annotation> annotations = annotatedField.annotations();
					for(Annotation annotation : annotations) {
						if(annotation instanceof IntSettingsProperty) {
							IntSettingsProperty settingsProperty = (IntSettingsProperty)annotation;
							inputValue.setMinValue(settingsProperty.minValue());
							inputValue.setMaxValue(settingsProperty.maxValue());
							switch(settingsProperty.validation()) {
								case ODD_NUMBER:
									inputValue.setIntegerValidation(IntegerValidation.ODD);
									break;
								default:
									inputValue.setIntegerValidation(null);
									break;
							}
						} else if(annotation instanceof FloatSettingsProperty) {
							FloatSettingsProperty settingsProperty = (FloatSettingsProperty)annotation;
							inputValue.setMinValue(settingsProperty.minValue());
							inputValue.setMaxValue(settingsProperty.maxValue());
						} else if(annotation instanceof DoubleSettingsProperty) {
							DoubleSettingsProperty settingsProperty = (DoubleSettingsProperty)annotation;
							inputValue.setMinValue(settingsProperty.minValue());
							inputValue.setMaxValue(settingsProperty.maxValue());
						} else if(annotation instanceof StringSettingsProperty) {
							StringSettingsProperty settingsProperty = (StringSettingsProperty)annotation;
							inputValue.setRegularExpression(settingsProperty.regExp());
							inputValue.setMultiLine(settingsProperty.isMultiLine());
						} else if(annotation instanceof FileSettingProperty) {
							inputValue.setFileSettingProperty((FileSettingProperty)annotation);
						}
					}
				}
			}
		}
		return inputValues;
	}

	@Override
	public SystemSettingsStrategy getSystemSettingsStrategy() {

		Class<?> clazz = getSettingclass();
		SystemSettings annotation = clazz.getAnnotation(SystemSettings.class);
		if(annotation != null) {
			String checkMethod = annotation.dynamicCheckMethod();
			if(annotation.value() == SystemSettingsStrategy.DYNAMIC && !checkMethod.isEmpty()) {
				try {
					Method method = clazz.getMethod(checkMethod);
					Object result = method.invoke(null);
					if(result instanceof SystemSettingsStrategy) {
						return (SystemSettingsStrategy)result;
					}
				} catch(NoSuchMethodException | SecurityException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException("specified dynamic check method can't be used", e);
				}
			}
			return annotation.value();
		}
		return SystemSettingsStrategy.NULL;
	}

	@Override
	public boolean requiresUserSettings() {

		SystemSettingsStrategy strategy = getSystemSettingsStrategy();
		return strategy == SystemSettingsStrategy.NONE || strategy == SystemSettingsStrategy.DYNAMIC;
	}
}
