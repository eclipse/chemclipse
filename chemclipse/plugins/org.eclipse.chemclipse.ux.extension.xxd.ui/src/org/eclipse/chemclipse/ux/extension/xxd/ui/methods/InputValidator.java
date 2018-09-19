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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class InputValidator implements IValidator {

	private static final Logger logger = Logger.getLogger(InputValidator.class);
	//
	private static final String ERROR = "Please enter a value.";
	private InputValue inputValue;

	public InputValidator(InputValue inputValue) {
		this.inputValue = inputValue;
	}

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null || "".equals(value)) {
			message = ERROR;
		} else {
			Class<?> rawType = inputValue.getRawType();
			if(rawType != null) {
				message = parse(rawType, (String)value);
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
			if(rawType == int.class || rawType == Integer.class) {
				Integer.parseInt(value);
			} else if(rawType == float.class || rawType == Float.class) {
				float parsedValue = Float.parseFloat(value);
				if(inputValue.isConstraintAvailble()) {
					if(parsedValue < (float)inputValue.getMinValue() || parsedValue > (float)inputValue.getMaxValue()) {
						message = "The value must be >= " + inputValue.getMinValue() + " and <= " + inputValue.getMaxValue();
					}
				}
			} else if(rawType == double.class || rawType == Double.class) {
				double parsedValue = Double.parseDouble(value);
				if(inputValue.isConstraintAvailble()) {
					if(parsedValue < (double)inputValue.getMinValue() || parsedValue > (double)inputValue.getMaxValue()) {
						message = "The value must be >= " + inputValue.getMinValue() + " and <= " + inputValue.getMaxValue();
					}
				}
			} else if(rawType == String.class) {
				//
			} else if(rawType == boolean.class || rawType == Boolean.class) {
				Boolean.parseBoolean(value);
			} else if(rawType.isEnum()) {
				// Enum[] enums = (Enum[])rawType.getEnumConstants();
				// for(Enum enumm : enums) {
				// System.out.println(enumm);
				// }
			} else {
				logger.info("Unknown Raw Type: " + rawType);
			}
		} catch(NumberFormatException e) {
			message = ERROR;
		}
		return message;
	}
}
