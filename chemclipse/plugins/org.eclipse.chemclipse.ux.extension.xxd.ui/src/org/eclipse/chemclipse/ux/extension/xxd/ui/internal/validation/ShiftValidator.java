/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ShiftValidator implements IValidator<Object> {

	private double shift = 0.0d;

	@Override
	public IStatus validate(Object value) {

		this.shift = 0.0d;
		boolean parseError = true;
		//
		if(value != null) {
			if(value instanceof String text) {
				try {
					shift = Double.parseDouble(text.trim());
					parseError = false;
				} catch(NumberFormatException e) {
					// Don't catch it here.
				}
			}
		}
		//
		if(parseError) {
			return ValidationStatus.error(ExtensionMessages.enterValidNumber);
		} else {
			return ValidationStatus.ok();
		}
	}

	public double getShift() {

		return shift;
	}
}
