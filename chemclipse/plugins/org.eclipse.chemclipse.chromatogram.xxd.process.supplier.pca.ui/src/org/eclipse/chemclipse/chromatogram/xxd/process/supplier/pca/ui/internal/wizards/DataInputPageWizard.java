/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.InputFilesTable;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class DataInputPageWizard extends WizardPage {

	private InputFilesTable inputFilesTable;
	private Text textGroupName;

	public DataInputPageWizard(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	abstract protected void addFiles();

	@Override
	public void createControl(Composite parent) {

		GridLayout gridLayout;
		GridData gridData;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);
		/*
		 * Select the process entry.
		 */
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 30;
		Label label = new Label(composite, SWT.NONE);
		label.setText("Set group name (optional)");
		textGroupName = new Text(composite, SWT.BORDER);
		textGroupName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(composite, SWT.None);
		label.setText(" Select input files ");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		inputFilesTable = new InputFilesTable(composite, gridData);
		Composite compositeButtonTable = new Composite(composite, SWT.NONE);
		compositeButtonTable.setLayout(new FillLayout());
		Button button = new Button(compositeButtonTable, SWT.PUSH);
		button.addListener(SWT.Selection, (event) -> addFiles());
		button.setText("Add");
		button = new Button(compositeButtonTable, SWT.PUSH);
		button.addListener(SWT.Selection, (event) -> {
			inputFilesTable.removeSelection();
			setPageComplete(!inputFilesTable.getDataInputEntries().isEmpty());
		});
		button.setText("Remove");
		setControl(composite);
	}

	public List<IDataInputEntry> getDataInputEntries() {

		return inputFilesTable.getDataInputEntries();
	}

	public String getGroupName() {

		return textGroupName.getText();
	}

	protected void updata() {

		inputFilesTable.update();
		setPageComplete(!inputFilesTable.getDataInputEntries().isEmpty());
	}
}
