/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.chemclipse.support.settings.IntSettingsProperty.Validation;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ExtendedLongFieldEditor extends org.eclipse.jface.preference.StringFieldEditor {

	private Validation validation;
	private long min = Long.MIN_VALUE;
	private long max = Long.MAX_VALUE;

	public ExtendedLongFieldEditor(String name, String labelText, Composite parent) {

		super(name, labelText, parent);
	}

	public ExtendedLongFieldEditor(String name, String labelText, long min, long max, Composite parent) {

		super(name, labelText, parent);
		setValidRange(min, max);
	}

	public ExtendedLongFieldEditor(String name, String labelText, long min, long max, Validation validation, Composite parent) {

		super(name, labelText, parent);
		setValidRange(min, max);
		setValidation(validation);
	}

	public void setValidRange(long min, long max) {

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
			long number = Long.parseLong(numberString);
			clearErrorMessage();
			if(validation != null) {
				if(validation == Validation.ODD_NUMBER_INCLUDING_ZERO) {
					if(number == 0L) {
						return true;
					}
					if(number % 2L == 0L) {
						showErrorMessage();
						return false;
					}
				}
				if(validation == Validation.ODD_NUMBER) {
					if(number % 2L == 0L) {
						showErrorMessage();
						return false;
					}
				} else if(validation == Validation.EVEN_NUMBER) {
					if(number % 2L != 0L) {
						showErrorMessage();
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
