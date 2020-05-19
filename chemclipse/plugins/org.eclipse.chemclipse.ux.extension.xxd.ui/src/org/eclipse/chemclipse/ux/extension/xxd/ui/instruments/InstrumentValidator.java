/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.instruments;

import org.eclipse.chemclipse.support.util.InstrumentListUtil;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class InstrumentValidator implements IValidator {

	private static final String ERROR = "Please enter a correct instrument.";
	private static final String ERROR_CONTENT = "The following character must be not used: " + InstrumentListUtil.SEPARATOR_TOKEN;
	//
	private String identifier = "";
	private String name = "";
	private String description = "";

	@Override
	public IStatus validate(Object value) {

		String message = null;
		//
		if(value == null) {
			message = ERROR;
		} else {
			if(value instanceof String) {
				String[] values = value.toString().trim().split("\\" + InstrumentListUtil.SEPARATOR_ENTRY);
				//
				identifier = values.length > 0 ? values[0].trim() : "";
				message = checkContent(identifier);
				//
				if(message == null) {
					name = values.length > 1 ? values[1].trim() : "";
					message = checkContent(name);
				}
				//
				if(message == null) {
					description = values.length > 2 ? values[2].trim() : "";
					message = checkContent(description);
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

	private String checkContent(String content) {

		if(content.contains(InstrumentListUtil.SEPARATOR_TOKEN)) {
			return ERROR_CONTENT;
		} else {
			return null;
		}
	}

	public String getIdentifier() {

		return identifier;
	}

	public String getName() {

		return name;
	}

	public String getDescription() {

		return description;
	}
}
