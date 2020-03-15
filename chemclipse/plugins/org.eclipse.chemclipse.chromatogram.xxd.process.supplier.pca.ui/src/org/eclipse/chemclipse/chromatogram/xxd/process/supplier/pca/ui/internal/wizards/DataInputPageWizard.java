/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public abstract class DataInputPageWizard extends WizardPage {

	private static final String FILES = "Input Files: ";
	private Label countFiles;
	private List<IDataInputEntry> dataInputEntries;
	private InputFilesTable inputFilesTable;

	public DataInputPageWizard(String pageName) {
		super(pageName);
		dataInputEntries = new ArrayList<>();
		setPageComplete(false);
	}

	abstract protected void addFiles();

	public void addInputFiles(List<IDataInputEntry> addInput) {

		Map<String, IDataInputEntry> uniqueInputs = new HashMap<>();
		dataInputEntries.forEach(e -> uniqueInputs.put(e.getName(), e));
		addInput.forEach(e -> uniqueInputs.put(e.getName(), e));
		dataInputEntries.clear();
		dataInputEntries.addAll(uniqueInputs.values());
		update();
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
		//
		Label label = new Label(composite, SWT.None);
		label.setText(" Select input files ");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		inputFilesTable = new InputFilesTable(composite, gridData);
		inputFilesTable.setDataInputEntries(dataInputEntries);
		countFiles = new Label(composite, SWT.NONE);
		countFiles.setText(FILES + "0");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		countFiles.setLayoutData(gridData);
		Composite compositeButtonTable = new Composite(composite, SWT.NONE);
		compositeButtonTable.setLayout(new FillLayout());
		Button button = new Button(compositeButtonTable, SWT.PUSH);
		button.addListener(SWT.Selection, (event) -> addFiles());
		button.setText("Add");
		button = new Button(compositeButtonTable, SWT.PUSH);
		button.addListener(SWT.Selection, (event) -> {
			inputFilesTable.removeSelection();
			update();
		});
		button.setText("Remove");
		setControl(composite);
	}

	public List<IDataInputEntry> getUniqueDataInputEnties() {

		return dataInputEntries;
	}

	private void redrawCountFiles() {

		countFiles.setText(FILES + Integer.toString(dataInputEntries.size()));
	}

	@Override
	public void setVisible(boolean visible) {

		if(visible) {
			update();
		}
		super.setVisible(visible);
	}

	protected void update() {

		inputFilesTable.update();
		redrawCountFiles();
		setPageComplete(!dataInputEntries.isEmpty());
	}
}
