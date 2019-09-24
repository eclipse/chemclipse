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
package org.eclipse.chemclipse.support.settings.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class MinMaxValidator<T extends Number & Comparable<T>> implements IValidator {

	private String fieldName;
	private T min;
	private T max;
	private Class<T> type;

	public MinMaxValidator(String fieldName, T min, T max, Class<T> type) {
		this.fieldName = fieldName;
		this.min = min;
		this.max = max;
		this.type = type;
	}

	@Override
	public IStatus validate(Object value) {

		if(type.isInstance(value)) {
			T number = type.cast(value);
			if(number.compareTo(min) < 0) {
				return ValidationStatus.error(fieldName + " can't be smaller than " + min);
			}
			if(number.compareTo(max) > 0) {
				return ValidationStatus.error(fieldName + " can't be larger than " + max);
			}
			return ValidationStatus.ok();
		}
		return ValidationStatus.error("A number between " + min + " and " + max + " is required for " + fieldName);
	}
}
