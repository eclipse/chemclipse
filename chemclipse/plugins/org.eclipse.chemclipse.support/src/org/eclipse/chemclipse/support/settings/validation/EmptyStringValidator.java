/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class EmptyStringValidator implements IValidator<Object> {

	private static final String ERROR = "Please enter a value.";

	public EmptyStringValidator() {

	}

	@Override
	public IStatus validate(Object value) {

		if(value == null) {
			return ValidationStatus.error(ERROR);
		}
		if(value instanceof String text && text.isEmpty()) {
			return ValidationStatus.error(ERROR);
		}
		return ValidationStatus.ok();
	}
}
