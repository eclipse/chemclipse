/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public class SettingsSupport {

	public static final String EMPTY_JSON_SETTINGS = "{}";

	public <T extends IProcessSettings> T getSettings(String content, Class<T> clazz, Shell shell) throws JsonParseException, JsonMappingException, IOException {

		String contentModified = getSettingsAsJson(content, clazz, shell);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		T settings = objectMapper.readValue(contentModified, clazz);
		return settings;
	}

	public String getSettingsAsJson(String content, Class<? extends IProcessSettings> clazz, Shell shell) throws JsonParseException, JsonMappingException, IOException {

		/*
		 * Initialize the input values with existing values.
		 */
		List<InputValue> inputValues = getInputValues(clazz);
		initializeInputValues(content, inputValues);
		try {
			return getContentViaWizard(shell, inputValues);
		} catch(Exception e) {
			/*
			 * Cancel pressed
			 */
			return (content == null) ? EMPTY_JSON_SETTINGS : content;
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeInputValues(String content, List<InputValue> inputValues) throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		/*
		 * Set existing values.
		 */
		if(content != null) {
			Map<String, Object> map = objectMapper.readValue(content, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				for(InputValue inputValue : inputValues) {
					if(inputValue.getName().equals(entry.getKey())) {
						inputValue.setValue(entry.getValue().toString());
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
	}

	private String getContentViaWizard(Shell shell, List<InputValue> inputValues) throws Exception {

		SettingsWizard wizard = new SettingsWizard(inputValues);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(SettingsWizard.DEFAULT_WIDTH, SettingsWizard.DEFAULT_HEIGHT);
		wizardDialog.create();
		//
		if(wizardDialog.open() == WizardDialog.OK) {
			return wizard.getDialogSettings().get(SettingsWizard.JSON_SETTINGS);
		} else {
			throw new Exception("Cancel has been pressed.");
		}
	}

	@SuppressWarnings("rawtypes")
	private List<InputValue> getInputValues(Class clazz) {

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
				inputValue.setName(property.getName());
				inputValue.setDescription(property.getMetadata().getDescription());
				inputValue.setDefaultValue(property.getMetadata().getDefaultValue());
				inputValues.add(inputValue);
				/*
				 * SettingsProperty ...
				 */
				for(Annotation annotation : annotatedField.annotations()) {
					if(annotation.annotationType().getName().contains("SettingsProperty")) {
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
						}
						// IonsSelectionSettingProperty
						// RetentionTimeMinutesProperty - remove?
					}
				}
			}
		}
		//
		return inputValues;
	}
}
