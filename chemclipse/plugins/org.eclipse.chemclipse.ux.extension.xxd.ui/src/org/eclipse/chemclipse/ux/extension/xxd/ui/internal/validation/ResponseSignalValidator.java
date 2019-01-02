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

import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.ResponseSignal;
import org.eclipse.chemclipse.support.validators.AbstractValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class ResponseSignalValidator extends AbstractValidator implements IValidator {

	public static final String DEMO = "TIC | 1.5 | 289893.38";
	//
	private static final String DELIMITER = "|";
	private static final String ERROR_TARGET = "Please enter a response signal, e.g.: " + DEMO;
	//
	private double signal;
	private double concentration;
	private double response;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR_TARGET;
		} else {
			if(value instanceof String) {
				String text = ((String)value).trim();
				if("".equals(text.trim())) {
					message = ERROR_TARGET;
				} else {
					String[] values = text.trim().split("\\" + DELIMITER);
					//
					String signalValue = parseString(values, 0);
					if("TIC".equals(signalValue)) {
						signal = IQuantitationSignal.TIC_SIGNAL;
					} else {
						signal = parseDouble(values, 0);
					}
					//
					concentration = parseDouble(values, 1);
					response = parseDouble(values, 2);
				}
			} else {
				message = ERROR_TARGET;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public IResponseSignal getResponseSignal() {

		return new ResponseSignal(signal, concentration, response);
	}
}
