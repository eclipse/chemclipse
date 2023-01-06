/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import org.eclipse.jface.dialogs.IInputValidator;

public class QuantitationReferenceInputValidator implements IInputValidator {

	private String[] items = new String[]{};

	public QuantitationReferenceInputValidator(String[] list) {

		if(list != null) {
			items = list;
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String target) {

		if(target == null || target.isEmpty()) {
			return "The quantitation reference must be not empty.";
		}
		//
		for(String item : items) {
			if(item.equals(target)) {
				return "The quantitation reference exists already.";
			}
		}
		return null;
	}
}