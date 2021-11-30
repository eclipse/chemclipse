/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - add a description field for the regular expression
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings.validation;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class RegularExpressionValidator implements IValidator {

	private String fieldName;
	private Pattern regExp;
	private String description;
	private boolean multiline;

	public RegularExpressionValidator(String fieldName, Pattern regExp, String description, boolean multiline) {

		this.fieldName = fieldName;
		this.regExp = regExp;
		this.description = description;
		this.multiline = multiline;
	}

	@Override
	public IStatus validate(Object value) {

		if(value instanceof String) {
			String string = (String)value;
			if(multiline) {
				String[] lines = string.split("[\r\n]+");
				int n = 1;
				for(String line : lines) {
					if(!regExp.matcher(line).matches()) {
						return ValidationStatus.error("Line #" + n + " of " + fieldName + " is not formatted correctly.");
					}
					n++;
				}
			} else if(!regExp.matcher(string).matches()) {
				return ValidationStatus.error(fieldName + " " + description);
			}
			return ValidationStatus.ok();
		}
		return ValidationStatus.error("A value is required for " + fieldName);
	}
}
