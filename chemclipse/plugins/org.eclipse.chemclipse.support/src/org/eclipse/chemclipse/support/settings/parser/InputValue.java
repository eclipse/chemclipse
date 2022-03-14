/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support FileSettingProperty, move static helper method into class, add validator support
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.support.settings.ComboSettingsProperty.ComboSupplier;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.core.databinding.validation.IValidator;

public class InputValue {

	private Class<?> rawType = null;
	private String name = "";
	private String description = "";
	private Object defaultValue;
	private final String regularExpression = null;
	private boolean isMultiLine = false;
	private FileSettingProperty fileSettingProperty;
	private final List<IValidator<Object>> validators = new ArrayList<>();
	private ComboSupplier<?> comboSupplier;

	public boolean hasRegexConstraint() {

		return (regularExpression != null && !"".equals(regularExpression));
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

	public Object getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {

		this.defaultValue = defaultValue;
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

	public void addValidator(IValidator<Object> validator) {

		validators.add(validator);
	}

	public Collection<IValidator<Object>> getValidators() {

		return Collections.unmodifiableCollection(validators);
	}

	public void setComboSupplier(ComboSupplier<?> comboSupplier) {

		this.comboSupplier = comboSupplier;
	}

	public ComboSupplier<?> getComboSupplier() {

		return comboSupplier;
	}
}
