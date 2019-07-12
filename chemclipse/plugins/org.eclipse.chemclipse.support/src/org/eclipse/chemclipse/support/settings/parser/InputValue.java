/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support FileSettingProperty, move static helper method into class
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.parser;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntegerValidation;
import org.eclipse.chemclipse.support.settings.IonsSelectionSettingProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public class InputValue {

	private Class<?> rawType = null;
	private String name = "";
	private String description = "";
	private String value = null; // Needed for initialization.
	private String defaultValue = "";
	/*
	 * SettingsProperty ...
	 */
	private Object minValue = null;
	private Object maxValue = null;
	private String regularExpression = null;
	private IntegerValidation integerValidation = null;
	private boolean isMultiLine = false; // StringSettingsProperty
	private FileSettingProperty fileSettingProperty;

	public boolean hasMinMaxConstraint() {

		return (minValue != null) && (maxValue != null);
	}

	public boolean hasRegexConstraint() {

		return (regularExpression != null && !"".equals(regularExpression));
	}

	public boolean hasIntegerValidation() {

		return (integerValidation != null);
	}

	public Class<?> getRawType() {

		return rawType;
	}

	public void setRawType(Class<?> rawType) {

		this.rawType = rawType;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {

		if(description == null) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {

		this.defaultValue = defaultValue;
	}

	public Object getMinValue() {

		return minValue;
	}

	public void setMinValue(Object minValue) {

		this.minValue = minValue;
	}

	public Object getMaxValue() {

		return maxValue;
	}

	public void setMaxValue(Object maxValue) {

		this.maxValue = maxValue;
	}

	public String getRegularExpression() {

		return regularExpression;
	}

	public void setRegularExpression(String regularExpression) {

		this.regularExpression = regularExpression;
	}

	public IntegerValidation getIntegerValidation() {

		return integerValidation;
	}

	public void setIntegerValidation(IntegerValidation integerValidation) {

		this.integerValidation = integerValidation;
	}

	public boolean isMultiLine() {

		return isMultiLine;
	}

	public void setMultiLine(boolean isMultiLine) {

		this.isMultiLine = isMultiLine;
	}

	public void setFileSettingProperty(FileSettingProperty annotation) {

		this.fileSettingProperty = annotation;
	}

	public FileSettingProperty getFileSettingProperty() {

		return fileSettingProperty;
	}

	/**
	 * parses a given class for annotations and generate {@link InputValue}s from it
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<InputValue> getInputValues(Class<?> clazz) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		//
		List<InputValue> inputValues = new ArrayList<>();
		JavaType javaType = objectMapper.getSerializationConfig().constructType(clazz);
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
				for(Annotation annotation : annotatedField.annotations()) {
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
					} else if(annotation instanceof IonsSelectionSettingProperty) {
						// IonsSelectionSettingProperty settingsProperty = (IonsSelectionSettingProperty)annotation;
						/*
						 * Find a better regex to match empty or 28;32 ...
						 */
						inputValue.setRegularExpression("(^$|\\d+;?)");
					} else if(annotation instanceof FileSettingProperty) {
						inputValue.setFileSettingProperty((FileSettingProperty)annotation);
					}
					// RetentionTimeMinutesProperty - remove?
				}
			}
		}
		//
		return inputValues;
	}

	public static List<InputValue> readJSON(Class<?> clazz, String content) throws IOException {

		if(clazz == null) {
			return Collections.emptyList();
		}
		List<InputValue> inputValues = getInputValues(clazz);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		/*
		 * Set existing values.
		 */
		if(content != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(content, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				for(InputValue inputValue : inputValues) {
					if(inputValue.getName().equals(entry.getKey())) {
						Object value = entry.getValue();
						if(value != null) {
							inputValue.setValue(value.toString());
						}
					}
				}
			}
		}
		/*
		 * Set default values on demand.
		 */
		for(InputValue inputValue : inputValues) {
			if(inputValue.getValue() == null) {
				inputValue.setValue(inputValue.getDefaultValue());
			}
		}
		return inputValues;
	}
}
