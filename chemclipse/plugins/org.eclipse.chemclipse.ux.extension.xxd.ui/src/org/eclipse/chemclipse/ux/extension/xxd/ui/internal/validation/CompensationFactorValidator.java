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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class CompensationFactorValidator implements IValidator<Object> {

	private static final String ERROR = ExtensionMessages.enterCorrectCompensationFactor;
	private static final String ERROR_VALUE_RANGE = ExtensionMessages.compensationFactorMustNotBeNegative;
	//
	private double compensationFactor = 0.0d;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		this.compensationFactor = 0.0d;
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String text) {
				text = text.trim();
				try {
					double compensationFactor = Double.parseDouble(text);
					if(compensationFactor <= 0.0d) {
						message = ERROR_VALUE_RANGE;
					} else {
						this.compensationFactor = compensationFactor;
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

	public double getCompensationFactor() {

		return compensationFactor;
	}
}