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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class InputFilesPage {

	private static final String FILES = "Input Files: ";
	private Label countFiles;
	private InputFilesTable inputFilesTable;
	//
	private PcaEditor pcaEditor;

	public InputFilesPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	/**
	 * Creates the add button.
	 *
	 * @param client
	 * @param editorPart
	 */
	private void createAddButton(Composite client, GridData gridData, FormToolkit formToolkit) {

		Button add;
		add = formToolkit.createButton(client, "Add", SWT.PUSH);
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
						inputFilesTable.reload();
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
	private void createButtons(Composite client, FormToolkit formToolkit) {

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		//
		createAddButton(client, gridData, formToolkit);
		createRemoveButton(client, gridData, formToolkit);
		createProcessButton(client, gridData, formToolkit);
	}

	private void createInputFilesSection(Composite parent, FormToolkit formToolkit) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		section = formToolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText("Input files");
		section.setDescription("Select the files to process. Use the add and remove buttons as needed. To edit group name click on cell. Click Run PCA to process the files. ");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Set the layout for the client.
		 */
		client = formToolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		Label label;
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 20;
		gridData.heightHint = 30;
		gridData.horizontalSpan = 2;
		/*
		 * Label II
		 */
		label = formToolkit.createLabel(client, "");
		label.setLayoutData(gridData);
		/*
		 * Creates the table and the action buttons.
		 */
		createTable(client);
		createButtons(client, formToolkit);
		createLabels(client, formToolkit);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		formToolkit.paintBordersFor(client);
	}

	/**
	 * Creates the file count labels.
	 *
	 * @param client
	 */
	private void createLabels(Composite client, FormToolkit formToolkit) {

		countFiles = formToolkit.createLabel(client, FILES + "0", SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		countFiles.setLayoutData(gridData);
	}

	private void createProcessButton(Composite client, GridData gridData, FormToolkit formToolkit) {

		Button process;
		process = formToolkit.createButton(client, "Run PCA", SWT.PUSH);
		process.setLayoutData(gridData);
		process.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		process.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				pcaEditor.runPcaCalculation();
			}
		});
	}

	/**
	 * Create the remove button.
	 *
	 * @param client
	 * @param editorPart
	 */
	private void createRemoveButton(Composite client, GridData gridData, FormToolkit formToolkit) {

		Button remove;
		remove = formToolkit.createButton(client, "Remove", SWT.PUSH);
		remove.setLayoutData(gridData);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				inputFilesTable.removeSelection();
				redrawCountFiles();
			}
		});
	}

	private void createTable(Composite client) {

		this.inputFilesTable = new InputFilesTable(client);
	}

	public List<IDataInputEntry> getDataInputEntries() {

		return inputFilesTable.getDataInputEntries();
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Data Input Files");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Forms API
		 */
		formToolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm scrolledForm = formToolkit.createScrolledForm(composite);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Input File Editor");
		/*
		 * Create the section.
		 */
		createInputFilesSection(scrolledFormComposite, formToolkit);
		//
		tabItem.setControl(composite);
	}

	private void redrawCountFiles() {

		countFiles.setText(FILES + Integer.toString(inputFilesTable.getDataInputEntries().size()));
	}
}
