/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - odd number including zero (for window sizes)
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.validation;

import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class EvenOddValidator implements IValidator {

	private Validation validation;
	private String fieldName;

	public EvenOddValidator(String fieldName, Validation validation) {

		this.fieldName = fieldName;
		this.validation = validation;
	}

	@Override
	public IStatus validate(Object value) {

		if(value instanceof Number) {
			int intValue = ((Number)value).intValue();
			if(validation == Validation.ODD_NUMBER_INCLUDING_ZERO) {
				if(intValue == 0) {
					return ValidationStatus.ok();
				}
				if(intValue % 2 == 0) {
					return ValidationStatus.error(fieldName + " must be odd or zero");
				}
			}
			if(validation == Validation.ODD_NUMBER) {
				if(intValue % 2 == 0) {
					return ValidationStatus.error(fieldName + " must be odd");
				}
			} else if(validation == Validation.EVEN_NUMBER) {
				if(intValue % 2 != 0) {
					return ValidationStatus.error(fieldName + " must be even");
				}
			}
			return ValidationStatus.ok();
		}
		return ValidationStatus.error(fieldName + " must be a number");
	}
}
