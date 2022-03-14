/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

public class RetentionIndexValidator implements IValidator<Object> {

	private static final String ERROR = "Please enter a correct retention index, e.g. 600";
	private static final String ERROR_VALUE_RANGE = "The retention index must be not <= 0.";
	//
	private float retentionIndex = 0.0f;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		this.retentionIndex = 0.0f;
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				try {
					float retentionIndex = Float.parseFloat(((String)value).trim());
					if(retentionIndex <= 0.0d) {
						message = ERROR_VALUE_RANGE;
					} else {
						this.retentionIndex = retentionIndex;
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

	public float getRetentionIndex() {

		return retentionIndex;
	}
}
