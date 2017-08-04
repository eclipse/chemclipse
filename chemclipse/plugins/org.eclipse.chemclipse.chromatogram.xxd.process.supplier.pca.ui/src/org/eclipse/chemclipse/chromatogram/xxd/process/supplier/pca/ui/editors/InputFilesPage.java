/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Daniel Mariano, Rafael Aguayo - additional functionality and UI improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.DataInputFromPeakFilesWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.InputFilesTable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class InputFilesPage {

	private static final String FILES = "Input Files: ";
	private Label countFiles;
	private InputFilesTable inputFilesTable;
	//
	private PcaEditor pcaEditor;

	public InputFilesPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder);
	}

	/**
	 * Creates the add button.
	 *
	 * @param client
	 * @param editorPart
	 */
	private void createAddButton(Composite client, GridData gridData) {

		Button add;
		add = new Button(client, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				DataInputFromPeakFilesWizard inputWizard = new DataInputFromPeakFilesWizard();
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), inputWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == Window.OK) {
					/*
					 * Get the list of selected chromatograms.
					 */
					if(inputWizard.getSelectedPeakFiles().size() > 0) {
						/*
						 * If it contains at least 1 element, add it to the input files list.
						 */
						inputFilesTable.getDataInputEntries().addAll(inputWizard.getSelectedPeakFiles());
						redrawCountFiles();
					}
				}
			}
		});
	}

	/**
	 * Create the action buttons.
	 *
	 * @param client
	 */
	private void createButtons(Composite client) {

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//
		createAddButton(client, gridData);
		createRemoveButton(client, gridData);
		createProcessButton(client, gridData);
	}

	private void createInputFilesSection(Composite parent) {

		Composite client = new Composite(parent, SWT.None);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		client.setLayout(layout);
		/*
		 * Creates the table and the action buttons.
		 */
		createTable(client);
		createButtons(client);
		createLabels(client);
		client.pack();
	}

	/**
	 * Creates the file count labels.
	 *
	 * @param client
	 */
	private void createLabels(Composite client) {

		countFiles = new Label(client, SWT.NONE);
		countFiles.setText(FILES + "0");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		countFiles.setLayoutData(gridData);
	}

	private void createProcessButton(Composite client, GridData gridData) {

		Button process;
		process = new Button(client, SWT.PUSH);
		process.setText("Run PCA");
		process.setLayoutData(gridData);
		process.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		process.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
			}
		});
	}

	/**
	 * Create the remove button.
	 *
	 * @param client
	 * @param editorPart
	 */
	private void createRemoveButton(Composite client, GridData gridData) {

		Button remove;
		remove = new Button(client, SWT.PUSH);
		remove.setText("Remove");
		remove.setLayoutData(gridData);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				inputFilesTable.removeSelection();
				redrawCountFiles();
				inputFilesTable.update();
			}
		});
	}

	private void createTable(Composite client) {

		GridData gridData;
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 400;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		this.inputFilesTable = new InputFilesTable(client, gridData);
	}

	private void initialize(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Input Files");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Create the section.
		 */
		createInputFilesSection(composite);
		//
		tabItem.setControl(composite);
	}

	private void redrawCountFiles() {

		countFiles.setText(FILES + Integer.toString(inputFilesTable.getDataInputEntries().size()));
	}

	public void update() {

		Optional<IPcaResults> pcaResults = pcaEditor.getPcaResults();
		if(pcaResults.isPresent()) {
			List<IDataInputEntry> inputs = pcaResults.get().getDataInputEntries();
			inputFilesTable.getDataInputEntries().clear();
			inputFilesTable.getDataInputEntries().addAll(inputs);
			inputFilesTable.update();
		}
	}
}
