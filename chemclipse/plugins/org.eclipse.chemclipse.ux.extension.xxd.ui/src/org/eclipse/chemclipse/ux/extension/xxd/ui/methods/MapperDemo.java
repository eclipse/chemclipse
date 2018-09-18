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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.impl.settings.SupplierFilterSettings;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
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

public class MapperDemo {

	public void test1() {

		try {
			/*
			 * "{\"Start RT (Minutes)\":10.16,\"Stop RT (Minutes)\":13.45}"
			 */
			// ObjectMapper mapper = new ObjectMapper();
			// Map<String, Object> values = new HashMap<>();
			// values.put("Start RT (Minutes)", 10.16);
			// values.put("Stop RT (Minutes)", 13.45);
			// String content = mapper.writeValueAsString(values);
			String content = null;
			Shell shell = DisplayUtils.getShell();
			SupplierFilterSettings settings = getSettings(content, SupplierFilterSettings.class, shell);
		} catch(JsonParseException e) {
			System.out.println(e);
		} catch(JsonMappingException e) {
			System.out.println(e);
		} catch(IOException e) {
			System.out.println(e);
		}
	}

	private <T extends IProcessSettings> T getSettings(String content, Class<T> clazz, Shell shell) throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		//
		if(content == null) {
			List<InputValue> inputValues = getInputValues(objectMapper, clazz);
			content = getContentViaWizard(shell, inputValues);
		}
		//
		T settings = objectMapper.readValue(content, clazz);
		return settings;
	}

	private String getContentViaWizard(Shell shell, List<InputValue> inputValues) {

		String content = "{}";
		//
		SettingsWizard wizard = new SettingsWizard(inputValues);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(SettingsWizard.DEFAULT_WIDTH, SettingsWizard.DEFAULT_HEIGHT);
		wizardDialog.create();
		//
		if(wizardDialog.open() == WizardDialog.OK) {
			content = wizard.getDialogSettings().get(SettingsWizard.JSON_SETTINGS);
		}
		//
		return content;
	}

	@SuppressWarnings("rawtypes")
	private List<InputValue> getInputValues(ObjectMapper objectMapper, Class clazz) {

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
					// System.out.println(annotation.annotationType().getName());
					// DoubleSettingsProperty doubleSettingsProperty = (DoubleSettingsProperty)annotation;
					// System.out.println(doubleSettingsProperty.minValue());
					// System.out.println(doubleSettingsProperty.maxValue());
				}
			}
		}
		//
		return inputValues;
	}
}
