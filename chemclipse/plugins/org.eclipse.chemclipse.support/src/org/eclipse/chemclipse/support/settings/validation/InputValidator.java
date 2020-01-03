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
 * Christoph Läubrich - support File properties
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.validation;

import java.io.File;

import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class InputValidator implements IValidator {

	private static final String ERROR = "Please enter a value.";
	private final InputValue inputValue;

	public InputValidator(InputValue inputValue) {
		this.inputValue = inputValue;
	}

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR;
		} else {
			Class<?> rawType = inputValue.getRawType();
			if(rawType != null) {
				message = parse(rawType, value.toString());
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	private String parse(Class<?> rawType, String value) {

		String message = null;
		try {
			if(rawType == boolean.class || rawType == Boolean.class) {
				Boolean.parseBoolean(value);
			} else if(rawType.isEnum()) {
				if("".equals(value)) {
					message = "Please select and option from the combo box.";
				}
			} else if(rawType == File.class) {
				FileSettingProperty property = inputValue.getFileSettingProperty();
				if(property != null && !(property.dialogType() == DialogType.SAVE_DIALOG)) {
					if(value != null && !value.isEmpty() && !new File(value).exists()) {
						return "Location does not exits, please choose a valid location";
					}
				}
			}
		} catch(NumberFormatException e) {
			message = ERROR;
		}
		return message;
	}
}
