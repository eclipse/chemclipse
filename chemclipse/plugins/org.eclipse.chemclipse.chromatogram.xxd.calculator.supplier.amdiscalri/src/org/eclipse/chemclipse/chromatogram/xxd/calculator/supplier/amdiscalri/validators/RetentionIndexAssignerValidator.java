/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IndexNameMarker;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class RetentionIndexAssignerValidator extends ValueParserSupport implements IValidator<String> {

	public static final String EXAMPLE_SINGLE = "600 | Hexane";
	//
	public static final String WHITE_SPACE = " ";
	public static final String SEPARATOR_TOKEN = ";";
	public static final String SEPARATOR_ENTRY = "|";
	//
	private static final String ERROR_MESSAGE = "";
	private static final String ERROR_TOKEN = "The item must not contain: " + SEPARATOR_TOKEN;
	//
	private int retentionIndex = 0;
	private String name = "";

	@Override
	public IStatus validate(String value) {

		clear();
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
				if(values.length >= 2) {
					retentionIndex = parseInteger(values, 0);
					if(retentionIndex <= 0) {
						message = "Please type in a valid retention index.";
					} else {
						name = parseString(values, 1);
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

	private void clear() {

		retentionIndex = 0;
		name = "";
	}

	public IndexNameMarker getSetting() {

		return new IndexNameMarker(retentionIndex, name);
	}
}