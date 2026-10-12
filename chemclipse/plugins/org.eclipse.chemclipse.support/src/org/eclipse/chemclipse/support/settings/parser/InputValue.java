/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
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
	private boolean comboEdit = false;
	private boolean comboRequired = false;
	private String label = "";
	private String tooltip = "";
	private String contributorURI = "";
	private String[] proposals = new String[0];
	private boolean useSpinner = false;
	private int spinnerMinValue = Integer.MIN_VALUE;
	private int spinnerMaxValue = Integer.MAX_VALUE;
	private int spinnerStep = 1;

	public boolean hasRegexConstraint() {

		return (!"".equals(regularExpression));
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

	public boolean isComboEdit() {

		return comboEdit;
	}

	public void setComboEdit(boolean comboEdit) {

		this.comboEdit = comboEdit;
	}

	public boolean isComboRequired() {

		return comboRequired;
	}

	public void setComboRequired(boolean comboRequired) {

		this.comboRequired = comboRequired;
	}

	public String getContributorURI() {

		return contributorURI;
	}

	public void setContributorURI(String contributorURI) {

		this.contributorURI = contributorURI;
	}

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public String getTooltip() {

		return tooltip;
	}

	public void setTooltip(String tooltip) {

		this.tooltip = tooltip;
	}

	public String[] getProposals() {

		return proposals;
	}

	public void setProposals(String[] proposals) {

		this.proposals = (proposals != null) ? proposals : new String[0];
	}

	public boolean isUseSpinner() {

		return useSpinner;
	}

	public void setUseSpinner(boolean useSpinner) {

		this.useSpinner = useSpinner;
	}

	public int getSpinnerMinValue() {

		return spinnerMinValue;
	}

	public void setSpinnerMinValue(int spinnerMinValue) {

		this.spinnerMinValue = spinnerMinValue;
	}

	public int getSpinnerMaxValue() {

		return spinnerMaxValue;
	}

	public void setSpinnerMaxValue(int spinnerMaxValue) {

		this.spinnerMaxValue = spinnerMaxValue;
	}

	public int getSpinnerStep() {

		return spinnerStep;
	}

	public void setSpinnerStep(int spinnerStep) {

		this.spinnerStep = spinnerStep;
	}
}