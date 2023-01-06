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

import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class RetentionTimeValidator implements IValidator<Object> {

	private static final String ERROR = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.ENTER_CORRECT_RETION_TIME_MINUTES_EXAMPLE) + " 4.25";
	private static final String ERROR_VALUE_RANGE = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_TIME_MUST_NOT_BE_NEGATIVE);
	//
	private double retentionTime = 0.0d;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		this.retentionTime = 0.0d;
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String text) {
				try {
					double retentionTime = Double.parseDouble(text.trim());
					if(retentionTime <= 0.0d) {
						message = ERROR_VALUE_RANGE;
					} else {
						this.retentionTime = retentionTime;
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

	public double getRetentionTime() {

		return retentionTime;
	}
}
