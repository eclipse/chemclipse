/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.l10n.SupportMessages;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.List;

public class DilutionInputValidator implements IInputValidator {

	private String[] items;

	public DilutionInputValidator() {

		items = new String[]{};
	}

	public DilutionInputValidator(List list) {

		if(list != null) {
			items = list.getItems();
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String newInstrument) {

		if(newInstrument.equals("")) {
			return SupportMessages.labelTypeInDilution;
		} else {
			for(String item : items) {
				if(item.equals(newInstrument)) {
					return SupportMessages.labelDilutionExists;
				}
			}
		}
		return null;
	}
}
