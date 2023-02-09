/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactorings
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.settings.ComboSettingsProperty;
import org.eclipse.chemclipse.support.settings.DoubleSettingsProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.support.settings.LongSettingsProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.ValidatorSettingsProperty;
import org.eclipse.chemclipse.support.settings.validation.EvenOddValidatorInteger;
import org.eclipse.chemclipse.support.settings.validation.EvenOddValidatorLong;
import org.eclipse.chemclipse.support.settings.validation.MinMaxValidator;
import org.eclipse.chemclipse.support.settings.validation.RegularExpressionValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.osgi.framework.FrameworkUtil;

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
				String contributorURI = "platform:/plugin/" + FrameworkUtil.getBundle(clazz).getSymbolicName();
				TranslationService translationService = TranslationSupport.getTranslationService();
				//
				for(BeanPropertyDefinition property : properties) {
					AnnotatedField annotatedField = property.getField();
					if(annotatedField != null) {
						//
						InputValue inputValue = new InputValue();
						inputValue.setRawType(annotatedField.getRawType());
						inputValue.setName((property.getName() == null) ? "" : translationService.translate(property.getName(), contributorURI));
						PropertyMetadata propertyMetadata = property.getMetadata();
						inputValue.setDescription((propertyMetadata.getDescription() == null) ? "" : translationService.translate(propertyMetadata.getDescription(), contributorURI));
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
							if(annotation instanceof IntSettingsProperty settingsProperty) {
								inputValue.addValidator(new MinMaxValidator<>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Integer.class));
								inputValue.addValidator(new EvenOddValidatorInteger(property.getName(), settingsProperty.validation()));
							} else if(annotation instanceof LongSettingsProperty settingsProperty) {
								inputValue.addValidator(new MinMaxValidator<>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Long.class));
								inputValue.addValidator(new EvenOddValidatorLong(property.getName(), settingsProperty.validation()));
							} else if(annotation instanceof FloatSettingsProperty settingsProperty) {
								inputValue.addValidator(new MinMaxValidator<>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Float.class));
							} else if(annotation instanceof DoubleSettingsProperty settingsProperty) {
								inputValue.addValidator(new MinMaxValidator<>(property.getName(), settingsProperty.minValue(), settingsProperty.maxValue(), Double.class));
							} else if(annotation instanceof StringSettingsProperty settingsProperty) {
								String regExp = settingsProperty.regExp();
								String description = translationService.translate(settingsProperty.description(), contributorURI);
								String name = translationService.translate(property.getName(), contributorURI);
								if(regExp != null && !regExp.isEmpty()) {
									inputValue.addValidator(new RegularExpressionValidator(name, Pattern.compile(regExp), description, settingsProperty.isMultiLine(), settingsProperty.allowEmpty()));
								}
								inputValue.setMultiLine(settingsProperty.isMultiLine());
							} else if(annotation instanceof FileSettingProperty fileSettingProperty) {
								inputValue.setFileSettingProperty(fileSettingProperty);
							} else if(annotation instanceof ComboSettingsProperty comboSettingsProperty) {
								try {
									inputValue.setComboSupplier(comboSettingsProperty.value().getDeclaredConstructor().newInstance());
								} catch(Exception e) {
									throw new RuntimeException("The specified ComboSupplier can't be created.", e);
								}
							} else if(annotation instanceof ValidatorSettingsProperty validatorSettingsProperty) {
								try {
									Class<? extends IValidator<Object>> validatorClass = validatorSettingsProperty.validator();
									IValidator<Object> validator = validatorClass.getDeclaredConstructor().newInstance();
									inputValue.addValidator(validator);
								} catch(Exception e) {
									throw new RuntimeException("The validator can't be instantiated.", e);
								}
							} else {
								/*
								 * Handle by default without any further action.
								 */
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
					if(result instanceof SystemSettingsStrategy systemSettingsStrategy) {
						return systemSettingsStrategy;
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
				return settingsClass.getDeclaredConstructor().newInstance();
			} catch(Exception e) {
				throw new RuntimeException("Can't create settings instance: " + e.getMessage(), e);
			}
		}
		return null;
	}
}
