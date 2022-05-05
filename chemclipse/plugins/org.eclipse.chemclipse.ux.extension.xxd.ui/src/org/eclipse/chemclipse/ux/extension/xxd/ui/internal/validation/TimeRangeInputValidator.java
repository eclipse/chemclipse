/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.ux.extension.xxd.ui.ranges.TimeRangeValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;

public class TimeRangeInputValidator implements IInputValidator {

	private TimeRangeValidator validator = new TimeRangeValidator();
	private Set<String> identifier = new HashSet<>();

	public TimeRangeInputValidator(Set<String> identifier) {

		if(identifier != null) {
			this.identifier = identifier;
		}
	}

	@Override
	public String isValid(String target) {

		IStatus status = validator.validate(target);
		if(status.isOK()) {
			String name = validator.getIdentifier();
			if(identifier.contains(name)) {
				return "The time range already exists.";
			}
		} else {
			return status.getMessage();
		}
		return null;
	}
}