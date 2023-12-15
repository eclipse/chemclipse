/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ConcentrationValidator implements IValidator<Object> {

	private static final String ERROR = "Please enter a correct concentration, e.g. 10 mg/L.";
	private static final String ERROR_VALUE_RANGE = "The concentration must be not <= 0.";
	//
	private double concentration = 0.0d;
	private String unit = "";

	@Override
	public IStatus validate(Object value) {

		String message = null;
		this.concentration = 0.0d;
		this.unit = "";
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String text) {
				text = text.trim();
				String[] values = text.split(" ");
				if(values.length != 2) {
					message = ERROR;
				} else {
					try {
						double concentration = Double.parseDouble(values[0]);
						if(concentration <= 0.0d) {
							message = ERROR_VALUE_RANGE;
						} else {
							this.concentration = concentration;
							this.unit = values[1];
						}
					} catch(NumberFormatException e) {
						message = ERROR;
					}
				}
			} else {
				message = ERROR;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public double getConcentration() {

		return concentration;
	}

	public String getUnit() {

		return unit;
	}
}
