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
package org.eclipse.chemclipse.chromatogram.vsd.filter.validators;

import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignal;
import org.eclipse.chemclipse.chromatogram.vsd.filter.model.WavenumberSignals;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class WavenumberSignalsValidator extends ValueParserSupport implements IValidator<Object> {

	private static final String ERROR_ENTRY = "Please define a wavenumber and intensity.";
	private static final String ERROR_TOKEN = "The restricted token '" + WavenumberSignals.SEPARATOR_TOKEN + "' is used.";
	private static final String ERROR_EMPTY = "No wavenumber signals are available yet.";
	//
	private double wavenumber = 0.0d;
	private double intensity = 0.0d;

	@Override
	public IStatus validate(Object value) {

		wavenumber = 0.0d;
		intensity = 0.0d;
		//
		String message = null;
		if(value == null) {
			message = ERROR_ENTRY;
		} else {
			if(value instanceof String text) {
				text = text.trim();
				if(text.contains(WavenumberSignals.SEPARATOR_TOKEN)) {
					message = ERROR_TOKEN;
				} else if("".equals(text.trim())) {
					message = ERROR_ENTRY;
				} else {
					String[] values = text.trim().split("\\" + WavenumberSignals.SEPARATOR_ENTRY); // The pipe needs to be escaped.
					if(values.length >= 2) {
						wavenumber = parseDouble(values, 0);
						intensity = parseDouble(values, 1);
					} else {
						message = ERROR_ENTRY;
					}
				}
			} else if(value instanceof WavenumberSignals wavenumberSignals) {
				if(wavenumberSignals.isEmpty()) {
					message = ERROR_EMPTY;
				}
			} else {
				message = ERROR_ENTRY;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public WavenumberSignal getSetting() {

		WavenumberSignal setting = new WavenumberSignal();
		//
		setting.setWavenumber(wavenumber);
		setting.setIntensity(intensity);
		//
		return setting;
	}
}