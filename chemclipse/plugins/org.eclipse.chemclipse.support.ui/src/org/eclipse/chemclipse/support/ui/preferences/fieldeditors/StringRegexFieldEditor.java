/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class StringRegexFieldEditor extends StringFieldEditor {

	private String regularExpression = "^.*$";

	public StringRegexFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	public StringRegexFieldEditor(String name, String labelText, int width, Composite parent) {
		super(name, labelText, width, parent);
	}

	public StringRegexFieldEditor(String name, String labelText, int width, int strategy, Composite parent) {
		super(name, labelText, width, strategy, parent);
	}

	public void setRegEx(String regEx) {

		this.regularExpression = regEx;
	}

	public String getRegEx() {

		return regularExpression;
	}

	@Override
	protected boolean doCheckState() {

		String value = getStringValue();
		Pattern p = Pattern.compile(regularExpression);
		Matcher m = p.matcher(value);
		if(!m.matches()) {
			return false;
		}
		return super.doCheckState();
	}
}
