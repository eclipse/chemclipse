/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractExtendedWizardPage extends WizardPage implements IExtendedWizardPage {

	public AbstractExtendedWizardPage(String pageName) {

		super(pageName);
	}

	@Override
	public String validateInput(Text textInput, String errorMessage) {

		String message = null;
		String text = textInput.getText().trim();
		if(text == null || text.isEmpty()) {
			message = errorMessage;
		}
		return message;
	}

	@Override
	public String validateDoubleInput(Text textInput, DecimalFormat decimalFormat, String errorMessage) {

		return validateDoubleInput(textInput, decimalFormat, Double.MIN_VALUE, Double.MAX_VALUE, errorMessage);
	}

	@Override
	public String validateDoubleInput(Text textInput, DecimalFormat decimalFormat, double min, double max, String errorMessage) {

		String message = null;
		String text = textInput.getText().trim();
		if(text == null || text.isEmpty()) {
			message = errorMessage;
		} else {
			try {
				double value = decimalFormat.parse(text).doubleValue();
				if(value < min || value > max) {
					message = errorMessage;
				}
			} catch(ParseException e) {
				message = errorMessage;
			}
		}
		return message;
	}

	@Override
	public String validateInput(Combo comboInput, String errorMessage) {

		String message = null;
		String text = comboInput.getText().trim();
		if(text == null || text.isEmpty()) {
			message = errorMessage;
		}
		return message;
	}

	@Override
	public String validateDateInput(DateTime dateTime, String errorMessage) {

		String message = null;
		int day = dateTime.getDay();
		int month = dateTime.getMonth();
		int year = dateTime.getYear();
		//
		if(day < 1 || day > 12) {
			message = errorMessage;
		} else if(month < 0 || month > 11) {
			message = errorMessage;
		} else if(year < 1752 || year > 9999) {
			message = errorMessage;
		}
		return message;
	}

	/**
	 * Updates whether the next page can be selected or not.
	 * 
	 * @param message
	 */
	protected void updateStatus(String message) {

		setErrorMessage(message);
		setPageComplete(message == null);
	}
}
