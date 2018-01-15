/*******************************************************************************
 * Copyright (c) 2018 pwenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * pwenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class NameValidator implements IValidator, VerifyListener {

	@Override
	public void verifyText(VerifyEvent e) {

		if(e.text != null) {
			System.out.println("A");
		} else {
			System.out.println("B");
		}
	}

	@Override
	public IStatus validate(Object value) {

		String messageError = "Please enter a correct name.";
		String message = null;
		//
		if(value == null) {
			message = messageError;
		} else {
			if(value instanceof String) {
				String text = (String)value;
				if(text.trim().length() < 1) {
					message = messageError;
				}
			} else {
				message = messageError;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}
}
