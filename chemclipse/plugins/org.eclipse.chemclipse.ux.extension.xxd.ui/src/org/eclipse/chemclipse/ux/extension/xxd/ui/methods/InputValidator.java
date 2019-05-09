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
		if(value == null) {
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
				int parsedValue = Integer.parseInt(value);
				if(inputValue.hasIntegerValidation()) {
					switch(inputValue.getIntegerValidation()) {
						case ODD:
							if(parsedValue % 2 == 0) {
								message = "The value must be odd.";
							}
							break;
						case EVEN:
							if(parsedValue % 2 == 1) {
								message = "The value must be even.";
							}
							break;
						default:
							break;
					}
				}
			} else if(rawType == float.class || rawType == Float.class) {
				float parsedValue = Float.parseFloat(value);
				if(inputValue.hasMinMaxConstraint()) {
					if(parsedValue < (float)inputValue.getMinValue() || parsedValue > (float)inputValue.getMaxValue()) {
						message = "The value must be >= " + inputValue.getMinValue() + " and <= " + inputValue.getMaxValue();
					}
				}
			} else if(rawType == double.class || rawType == Double.class) {
				double parsedValue = Double.parseDouble(value);
				if(inputValue.hasMinMaxConstraint()) {
					if(parsedValue < (double)inputValue.getMinValue() || parsedValue > (double)inputValue.getMaxValue()) {
						message = "The value must be >= " + inputValue.getMinValue() + " and <= " + inputValue.getMaxValue();
					}
				}
			} else if(rawType == String.class) {
				if(inputValue.hasRegexConstraint()) {
					if(inputValue.isMultiLine()) {
						String[] lines = value.split("[\r\n]+");
						int n = 1;
						exitloop:
						for(String line : lines) {
							if(!line.matches(inputValue.getRegularExpression())) {
								message = "Line #" + n + " is not formatted correctly.";
								break exitloop;
							}
							n++;
						}
					} else {
						if(!value.matches(inputValue.getRegularExpression())) {
							message = "The value must match the expression: " + inputValue.getRegularExpression();
						}
					}
				}
			} else if(rawType == boolean.class || rawType == Boolean.class) {
				Boolean.parseBoolean(value);
			} else if(rawType.isEnum()) {
				if("".equals(value)) {
					message = "Please select and option from the combo box.";
				}
			} else {
				logger.info("Unknown Raw Type: " + rawType);
			}
		} catch(NumberFormatException e) {
			message = ERROR;
		}
		return message;
	}
}
