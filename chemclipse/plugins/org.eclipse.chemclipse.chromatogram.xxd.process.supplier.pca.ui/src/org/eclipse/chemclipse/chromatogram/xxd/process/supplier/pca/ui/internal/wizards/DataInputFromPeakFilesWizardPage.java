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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.DataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.InputFilesTable;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DataInputFromPeakFilesWizardPage extends WizardPage {

	private InputFilesTable inputFilesTable;
	private Text textGroupName;

	public DataInputFromPeakFilesWizardPage(String pageName) {
		super(pageName);
		setTitle("Peak Input Files");
		setDescription("This wizard lets you select peak input files and set bulk group name.");
	}

	private void addFiles() {

		PeakInputFilesWizard inputWizard = new PeakInputFilesWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), inputWizard);
		wizardDialog.create();
		int returnCode = wizardDialog.open();
		if(returnCode == Window.OK) {
			List<String> selectedPeakFiles = inputWizard.getSelectedPeakFiles();
			for(String selectedPeakFile : selectedPeakFiles) {
				IDataInputEntry dataInputEntry = new DataInputEntry(selectedPeakFile);
				String groupName = textGroupName.getText().trim();
				if(!groupName.isEmpty()) {
					dataInputEntry.setGroupName(groupName);
				}
				inputFilesTable.getDataInputEntries().add(dataInputEntry);
			}
			inputFilesTable.reload();
		}
	}

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
		Button button = new Button(composite, SWT.PUSH);
		button.addListener(SWT.Selection, (event) -> addFiles());
		button.setText(" Select input files ");
		inputFilesTable = new InputFilesTable(composite, null);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		inputFilesTable.getTable().setLayoutData(gridData);
		setControl(composite);
	}

	public List<IDataInputEntry> getDataInputEntries() {

		return inputFilesTable.getDataInputEntries();
	}
}
