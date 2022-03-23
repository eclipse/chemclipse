/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements constructor
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class StringRegexFieldEditor extends StringFieldEditor {

	private Pattern pattern;
	private boolean useNegation = false;

	public StringRegexFieldEditor(String name, String labelText, Composite parent) {

		this(name, labelText, "(^.*$)", parent);
	}

	public StringRegexFieldEditor(String name, String labelText, String regularExpression, Composite parent) {

		this(name, labelText, regularExpression, false, parent);
	}

	public StringRegexFieldEditor(String name, String labelText, String regularExpression, boolean useNegation, Composite parent) {

		super(name, labelText, parent);
		this.pattern = Pattern.compile(regularExpression);
		this.useNegation = useNegation;
	}

	@Override
	protected boolean doCheckState() {

		boolean checkState = super.doCheckState();
		if(checkState) {
			Matcher matcher = pattern.matcher(getStringValue());
			boolean matches = matcher.matches();
			checkState = useNegation ? !matches : matches;
		}
		return checkState;
	}
}