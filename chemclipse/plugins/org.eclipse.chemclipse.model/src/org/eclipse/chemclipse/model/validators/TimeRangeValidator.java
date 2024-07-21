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
package org.eclipse.chemclipse.model.validators;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TimeRangeValidator extends ValueParserSupport implements IValidator<Object> {

	private String identifier = "";
	private int start = 0;
	private int center = 0;
	private int stop = 0;

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = "Value can't be null";
		} else {
			if(value instanceof String) {
				String text = ((String)value).trim();
				if(text.isEmpty()) {
					message = "Entry can't be empty";
				} else {
					/*
					 * Extract the name
					 */
					identifier = "";
					start = 0;
					center = 0;
					stop = 0;
					//
					String[] values = text.trim().split("\\" + '|'); // The pipe needs to be escaped.
					if(values.length > 1) {
						/*
						 * Evaluation
						 */
						identifier = parseString(values, 0);
						if(identifier.isBlank()) {
							message = "The identifier must not be blank.";
						}
						//
						start = parseInteger(values, 1);
						if(start < 0) {
							message = "The start must not be < 0.";
						}
						//
						center = parseInteger(values, 2);
						if(start < 0) {
							message = "The center must not be < 0.";
						}
						//
						stop = parseInteger(values, 1);
						if(stop < 0) {
							message = "The stop must not be < 0.";
						}
						//
						if(start > stop) {
							message = "Start must not be > than stop.";
						}
						//
					} else {
						message = "Please enter a valid identifier.";
					}
				}
			} else {
				message = "Value has to be a string.";
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public TimeRange getSetting() {

		return new TimeRange(identifier, start, center, stop);
	}
}