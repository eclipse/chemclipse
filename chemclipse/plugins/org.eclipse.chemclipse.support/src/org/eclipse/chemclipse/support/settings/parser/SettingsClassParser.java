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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.support.settings.ComboSettingsProperty;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.validation.EvenOddValidator;
import org.eclipse.chemclipse.support.settings.validation.MinMaxValidator;
import org.eclipse.chemclipse.support.settings.validation.RegularExpressionValidator;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**
 * parses {@link InputValue}s from class files
 *
 */
public class SettingsClassParser<SettingType> implements SettingsParser<SettingType> {

	private final Class<SettingType> settingclass;
	private List<InputValue> inputValues;
	private final Object defaultConstructorArgument;

	public SettingsClassParser(Class<SettingType> settingclass, Object defaultConstructorArgument) {
		this.settingclass = settingclass;
		this.defaultConstructorArgument = defaultConstructorArgument;
	}

	public Class<SettingType> getSettingClass() {

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
			inputValues = new ArrayList<>();
			Class<?> clazz = getSettingClass();
			Object defaultInstance = null;
			if(clazz != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
						Object defaultValue = propertyMetadata.getDefaultValue();
						if(defaultValue == null) {
							if(defaultInstance == null) {
								defaultInstance = createDefaultInstance();
							}
							if(defaultInstance != null) {
								AnnotatedMethod getter = property.getGetter();
								if(getter != null) {
									defaultValue = getter.getValue(defaultInstance);
								}
							}
						}
						inputValue.setDefaultValue((defaultValue == null) ? "" : defaultValue);
						inputValues.add(inputValue);
						/*
						 * SettingsProperty ...
						 */
						Iterable<Annotation> annotations = annotatedField.getAllAnnotations().annotations();
						for(Annotation annotation : annotations) {
							if(annotation instanceof IntSettingsProperty) {
								IntSettingsProperty settingsProperty = (IntSettingsProperty)annotation;
								inputValue.addValidator(new MinMaxValidator<Integer>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Integer.class));
								inputValue.addValidator(new EvenOddValidator(property.getName(), settingsProperty.validation()));
							} else if(annotation instanceof FloatSettingsProperty) {
								FloatSettingsProperty settingsProperty = (FloatSettingsProperty)annotation;
								inputValue.addValidator(new MinMaxValidator<Float>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Float.class));
							} else if(annotation instanceof DoubleSettingsProperty) {
								DoubleSettingsProperty settingsProperty = (DoubleSettingsProperty)annotation;
								inputValue.addValidator(new MinMaxValidator<Double>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Double.class));
							} else if(annotation instanceof StringSettingsProperty) {
								StringSettingsProperty settingsProperty = (StringSettingsProperty)annotation;
								String regExp = settingsProperty.regExp();
								if(regExp != null && !regExp.isEmpty()) {
									inputValue.addValidator(new RegularExpressionValidator(property.getName(), Pattern.compile(regExp), settingsProperty.isMultiLine()));
								}
								inputValue.setMultiLine(settingsProperty.isMultiLine());
							} else if(annotation instanceof FileSettingProperty) {
								inputValue.setFileSettingProperty((FileSettingProperty)annotation);
							} else if(annotation instanceof ComboSettingsProperty) {
								try {
									inputValue.setComboSupplier(((ComboSettingsProperty)annotation).value().newInstance());
								} catch(InstantiationException
										| IllegalAccessException e) {
									throw new RuntimeException("can't create specified ComboSupplier", e);
								}
							}
						}
					}
				}
			}
		}
		return inputValues;
	}

	@Override
	public SystemSettingsStrategy getSystemSettingsStrategy() {

		Class<?> clazz = getSettingClass();
		if(clazz == null) {
			return SystemSettingsStrategy.NULL;
		}
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
	public SettingType createDefaultInstance() {

		Class<SettingType> settingsClass = getSettingClass();
		if(settingsClass != null) {
			try {
				if(defaultConstructorArgument != null) {
					Constructor<?>[] constructors = settingsClass.getConstructors();
					for(Constructor<?> constructor : constructors) {
						if(constructor.getParameterCount() == 1) {
							Class<?> parameter = constructor.getParameterTypes()[0];
							if(parameter.isInstance(defaultConstructorArgument)) {
								return settingsClass.cast(constructor.newInstance(defaultConstructorArgument));
							}
						}
					}
				}
				// try default constructor instead
				return settingsClass.newInstance();
			} catch(InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("can't create settings instance: " + e.getMessage(), e);
			}
		}
		return null;
	}
}
