/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This field editor displays the retention time in minutes but stores it at milliseconds.
 * int DEF_RETENTION_TIME_WINDOW = 12000; // = 0.2 minutes
 * int MIN_RETENTION_TIME_WINDOW = 60; // = 0.001 minutes
 * int MAX_RETENTION_TIME_WINDOW = 60000; // = 1.0 minutes;
 */
public class RetentionTimeMinutesFieldEditor extends StringFieldEditor {

	private double minRetentionTimeMinutes = Integer.MIN_VALUE;
	private double maxRetentionTimeMinutes = Integer.MAX_VALUE;
	private double MINUTE_CORRELATION_FACTOR = 60000.0d; // 1ms * 1000 = 1s; 1s * 60 = 1min
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public RetentionTimeMinutesFieldEditor(String name, String labelText, int minRetentionTimeMilliseconds, int maxRetentionTimeMilliseconds, Composite parent) {
		super(name, labelText, parent);
		minRetentionTimeMinutes = minRetentionTimeMilliseconds / MINUTE_CORRELATION_FACTOR;
		maxRetentionTimeMinutes = maxRetentionTimeMilliseconds / MINUTE_CORRELATION_FACTOR;
	}

	@Override
	protected boolean checkState() {

		Text textControl = getTextControl();
		if(textControl == null) {
			return false;
		}
		String stringValue = textControl.getText();
		double value;
		try {
			value = Double.valueOf(stringValue);
			if(value >= minRetentionTimeMinutes && value <= maxRetentionTimeMinutes) {
				clearErrorMessage();
				return true;
			} else {
				setAndShowErrorMessage();
			}
		} catch(NumberFormatException e) {
			setAndShowErrorMessage();
		}
		return false;
	}

	@Override
	protected void doLoad() {

		Text textControl = getTextControl();
		if(textControl != null) {
			Double retentionTimeMinutes = getPreferenceStore().getInt(getPreferenceName()) / MINUTE_CORRELATION_FACTOR;
			textControl.setText(retentionTimeMinutes.toString());
			oldValue = getPreferenceStore().getString(getPreferenceName());
		}
	}

	@Override
	protected void doLoadDefault() {

		Text textControl = getTextControl();
		if(textControl != null) {
			Double value = getPreferenceStore().getDefaultInt(getPreferenceName()) / MINUTE_CORRELATION_FACTOR;
			textControl.setText(value.toString());
		}
		valueChanged();
	}

	@Override
	protected void doStore() {

		Text textControl = getTextControl();
		if(textControl != null) {
			double value = Double.valueOf(textControl.getText());
			int retentionTime = (int)(value * MINUTE_CORRELATION_FACTOR);
			getPreferenceStore().setValue(getPreferenceName(), retentionTime);
		}
	}

	private void setAndShowErrorMessage() {

		showErrorMessage("Allowed retention time range: " + decimalFormat.format(minRetentionTimeMinutes) + " - " + decimalFormat.format(maxRetentionTimeMinutes) + " minutes");
	}
}
