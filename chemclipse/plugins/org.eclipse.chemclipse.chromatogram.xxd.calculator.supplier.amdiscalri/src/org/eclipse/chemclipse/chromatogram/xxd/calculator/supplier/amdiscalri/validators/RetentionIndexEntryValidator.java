/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.validators;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class RetentionIndexEntryValidator extends ValueParserSupport implements IValidator<String> {

	public static final String EXAMPLE_SINGLE = "22.549 | 1400.0 | C14";
	//
	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	private static final String ERROR_MESSAGE = "";
	private static final String ERROR_TOKEN = "The item must not contain: " + SEPARATOR_TOKEN;
	//
	private int retentionTime = 0;
	private float retentionIndex = 0.0f;
	private String name = "";

	@Override
	public IStatus validate(String value) {

		String message = null;
		if(value == null) {
			message = ERROR_MESSAGE;
		} else {
			String text = ((String)value).trim();
			if(text.contains(SEPARATOR_TOKEN)) {
				message = ERROR_TOKEN;
			} else if(text.isEmpty() || text.isBlank()) {
				message = ERROR_MESSAGE;
			} else {
				String[] values = text.trim().split("\\" + SEPARATOR_ENTRY); // The pipe needs to be escaped.
				if(values.length >= 3) {
					double retentionTimeMinutes = parseDouble(values, 0);
					retentionTime = (int)(retentionTimeMinutes * IChromatogramOverview.MINUTE_CORRELATION_FACTOR); // Minutes to milliseconds.
					if(retentionTime <= 0) {
						message = "Please type in a valid retention time.";
					} else {
						retentionIndex = parseFloat(values, 1);
						if(retentionIndex <= 0) {
							message = "Please type in a valid retention index.";
						} else {
							name = parseString(values, 2);
						}
					}
				}
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public IRetentionIndexEntry getSetting() {

		return new RetentionIndexEntry(retentionTime, retentionIndex, name);
	}
}