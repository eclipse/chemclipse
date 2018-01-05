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

import org.eclipse.chemclipse.support.util.TargetListUtil;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.List;

public class TargetInputValidator implements IInputValidator {

	private String[] items;

	public TargetInputValidator() {
		items = new String[]{};
	}

	public TargetInputValidator(List list) {
		if(list != null) {
			items = list.getItems();
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String newTarget) {

		if(newTarget.equals("")) {
			return "The target must be not empty.";
		} else if(newTarget.contains(TargetListUtil.SEPARATOR_TOKEN)) {
			return "The target must not contain the following character: " + TargetListUtil.SEPARATOR_TOKEN;
		} else {
			for(String item : items) {
				if(item.equals(newTarget)) {
					return "The target already exists.";
				}
			}
		}
		return null;
	}
}
