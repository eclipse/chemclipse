/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

// TODO Is this class used?
public class QuantInputValidator implements IInputValidator {

	@SuppressWarnings("unused")
	private String[] items = new String[]{};

	public QuantInputValidator(String[] list) {

		if(list != null) {
			items = list;
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String target) {

		// TODO
		return null;
	}
}
