/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ResponseFactorValidator implements IValidator {

	private static final String ERROR = "Please enter a correct response factor.";
	private static final String ERROR_VALUE_RANGE = "The response factor must be not <= 0.";
	//
	private double responseFactor = 0.0d;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		this.responseFactor = 0.0d;
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				String text = ((String)value).trim();
				try {
					double responseFactor = Double.parseDouble(text);
					if(responseFactor <= 0.0d) {
						message = ERROR_VALUE_RANGE;
					} else {
						this.responseFactor = responseFactor;
					}
				} catch(NumberFormatException e) {
					message = ERROR;
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

	public double getResponseFactor() {

		return responseFactor;
	}
}
