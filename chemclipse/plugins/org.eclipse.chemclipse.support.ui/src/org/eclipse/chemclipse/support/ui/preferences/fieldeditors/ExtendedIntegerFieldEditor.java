/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add even/odd validation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.chemclipse.support.ui.messages.SupportMessages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ExtendedIntegerFieldEditor extends org.eclipse.jface.preference.IntegerFieldEditor {

	private Validation validation;
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;

	public ExtendedIntegerFieldEditor(String name, String labelText, Composite parent) {

		super(name, labelText, parent);
	}

	public ExtendedIntegerFieldEditor(String name, String labelText, int min, int max, Composite parent) {

		super(name, labelText, parent);
		setValidRange(min, max);
	}

	public ExtendedIntegerFieldEditor(String name, String labelText, int min, int max, Validation validation, Composite parent) {

		super(name, labelText, parent);
		setValidRange(min, max);
		setValidation(validation);
	}

	@Override
	public void setValidRange(int min, int max) {

		this.min = min;
		this.max = max;
	}

	public void setValidation(Validation validation) {

		this.validation = validation;
	}

	@Override
	protected boolean checkState() {

		Text text = getTextControl();
		if(text == null) {
			return false;
		}
		String numberString = text.getText();
		try {
			int number = Integer.parseInt(numberString);
			clearErrorMessage();
			if(validation != null) {
				if(validation == Validation.ODD_NUMBER_INCLUDING_ZERO) {
					if(number == 0) {
						return true;
					}
					if(number % 2 == 0) {
						showErrorMessage(SupportMessages.INSTANCE().getMessage("ExtendedIntegerFieldEditor.errorMessageOddIncludingZero"));
						return false;
					}
				}
				if(validation == Validation.ODD_NUMBER) {
					if(number % 2 == 0) {
						showErrorMessage(SupportMessages.INSTANCE().getMessage("ExtendedIntegerFieldEditor.errorMessageOdd"));
						return false;
					}
				} else if(validation == Validation.EVEN_NUMBER) {
					if(number % 2 != 0) {
						showErrorMessage(SupportMessages.INSTANCE().getMessage("ExtendedIntegerFieldEditor.errorMessageEven"));
						return false;
					}
				}
			}
			if(number >= min && number <= max) {
				return true;
			}
		} catch(NumberFormatException e) {
			showErrorMessage();
		}
		showErrorMessage();
		return false;
	}
}
