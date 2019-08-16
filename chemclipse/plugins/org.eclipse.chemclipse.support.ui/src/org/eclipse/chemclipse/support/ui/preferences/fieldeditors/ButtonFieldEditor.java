/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.fieldeditors;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ButtonFieldEditor extends FieldEditor {

	private Button button;

	public Button getButton() {

		return button;
	}

	public ButtonFieldEditor(String text, Composite parent) {
		super("BUTTONX", text, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		GridData gridData = (GridData)this.button.getLayoutData();
		gridData.horizontalSpan = numColumns;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		this.button = new Button(parent, SWT.NONE);
		this.button.setText(getLabelText());
		GridData gridData = new GridData();
		gridData.horizontalSpan = numColumns;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessVerticalSpace = false;
		this.button.setLayoutData(gridData);
	}

	@Override
	protected void doLoad() {

	}

	@Override
	protected void doLoadDefault() {

	}

	@Override
	protected void doStore() {

	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}
}
