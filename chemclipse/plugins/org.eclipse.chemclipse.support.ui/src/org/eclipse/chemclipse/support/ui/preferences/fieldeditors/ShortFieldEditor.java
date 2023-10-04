/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A field editor for an Short type preference.
 */
public class ShortFieldEditor extends StringFieldEditor {

	private int minValidValue = Short.MIN_VALUE;
	private int maxValidValue = Short.MAX_VALUE;
	private static final int DEFAULT_TEXT_LIMIT = 10;

	/**
	 * Creates a new Short field editor
	 */
	protected ShortFieldEditor() {

	}

	/**
	 * Creates an Short field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public ShortFieldEditor(String name, String labelText, Composite parent) {

		this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
	}

	/**
	 * Creates an Short field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param textLimit
	 *            the maximum number of characters in the text.
	 */
	public ShortFieldEditor(String name, String labelText, Composite parent, int textLimit) {

		init(name, labelText);
		setTextLimit(textLimit);
		setEmptyStringAllowed(false);
		showErrorMessage("Value must be a short.");
		createControl(parent);
	}

	/**
	 * Sets the range of valid values for this field.
	 *
	 * @param min
	 *            the minimum allowed value (inclusive)
	 * @param max
	 *            the maximum allowed value (inclusive)
	 */
	public void setValidRange(int min, int max) {

		minValidValue = min;
		maxValidValue = max;
		showErrorMessage(NLS.bind(SupportMessages.allowedRange, min, max));
	}

	@Override
	protected boolean checkState() {

		Text text = getTextControl();
		if(text == null) {
			return false;
		}
		String numberString = text.getText();
		try {
			Short number = Short.parseShort(numberString);
			if(number >= minValidValue && number <= maxValidValue) {
				clearErrorMessage();
				return true;
			}
			showErrorMessage();
			return false;
		} catch(NumberFormatException e) {
			showErrorMessage();
		}
		return false;
	}

	@Override
	protected void doLoad() {

		Text text = getTextControl();
		if(text != null) {
			short value = (short)getPreferenceStore().getInt(getPreferenceName());
			text.setText(Short.toString(value));
			oldValue = Short.toString(value);
		}
	}

	@Override
	protected void doLoadDefault() {

		Text text = getTextControl();
		if(text != null) {
			short value = (short)getPreferenceStore().getDefaultInt(getPreferenceName());
			text.setText(Short.toString(value));
		}
		valueChanged();
	}

	@Override
	protected void doStore() {

		Text text = getTextControl();
		if(text != null) {
			Short b = Short.valueOf(text.getText());
			getPreferenceStore().setValue(getPreferenceName(), b.shortValue());
		}
	}

	/**
	 * Returns this field editor's current value as an Short.
	 *
	 * @return the value
	 * @exception NumberFormatException
	 *                if the <code>String</code> does not
	 *                contain a parsable Short
	 */
	public int getShortValue() throws NumberFormatException {

		return Short.parseShort(getStringValue());
	}
}
