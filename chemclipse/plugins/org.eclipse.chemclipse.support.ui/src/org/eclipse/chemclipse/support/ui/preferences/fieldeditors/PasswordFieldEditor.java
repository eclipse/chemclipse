/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class PasswordFieldEditor extends StringFieldEditor {

	public PasswordFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	public PasswordFieldEditor(String name, String labelText, int width, Composite parent) {
		super(name, labelText, width, parent);
	}

	public PasswordFieldEditor(String name, String labelText, int width, int strategy, Composite parent) {
		super(name, labelText, width, strategy, parent);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		super.doFillIntoGrid(parent, numColumns);
		getTextControl().setEchoChar('*');
	}
}
