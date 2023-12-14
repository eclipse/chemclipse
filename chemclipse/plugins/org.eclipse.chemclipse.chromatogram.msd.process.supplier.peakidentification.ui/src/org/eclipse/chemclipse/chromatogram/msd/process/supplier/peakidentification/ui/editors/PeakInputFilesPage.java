/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.wizards.PeakInputFilesWizard;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class PeakInputFilesPage implements IMultiEditorPage {

	private FormToolkit toolkit;
	private int pageIndex;
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;
	private Table inputFilesTable;
	private Label countFiles;
	private static final String FILES = "Input Files: ";

	public PeakInputFilesPage(BatchProcessEditor editorPart, Composite container) {

		createPage(editorPart, container);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public int getPageIndex() {

		return pageIndex;
	}

	@Override
	public void dispose() {

		if(toolkit != null) {
			toolkit.dispose();
		}
	}

	@Override
	public void setPeakIdentificationBatchJob(IPeakIdentificationBatchJob peakIdentificationBatchJob) {

		if(peakIdentificationBatchJob != null) {
			this.peakIdentificationBatchJob = peakIdentificationBatchJob;
			reloadTable();
		}
	}

	// ---------------------------------------private methods
	/**
	 * Creates the page.
	 * 
	 */
	private void createPage(BatchProcessEditor editorPart, Composite container) {

		/*
		 * Create the parent composite.
		 */
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(new FillLayout());
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Forms API
		 */
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm scrolledForm = toolkit.createScrolledForm(parent);
		Composite scrolledFormComposite = scrolledForm.getBody();
		scrolledFormComposite.setLayout(new TableWrapLayout());
		scrolledForm.setText("Input File Editor");
		/*
		 * Create the section.
		 */
		createInputFilesSection(scrolledFormComposite, editorPart);
		/*
		 * Get the page index.
		 */
		pageIndex = editorPart.addPage(parent);
	}

	private void createInputFilesSection(Composite parent, final BatchProcessEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		/*
		 * Section
		 */
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Input files");
		section.setDescription("Select the files to process. Use the add and remove buttons.");
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		/*
		 * Set the layout for the client.
		 */
		client = toolkit.createComposite(section, SWT.WRAP);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		/*
		 * Creates the table and the action buttons.
		 */
		createTable(client);
		createButtons(client, editorPart);
		createLabels(client);
		/*
		 * Add the client to the section and paint flat borders.
		 */
		section.setClient(client);
		toolkit.paintBordersFor(client);
	}

	/**
	 * Creates the table.
	 * 
	 * @param client
	 */
	private void createTable(Composite client) {

		GridData gridData;
		inputFilesTable = toolkit.createTable(client, SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 2;
		inputFilesTable.setLayoutData(gridData);
		inputFilesTable.setHeaderVisible(true);
		inputFilesTable.setLinesVisible(true);
	}

	/**
	 * Create the action buttons.
	 * 
	 * @param client
	 */
	private void createButtons(Composite client, final BatchProcessEditor editorPart) {

		createAddButton(client, editorPart);
		createRemoveButton(client, editorPart);
	}

	/**
	 * Creates the add button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createAddButton(Composite client, final BatchProcessEditor editorPart) {

		Button add;
		add = toolkit.createButton(client, "Add", SWT.PUSH);
		add.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				PeakInputFilesWizard inputWizard = new PeakInputFilesWizard();
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), inputWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == WizardDialog.OK) {
					/*
					 * Get the list of selected chromatograms.
					 */
					List<String> selectedPeakFiles = inputWizard.getSelectedPeakFiles();
					if(!selectedPeakFiles.isEmpty()) {
						/*
						 * If it contains at least 1 element, add it to the input files list.
						 */
						addEntries(selectedPeakFiles);
						reloadTable();
						editorPart.setDirty();
					}
				}
			}
		});
	}

	/**
	 * Create the remove button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createRemoveButton(Composite client, final BatchProcessEditor editorPart) {

		Button remove;
		remove = toolkit.createButton(client, "Remove", SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				removeEntries(inputFilesTable.getSelectionIndices());
				editorPart.setDirty();
			}
		});
	}

	/**
	 * Creates the file count labels.
	 * 
	 * @param client
	 */
	private void createLabels(Composite client) {

		countFiles = toolkit.createLabel(client, FILES + "0", SWT.NONE);
		countFiles.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	}

	/**
	 * Add the selected chromatograms to the input files list.
	 * 
	 * @param selectedChromatograms
	 */
	private void addEntries(List<String> selectedChromatograms) {

		List<IPeakInputEntry> inputEntries = peakIdentificationBatchJob.getPeakInputEntries();
		IPeakInputEntry inputEntry;
		for(String inputFile : selectedChromatograms) {
			inputEntry = new PeakInputEntry(inputFile);
			inputEntries.add(inputEntry);
		}
	}

	/**
	 * Remove the given entries.
	 * The table need not to be reloaded.
	 * 
	 * @param indices
	 */
	private void removeEntries(int[] indices) {

		if(indices == null || indices.length == 0) {
			return;
		}
		/*
		 * Remove the entries from the table.
		 */
		inputFilesTable.remove(indices);
		/*
		 * Remove the entries from the batchProcessJob instance.
		 */
		List<IPeakInputEntry> inputEntries = peakIdentificationBatchJob.getPeakInputEntries();
		int counter = 0;
		for(int index : indices) {
			/*
			 * Decrease the index and increase the counter to remove the correct entries.
			 */
			index -= counter;
			inputEntries.remove(index);
			counter++;
		}
		redrawCountFiles(inputEntries);
	}

	/**
	 * Reload the table.
	 */
	private void reloadTable() {

		if(peakIdentificationBatchJob != null && inputFilesTable != null) {
			/*
			 * Remove all entries.
			 */
			inputFilesTable.removeAll();
			/*
			 * Header
			 */
			String[] titles = {"Filename", "Path"};
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(inputFilesTable, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			List<IPeakInputEntry> inputEntries = peakIdentificationBatchJob.getPeakInputEntries();
			for(IPeakInputEntry entry : inputEntries) {
				TableItem item = new TableItem(inputFilesTable, SWT.NONE);
				item.setText(0, entry.getName());
				item.setText(1, entry.getInputFile());
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				inputFilesTable.getColumn(i).pack();
			}
			/*
			 * Set the count label information.
			 */
			redrawCountFiles(inputEntries);
		}
	}

	private void redrawCountFiles(List<IPeakInputEntry> inputEntries) {

		countFiles.setText(FILES + Integer.toString(inputEntries.size()));
	}
	// ---------------------------------------private methods
}
