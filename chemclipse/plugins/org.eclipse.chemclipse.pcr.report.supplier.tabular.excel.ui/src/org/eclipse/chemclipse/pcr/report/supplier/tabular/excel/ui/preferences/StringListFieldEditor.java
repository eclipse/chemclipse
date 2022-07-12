/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.excel.ui.preferences;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.preferences.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

public class StringListFieldEditor extends ListEditor {

	public StringListFieldEditor() {

		super();
	}

	public StringListFieldEditor(final String name, final String labelText, final Composite parent) {

		super(name, labelText, parent);
	}

	@Override
	public int getNumberOfControls() {

		return 3;
	}

	@Override
	protected String createList(final String[] items) {

		return StringUtils.createList(items);
	}

	@Override
	protected String getNewInputObject() {

		final InputDialog input = new InputDialog(getShell(), "Input", "Enter a new value:", "", new IInputValidator() {

			@Override
			public String isValid(final String newText) {

				if(newText.contains(StringUtils.SEPARATOR_TOKEN)) {
					return "Must not contain " + StringUtils.SEPARATOR_TOKEN;
				}
				return null;
			}
		});
		if(input.open() == Window.OK) {
			return input.getValue();
		}
		return null;
	}

	@Override
	protected String[] parseString(final String stringList) {

		return StringUtils.parseString(stringList);
	}
}
