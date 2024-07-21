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
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.support.util.ValueParserSupport;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TimeRangesValidator extends ValueParserSupport implements IValidator<Object> {

	@Override
	public IStatus validate(Object value) {

		String message = null;
		if(value == null) {
			message = "The time ranges are empty.";
		} else {
			if(value instanceof TimeRanges timeRanges) {
				for(TimeRange timeRange : timeRanges.values()) {
					if(timeRange.getIdentifier().isEmpty()) {
						message = "The identifier is empty.";
					} else {
						if(timeRange.getStart() > timeRange.getStop()) {
							message = "The start must not be greater than the stop value.";
						}
					}
				}
			} else {
				message = "The settings class is not of type: " + TimeRanges.class.getName();
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}
}