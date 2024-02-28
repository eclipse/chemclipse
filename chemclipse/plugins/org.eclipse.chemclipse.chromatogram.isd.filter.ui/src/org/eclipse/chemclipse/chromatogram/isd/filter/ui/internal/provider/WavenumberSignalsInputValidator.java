/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.isd.filter.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.isd.filter.model.WavenumberSignal;
import org.eclipse.chemclipse.chromatogram.isd.filter.model.WavenumberSignals;
import org.eclipse.chemclipse.chromatogram.isd.filter.validators.WavenumberSignalsValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;

public class WavenumberSignalsInputValidator implements IInputValidator {

	private WavenumberSignalsValidator validator = new WavenumberSignalsValidator();
	private WavenumberSignals wavenumberSignals;

	public WavenumberSignalsInputValidator(WavenumberSignals wavenumberSignals) {

		this.wavenumberSignals = wavenumberSignals;
	}

	@Override
	public String isValid(String input) {

		IStatus status = validator.validate(input);
		if(status.isOK()) {
			WavenumberSignal wavenumberSignal = validator.getSetting();
			if(wavenumberSignals.contains(wavenumberSignal)) {
				return "The wavenumber exists already.";
			}
		} else {
			return status.getMessage();
		}
		//
		return null;
	}
}