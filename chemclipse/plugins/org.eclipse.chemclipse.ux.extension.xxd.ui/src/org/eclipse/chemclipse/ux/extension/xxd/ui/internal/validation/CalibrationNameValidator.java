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

public class CalibrationNameValidator implements IValidator<Object> {

	private static final String ERROR = ExtensionMessages.enterCalibrationIndexName;
	//
	private String name = "";

	@Override
	public IStatus validate(Object value) {

		String message = null;
		this.name = "";
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String text) {
				text = text.trim();
				if(text.length() < 1) {
					message = ERROR;
				} else {
					this.name = text;
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

	public String getName() {

		return name;
	}
}
