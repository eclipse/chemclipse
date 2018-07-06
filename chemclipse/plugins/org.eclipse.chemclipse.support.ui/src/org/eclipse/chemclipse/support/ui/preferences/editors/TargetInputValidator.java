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
package org.eclipse.chemclipse.support.ui.preferences.editors;

import org.eclipse.chemclipse.support.validators.TargetValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.List;

public class TargetInputValidator implements IInputValidator {

	private String[] items = new String[]{};
	private TargetValidator targetValidator = new TargetValidator();

	public TargetInputValidator(List list) {
		if(list != null) {
			items = list.getItems();
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String target) {

		IStatus status = targetValidator.validate(target);
		if(status.isOK()) {
			for(String item : items) {
				if(item.equals(target)) {
					return "The target already exists.";
				}
			}
		} else {
			return status.getMessage();
		}
		return null;
	}
}
