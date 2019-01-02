/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class QuantitationCompoundValidator extends ValueParserSupport implements IValidator {

	public static final String DEMO = "Styrene | mg/kg | 5.68";
	//
	private static final String DELIMITER = "|";
	private static final String ERROR_TARGET = "Please enter a compound, e.g.: " + DEMO;
	//
	private String name;
	private String concentrationUnit;
	private int retentionTime;

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
					name = parseString(values, 0);
					concentrationUnit = parseString(values, 1);
					retentionTime = (int)(parseDouble(values, 2, 0.0d) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
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

	@SuppressWarnings("rawtypes")
	public IQuantitationCompound getQuantitationCompound() {

		return new QuantitationCompoundMSD(name, concentrationUnit, retentionTime);
	}
}
