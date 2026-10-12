/*******************************************************************************
 * Copyright (c) 2020, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.ui.wizards;

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PageDataType extends AbstractExtendedWizardPage {

	private DataType dataType = BatchProcessJob.DATA_TYPE_DEFAULT;
	private BatchProcessWizardElements wizardElements;

	public PageDataType(BatchProcessWizardElements wizardElements) {

		super(PageDataType.class.getName());
		setTitle("Chromatogram Batch Process");
		setDescription("Select the data type and export path.");
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
	public void setVisible(boolean visible) {

		super.setVisible(visible);
	}

	public DataType getDataType() {

		return dataType;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createComboViewerDataTypes(composite);
		createBatchProjectFileField(composite);

		setControl(composite);
	}

	private ComboViewer createComboViewerDataTypes(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof DataType dataType) {
					return dataType.label();
				}

				return null;
			}
		});

		combo.setToolTipText("Select the data type, that shall be batch processed.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof DataType selectedDataType) {
					dataType = selectedDataType;
				} else {
					dataType = BatchProcessJob.DATA_TYPE_DEFAULT;
				}
			}
		});

		comboViewer.setInput(BatchProcessJob.DATA_TYPES);
		comboViewer.setSelection(new StructuredSelection(BatchProcessJob.DATA_TYPE_DEFAULT));

		return comboViewer;
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