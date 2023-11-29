/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import java.awt.List;

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.jface.dialogs.IInputValidator;

public class WavelengthInputValidator implements IInputValidator {

	private String[] items;

	public WavelengthInputValidator() {

		items = new String[]{};
	}

	public WavelengthInputValidator(List list) {

		if(list != null) {
			items = list.getItems();
		} else {
			items = new String[]{};
		}
	}

	@Override
	public String isValid(String newText) {

		/*
		 * Test if the input is an integer value.<br/> If yes, return null,
		 * otherwise throw a failure description.
		 */
		try {
			String wavelengthList[] = newText.trim().split("-"); //$NON-NLS-1$
			if(wavelengthList.length == 1) {
				String wavelength = String.valueOf(Integer.parseInt(wavelengthList[0].trim()));
				/*
				 * 0 = TIC must be not added.
				 */
				if(wavelength.equals("0")) { //$NON-NLS-1$
					return SupportMessages.ticMustNotBeAdded;
				}
			} else if(wavelengthList.length == 2) {
				String wavelength1 = String.valueOf(Integer.parseInt(wavelengthList[0].trim()));
				String wavelength2 = String.valueOf(Integer.parseInt(wavelengthList[1].trim()));
				/*
				 * 0 = TIC must be not added.
				 */
				if(wavelength1.equals("0") || wavelength2.equals("0")) { //$NON-NLS-1$ //$NON-NLS-2$
					return SupportMessages.ticMustNotBeAdded;
				}
			} else {
				return SupportMessages.inputMustBeValidRange;
			}
		} catch(NumberFormatException e) {
			return SupportMessages.inputMustBeIntegerOrIntegerRange;
		}
		/*
		 * Test if 0 (TIC) still exists.
		 */
		for(String item : items) {
			if(item.equals("0")) { //$NON-NLS-1$
				return SupportMessages.removeTICbeforeAddingWavelengthValues;
			}
		}
		/*
		 * Test if the ion value already exists.
		 */
		for(String item : items) {
			if(item.equals(newText)) {
				return SupportMessages.wavelengthValueAlreadyExists;
			}
		}
		return null;
	}
}
