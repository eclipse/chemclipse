/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.process.supplier.batchprocess.ui.wizards;

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PageBatchProcessFile extends AbstractExtendedWizardPage {

	private BatchProcessWizardElements wizardElements;

	public PageBatchProcessFile(BatchProcessWizardElements wizardElements) {

		super(PageBatchProcessFile.class.getName());
		setTitle("Mass Spectra Batch Process");
		setDescription("Select the export path.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		return wizardElements.getBatchProcessFile() != null;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createBatchProjectFileField(composite);

		setControl(composite);
	}

	private void createBatchProjectFileField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout(3, false));

		Label labelFile = new Label(parent, SWT.NONE);
		labelFile.setText("Save as:");

		Text fileText = new Text(parent, SWT.BORDER);
		fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button buttonBrowse = new Button(parent, SWT.PUSH);
		buttonBrowse.setText("Browse...");

		buttonBrowse.addListener(SWT.Selection, _ -> {
			String selected = openFileDialog(getShell());
			if(selected != null) {
				fileText.setText(selected);
				wizardElements.setBatchProcessFile(new File(selected));
			}
			validateSelection();
		});
	}

	private String openFileDialog(Shell shell) {

		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setOverwrite(true);
		fileDialog.setText(BatchProcessJob.DESCRIPTION);
		fileDialog.setFileName("BatchJob_" + new Date().getTime());
		fileDialog.setFilterExtensions(BatchProcessJob.FILTER_EXTENSION);
		fileDialog.setFilterNames(BatchProcessJob.FILTER_NAME);
		return fileDialog.open();
	}

	private void validateSelection() {

		String message = null;
		File file = wizardElements.getBatchProcessFile();
		if(file == null) {
			message = "Please select a file location.";
		}

		updateStatus(message);
	}
}
