/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.validators.TargetValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;

public class TargetTemplateInputValidator implements IInputValidator {

	private TargetValidator targetValidator = new TargetValidator();
	private Set<String> names = new HashSet<>();

	public TargetTemplateInputValidator(Set<String> names) {
		if(names != null) {
			this.names = names;
		}
	}

	@Override
	public String isValid(String target) {

		IStatus status = targetValidator.validate(target);
		if(status.isOK()) {
			String name = targetValidator.getName();
			if(names.contains(name)) {
				return "The target templates already exists.";
			}
		} else {
			return status.getMessage();
		}
		return null;
	}
}
