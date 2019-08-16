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
package org.eclipse.chemclipse.support.validators;

import org.eclipse.chemclipse.support.util.TargetListUtil;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TargetValidator implements IValidator {

	private static final String ERROR_TARGET = "Please enter target, e.g.: Styrene | 100-42-5 | comment | contributor | referenceId";
	private static final String ERROR_TOKEN = "The target must not contain: " + TargetListUtil.SEPARATOR_TOKEN;
	//
	private String name = "";
	private String casNumber = "";
	private String comments = "";
	private String contributor = "";
	private String referenceId = "";

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = ERROR_TARGET;
		} else {
			if(value instanceof String) {
				String text = ((String)value).trim();
				if(text.contains(TargetListUtil.SEPARATOR_TOKEN)) {
					message = ERROR_TOKEN;
				} else if("".equals(text.trim())) {
					message = ERROR_TARGET;
				} else {
					/*
					 * Extract name, casNumber, ...
					 */
					String[] values = text.trim().split("\\" + TargetListUtil.SEPARATOR_ENTRY); // The pipe needs to be escaped.
					if(values.length > 0) {
						name = values[0].trim();
						//
						if(values.length > 1) {
							casNumber = values[1].trim();
						}
						//
						if(values.length > 2) {
							comments = values[2].trim();
						}
						//
						if(values.length > 3) {
							contributor = values[3].trim();
						}
						//
						if(values.length > 4) {
							referenceId = values[4].trim();
						}
					} else {
						message = ERROR_TARGET;
					}
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

	public String getName() {

		return name;
	}

	public String getCasNumber() {

		return casNumber;
	}

	public String getComments() {

		return comments;
	}

	public String getContributor() {

		return contributor;
	}

	public String getReferenceId() {

		return referenceId;
	}
}
