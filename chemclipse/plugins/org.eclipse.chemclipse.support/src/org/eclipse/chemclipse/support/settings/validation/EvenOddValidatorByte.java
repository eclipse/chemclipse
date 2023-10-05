/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.validation;

import org.eclipse.chemclipse.support.settings.ByteSettingsProperty.Validation;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class EvenOddValidatorByte implements IValidator<Object> {

	private Validation validation;
	private String fieldName;

	public EvenOddValidatorByte(String fieldName, Validation validation) {

		this.fieldName = fieldName;
		this.validation = validation;
	}

	@Override
	public IStatus validate(Object value) {

		if(value instanceof Number number) {
			byte byteValue = number.byteValue();
			if(validation == Validation.ODD_NUMBER_INCLUDING_ZERO) {
				if(byteValue == 0) {
					return ValidationStatus.ok();
				}
				if(byteValue % 2 == 0) {
					return ValidationStatus.error(fieldName + " must be odd or zero.");
				}
			}
			if(validation == Validation.ODD_NUMBER) {
				if(byteValue % 2 == 0) {
					return ValidationStatus.error(fieldName + " must be odd.");
				}
			} else if(validation == Validation.EVEN_NUMBER) {
				if(byteValue % 2 != 0) {
					return ValidationStatus.error(fieldName + " must be even.");
				}
			}
			return ValidationStatus.ok();
		}
		return ValidationStatus.error(fieldName + " must be a number.");
	}
}
