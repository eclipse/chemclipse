/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.cas;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class CasValidator implements IValidator<String> {

	private static final String ERROR_MESSAGE = "The CAS# is invalid.";
	private boolean allowEmpty = false;

	public CasValidator(boolean allowEmpty) {

		this.allowEmpty = allowEmpty;
	}

	@Override
	public IStatus validate(String value) {

		String message = null;
		//
		if(value != null) {
			if(value.isBlank()) {
				if(!allowEmpty) {
					message = ERROR_MESSAGE;
				}
			} else {
				if(!CasSupport.isValid(value)) {
					message = ERROR_MESSAGE;
				}
			}
		} else {
			message = ERROR_MESSAGE;
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}
}