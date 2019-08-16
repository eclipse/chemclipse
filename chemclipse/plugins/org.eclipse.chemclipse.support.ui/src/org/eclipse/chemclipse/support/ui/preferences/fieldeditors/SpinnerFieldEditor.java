/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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
import org.eclipse.swt.widgets.Spinner;

public class SpinnerFieldEditor extends FieldEditor {

	private int min;
	private int max;
	private Spinner spinner;

	public SpinnerFieldEditor(String name, String labelText, int min, int max, Composite parent) {
		init(name, labelText);
		this.min = min;
		this.max = max;
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spinner.setMinimum(min);
		spinner.setMaximum(max);
	}

	@Override
	protected void doLoad() {

		spinner.setSelection(getPreferenceStore().getInt(getPreferenceName()));
	}

	@Override
	protected void doLoadDefault() {

		spinner.setSelection(getPreferenceStore().getDefaultInt(getPreferenceName()));
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), spinner.getSelection());
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}
}
