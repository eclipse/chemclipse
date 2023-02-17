/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ExtendedLabelFieldEditor extends FieldEditor {

	private Label infoLabel;
	private String errorMessage;
	private Color color;

	public ExtendedLabelFieldEditor(String label, String info, Composite parent) {

		this(label, info, null, parent);
	}

	public ExtendedLabelFieldEditor(String label, String info, Color color, Composite parent) {

		init("extendedLabelFieldEditor", label);
		errorMessage = "";
		this.color = color;
		createControl(parent);
		getInfoControl(parent).setText(info);
	}

	public ExtendedLabelFieldEditor(String label, int info, Color color, Composite parent) {

		this(label, Integer.toString(info), color, parent);
	}

	public ExtendedLabelFieldEditor(String label, int info, Composite parent) {

		this(label, Integer.toString(info), null, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		GridData gd = (GridData)infoLabel.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		infoLabel = getInfoControl(parent);
		GridData gridData = new GridData();
		gridData.horizontalSpan = numColumns - 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		infoLabel.setLayoutData(gridData);
	}

	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}

	public Label getInfoControl(Composite parent) {

		if(infoLabel == null) {
			infoLabel = new Label(parent, SWT.NONE);
			infoLabel.setFont(parent.getFont());
			if(color != null) {
				infoLabel.setForeground(color);
			}
		}
		return infoLabel;
	}

	@Override
	public boolean isValid() {

		return true;
	}

	/**
	 * Sets the error message that will be displayed when and if
	 * an error occurs.
	 * 
	 * @param message
	 *            the error message
	 */
	public void setErrorMessage(String message) {

		errorMessage = message;
	}

	@Override
	public void setFocus() {

		if(infoLabel != null) {
			infoLabel.setFocus();
		}
	}

	public void setInfoLabelText(String text) {

		if(infoLabel != null) {
			infoLabel.setText(text);
		}
	}

	public void showErrorMessage() {

		showErrorMessage(errorMessage);
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {

		super.setEnabled(enabled, parent);
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
}
