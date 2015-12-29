/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class FloatFieldEditor extends StringFieldEditor {

	private float minValue = Float.MIN_VALUE;
	private float maxValue = Float.MAX_VALUE;

	public FloatFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	public FloatFieldEditor(String name, String labelText, float minValue, float maxValue, Composite parent) {
		super(name, labelText, parent);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	protected boolean checkState() {

		Text textControl = getTextControl();
		if(textControl == null) {
			return false;
		}
		String stringValue = textControl.getText();
		Float value;
		try {
			value = Float.valueOf(stringValue);
			if(value >= minValue && value <= maxValue) {
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
			Float value = getPreferenceStore().getFloat(getPreferenceName());
			textControl.setText(value.toString());
		}
		super.doLoad();
	}

	@Override
	protected void doLoadDefault() {

		Text textControl = getTextControl();
		if(textControl != null) {
			Float value = getPreferenceStore().getDefaultFloat(getPreferenceName());
			textControl.setText(value.toString());
		}
		valueChanged();
	}

	@Override
	protected void doStore() {

		Text textControl = getTextControl();
		if(textControl != null) {
			float value = Float.valueOf(textControl.getText());
			getPreferenceStore().setValue(getPreferenceName(), value);
		}
	}

	private void setAndShowErrorMessage() {

		showErrorMessage("Allowed range (MIN = " + minValue + ")(MAX = " + maxValue + ")");
	}
}
