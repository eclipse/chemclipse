/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DatabaseNameDialog extends TitleAreaDialog {

	private Text textDatabaseName;
	private String databaseName;

	public DatabaseNameDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {

		super.create();
		setTitle("Quantitation Table");
		setMessage("Please select a new database name.", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		//
		createDatabaseName(container);
		//
		return composite;
	}

	private void createDatabaseName(Composite container) {

		Label label = new Label(container, SWT.NONE);
		label.setText("Database Name");
		//
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		//
		textDatabaseName = new Text(container, SWT.BORDER);
		textDatabaseName.setLayoutData(gridData);
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	private void saveInput() {

		databaseName = textDatabaseName.getText();
	}

	@Override
	protected void okPressed() {

		saveInput();
		super.okPressed();
	}

	public String getDatabaseName() {

		return databaseName;
	}
}
