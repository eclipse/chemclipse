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

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextFieldEditor extends FieldEditor {

	private Text text;

	public TextFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.widthHint = 400;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		text.setLayoutData(gridData);
	}

	@Override
	protected void doLoad() {

		text.setText(getPreferenceStore().getString(getPreferenceName()));
	}

	@Override
	protected void doLoadDefault() {

		text.setSelection(getPreferenceStore().getDefaultInt(getPreferenceName()));
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), text.getText().trim());
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}
}
