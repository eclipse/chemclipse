/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.editors;

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
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.wizards.ChromatogramProcessEntriesWizard;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ProcessEntriesPage implements IMultiEditorPage {

	private FormToolkit toolkit;
	private int pageIndex;
	private IBatchProcessJob batchProcessJob;
	private Table processEntriesTable;

	public ProcessEntriesPage(BatchProcessJobEditor editorPart, Composite container) {
		createPage(editorPart, container);
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
	public void setBatchProcessJob(IBatchProcessJob batchProcessJob) {

		if(batchProcessJob != null) {
			this.batchProcessJob = batchProcessJob;
			reloadTable();
		}
	}

	// ---------------------------------------private methods
	/**
	 * Creates the page.
	 * 
	 */
	private void createPage(BatchProcessJobEditor editorPart, Composite container) {

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
		scrolledForm.setText("Process Entry Editor");
		/*
		 * Create the section.
		 */
		createProcessEntriesSection(scrolledFormComposite, editorPart);
		/*
		 * Get the page index.
		 */
		pageIndex = editorPart.addPage(parent);
	}

	private void createProcessEntriesSection(Composite parent, final BatchProcessJobEditor editorPart) {

		Section section;
		Composite client;
		GridLayout layout;
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText("Process entries");
		section.setDescription("Select the process entries. Use the add, remove, move up and down buttons.");
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
		processEntriesTable = toolkit.createTable(client, SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		gridData.verticalSpan = 5;
		processEntriesTable.setLayoutData(gridData);
		processEntriesTable.setHeaderVisible(true);
		processEntriesTable.setLinesVisible(true);
	}

	/**
	 * Create the action buttons.
	 * 
	 * @param client
	 */
	private void createButtons(Composite client, final BatchProcessJobEditor editorPart) {

		createAddButton(client, editorPart);
		createRemoveButton(client, editorPart);
		createEditButton(client, editorPart);
		createMoveUpButton(client, editorPart);
		createMoveDownButton(client, editorPart);
	}

	/**
	 * Create the add button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createAddButton(Composite client, final BatchProcessJobEditor editorPart) {

		Button add;
		add = toolkit.createButton(client, "Add", SWT.PUSH);
		add.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				Shell shell = Display.getCurrent().getActiveShell();
				ChromatogramProcessEntriesWizard processEntriesWizard = new ChromatogramProcessEntriesWizard(null);
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, processEntriesWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == WizardDialog.OK) {
					IChromatogramProcessEntry processEntry = processEntriesWizard.getChromatogramProcessEntry();
					if(processEntry != null) {
						List<IChromatogramProcessEntry> processEntries = batchProcessJob.getChromatogramProcessEntries();
						processEntries.add(processEntry);
						reloadTable();
						editorPart.setDirty();
					} else {
						MessageBox messageBox = new MessageBox(shell);
						messageBox.setText("Error Add Process Entry");
						messageBox.setMessage("Please select a valid processing type and a valid plug-in.");
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
	private void createRemoveButton(Composite client, final BatchProcessJobEditor editorPart) {

		Button remove;
		remove = toolkit.createButton(client, "Remove", SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				removeEntries(processEntriesTable.getSelectionIndices());
				editorPart.setDirty();
			}
		});
	}

	/**
	 * Create the edit button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createEditButton(Composite client, final BatchProcessJobEditor editorPart) {

		Button edit;
		edit = toolkit.createButton(client, "Edit", SWT.PUSH);
		edit.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		edit.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				Shell shell = Display.getCurrent().getActiveShell();
				List<IChromatogramProcessEntry> processEntries = batchProcessJob.getChromatogramProcessEntries();
				IChromatogramProcessEntry entry = processEntries.get(processEntriesTable.getSelectionIndex());
				ChromatogramProcessEntriesWizard processEntriesWizard = new ChromatogramProcessEntriesWizard(entry);
				BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, processEntriesWizard);
				wizardDialog.create();
				int returnCode = wizardDialog.open();
				/*
				 * If OK
				 */
				if(returnCode == WizardDialog.OK) {
					IChromatogramProcessEntry processEntry = processEntriesWizard.getChromatogramProcessEntry();
					if(processEntry != null) {
						reloadTable();
						editorPart.setDirty();
					}
				} else {
					MessageBox messageBox = new MessageBox(shell);
					messageBox.setText("Error Add Process Entry");
					messageBox.setMessage("Please select a valid processing type and a valid plug-in.");
					messageBox.open();
				}
			}
		});
	}

	/**
	 * Create the move up button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createMoveUpButton(Composite client, final BatchProcessJobEditor editorPart) {

		Button up;
		up = toolkit.createButton(client, "Move Up", SWT.PUSH);
		up.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		up.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				moveUp(processEntriesTable.getSelectionIndex());
				editorPart.setDirty();
			}
		});
	}

	/**
	 * Create the move down button.
	 * 
	 * @param client
	 * @param editorPart
	 */
	private void createMoveDownButton(Composite client, final BatchProcessJobEditor editorPart) {

		Button down;
		down = toolkit.createButton(client, "Move Down", SWT.PUSH);
		down.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		down.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				super.widgetSelected(e);
				moveDown(processEntriesTable.getSelectionIndex());
				editorPart.setDirty();
			}
		});
	}

	/**
	 * Moves the selected element up.
	 * 
	 * @param index
	 */
	private void moveUp(int index) {

		/*
		 * Moves up the entry in the batchProcessJob instance.
		 */
		List<IChromatogramProcessEntry> processEntries = batchProcessJob.getChromatogramProcessEntries();
		IChromatogramProcessEntry entry1;
		IChromatogramProcessEntry entry2;
		int indexUp;
		/*
		 * Toggle the elements.
		 */
		if(index > 0) {
			indexUp = index - 1;
			entry1 = processEntries.get(indexUp);
			entry2 = processEntries.get(index);
			processEntries.set(indexUp, entry2);
			processEntries.set(index, entry1);
		}
		reloadTable();
	}

	/**
	 * Moves the selected element down.
	 * 
	 * @param index
	 */
	private void moveDown(int index) {

		/*
		 * Moves up the entry in the batchProcessJob instance.
		 */
		List<IChromatogramProcessEntry> processEntries = batchProcessJob.getChromatogramProcessEntries();
		IChromatogramProcessEntry entry1;
		IChromatogramProcessEntry entry2;
		int indexDown;
		/*
		 * Toggle the elements.
		 */
		if(index < processEntries.size() - 1) {
			indexDown = index + 1;
			entry1 = processEntries.get(index);
			entry2 = processEntries.get(indexDown);
			processEntries.set(indexDown, entry1);
			processEntries.set(index, entry2);
		}
		reloadTable();
	}

	/**
	 * Removes the entries from the table.
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
		processEntriesTable.remove(indices);
		/*
		 * Remove the entries from the batchProcessJob instance.
		 */
		List<IChromatogramProcessEntry> processEntries = batchProcessJob.getChromatogramProcessEntries();
		int counter = 0;
		for(int index : indices) {
			/*
			 * Decrease the index and increase the counter to remove the correct entries.
			 */
			index -= counter;
			processEntries.remove(index);
			counter++;
		}
	}

	/**
	 * Reload the table.
	 */
	private void reloadTable() {

		if(batchProcessJob != null && processEntriesTable != null) {
			/*
			 * Remove all entries.
			 */
			processEntriesTable.removeAll();
			/*
			 * Header
			 */
			String[] titles = {"Name", "Type", "Processor Id"};
			for(int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(processEntriesTable, SWT.NONE);
				column.setText(titles[i]);
			}
			/*
			 * Data
			 */
			ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
			List<IChromatogramProcessEntry> processEntries = batchProcessJob.getChromatogramProcessEntries();
			for(IChromatogramProcessEntry entry : processEntries) {
				TableItem item = new TableItem(processEntriesTable, SWT.NONE);
				/*
				 * Get the processor name.
				 * And set the entries.
				 */
				item.setText(0, processTypeSupport.getProcessorName(entry));
				item.setText(1, entry.getProcessCategory());
				item.setText(2, entry.getProcessorId());
			}
			/*
			 * Pack to make the entries visible.
			 */
			for(int i = 0; i < titles.length; i++) {
				processEntriesTable.getColumn(i).pack();
			}
		}
	}
	// ---------------------------------------private methods
}