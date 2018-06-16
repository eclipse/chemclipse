/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakOutputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.support.ConverterTypeSupport;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.internal.wizards.PeakOutputFilesWizard;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class PeakOutputFilesPage implements IMultiEditorPage {

	private FormToolkit toolkit;
	private int pageIndex;
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;
	private Table outputFilesTable;

	public PeakOutputFilesPage(BatchProcessEditor editorPart, Composite container) {
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
		scrolledForm.setText("Output File Editor");
		/*
		 * Create the section.
		 */
		createOutputFilesSection(scrolledFormComposite, editorPart);
		/*
		 * Get the page index.
		 */
		pageIndex = editorPart.addPage(parent);
	}

	private void createOutputFilesSection(Composite parent, final BatchProcessEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Output files");
		section.setDescription("Select the output file formats. Use the add and remove buttons.");
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
		outputFilesTable = toolkit.createTable(client, SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 2;
		outputFilesTable.setLayoutData(gridData);
		outputFilesTable.setHeaderVisible(true);
		outputFilesTable.setLinesVisible(true);
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
	 * Create the add button.
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
				Shell shell = Display.getCurrent().getActiveShell();
				PeakOutputFilesWizard outputEntriesWizard = new PeakOutputFilesWizard();
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, outputEntriesWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == WizardDialog.OK) {
					IPeakOutputEntry outputEntry = outputEntriesWizard.getPeakOutputEntry();
					if(outputEntry != null) {
						List<IPeakOutputEntry> outputEntries = peakIdentificationBatchJob.getPeakOutputEntries();
						outputEntries.add(outputEntry);
						reloadTable();
						editorPart.setDirty();
					} else {
						MessageBox messageBox = new MessageBox(shell);
						messageBox.setText("Error Add Output Entry");
						messageBox.setMessage("Please select a valid peak output converter and folder.");
						messageBox.open();
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
				removeEntries(outputFilesTable.getSelectionIndices());
				editorPart.setDirty();
			}
		});
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
		outputFilesTable.remove(indices);
		/*
		 * Remove the entries from the batchProcessJob instance.
		 */
		List<IPeakOutputEntry> outputEntries = peakIdentificationBatchJob.getPeakOutputEntries();
		int counter = 0;
		for(int index : indices) {
			/*
			 * Decrease the index and increase the counter to remove the correct entries.
			 */
			index -= counter;
			outputEntries.remove(index);
			counter++;
		}
	}

	/**
	 * Reload the table.
	 */
	private void reloadTable() {

		if(peakIdentificationBatchJob != null && outputFilesTable != null) {
			/*
			 * Remove all entries.
			 */
			outputFilesTable.removeAll();
			/*
			 * Header
			 */
			String[] titles = {"Converter Name", "Output Folder", "Converter Id"};
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(outputFilesTable, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			ConverterTypeSupport converterTypeSupport = new ConverterTypeSupport();
			List<IPeakOutputEntry> outputEntries = peakIdentificationBatchJob.getPeakOutputEntries();
			for(IPeakOutputEntry entry : outputEntries) {
				TableItem item = new TableItem(outputFilesTable, SWT.NONE);
				item.setText(0, converterTypeSupport.getConverterName(entry));
				item.setText(1, entry.getOutputFolder());
				item.setText(2, entry.getConverterId());
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				outputFilesTable.getColumn(i).pack();
			}
		}
	}
	// ---------------------------------------private methods
}