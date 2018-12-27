/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller.ConcentrationResponseEntryEdit;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.ConcentrationResponseContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.ConcentrationResponseLabelProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.ConcentrationResponseTableComparator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables.dialogs.ConcentrationResponseEntryEditDialog;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntries;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ConcentrationResponseEntriesUI extends AbstractTableViewerUI implements IQuantitationCompoundUpdater {

	private static final String MESSAGE_BOX_TEXT = "Concentration Response Entry";
	private IQuantitationCompoundMSD quantitationCompoundMSD;
	private IEventBroker eventBroker;
	private IQuantDatabase database;
	private Map<String, Object> map;

	public ConcentrationResponseEntriesUI(Composite parent, int style, IEventBroker eventBroker) {
		parent.setLayout(new FillLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		addList(composite);
		addButtons(composite);
		//
		this.eventBroker = eventBroker;
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IQuantitationCompoundMSD quantitationCompoundMSD, IQuantDatabase database) {

		this.database = database;
		this.quantitationCompoundMSD = quantitationCompoundMSD;
		//
		if(quantitationCompoundMSD == null || database == null) {
			getTableViewer().setInput(null);
		} else {
			setTableViewerInput();
		}
	}

	private void setTableViewerInput() {

		getTableViewer().setInput(quantitationCompoundMSD);
	}

	private void addList(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(gridDataTable);
		//
		String[] titles = {"Ion (0 = TIC)", "Concentration", "Response"};
		int bounds[] = {100, 100, 100};
		IStructuredContentProvider contentProvider = new ConcentrationResponseContentProvider();
		ILabelProvider labelProvider = new ConcentrationResponseLabelProvider();
		ConcentrationResponseTableComparator viewerTableComparator = new ConcentrationResponseTableComparator();
		//
		createTableViewer(composite, gridDataTable, contentProvider, labelProvider, viewerTableComparator, titles, bounds);
		setTableViewerInput();
	}

	private void addButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		GridData gridDataButtons = new GridData(GridData.FILL_VERTICAL);
		gridDataButtons.verticalAlignment = SWT.TOP;
		composite.setLayoutData(gridDataButtons);
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonAdd(composite, gridData);
		addButtonEdit(composite, gridData);
		addButtonRemove(composite, gridData);
		addButtonRemoveAll(composite, gridData);
	}

	/**
	 * Add a new quantitation response entry.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonAdd(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Add");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * The selected compound must be not null.
				 */
				if(quantitationCompoundMSD != null) {
					/*
					 * Try to add a new response entry.
					 */
					Shell shell = Display.getCurrent().getActiveShell();
					String concentrationUnit = quantitationCompoundMSD.getConcentrationUnit();
					ConcentrationResponseEntryEdit concentrationResponseEntryEdit = new ConcentrationResponseEntryEdit(concentrationUnit);
					ConcentrationResponseEntryEditDialog dialog = new ConcentrationResponseEntryEditDialog(shell, concentrationResponseEntryEdit, "Create a new concentration response entry.");
					if(dialog.open() == IDialogConstants.OK_ID) {
						/*
						 * Save the response entry.
						 */
						IConcentrationResponseEntry concentrationResponseEntryMSD = concentrationResponseEntryEdit.getConcentrationResponseEntryMSD();
						if(concentrationResponseEntryMSD != null) {
							quantitationCompoundMSD.getConcentrationResponseEntriesMSD().add(concentrationResponseEntryMSD);
							setTableViewerInput();
							triggerCompoundDocumentUpdateEvent();
						}
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation compound previously.");
				}
			}
		});
	}

	/**
	 * Edit the quantitation compound entry.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonEdit(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Edit");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * The selected compound must be not null.
				 */
				if(quantitationCompoundMSD != null) {
					/*
					 * Try to edit a new response entry.
					 */
					IConcentrationResponseEntry concentrationResponseEntryOld = getSelectedConcentrationResponseEntryMSD();
					if(concentrationResponseEntryOld != null) {
						/*
						 * Edit
						 */
						Shell shell = Display.getCurrent().getActiveShell();
						String concentrationUnit = quantitationCompoundMSD.getConcentrationUnit();
						ConcentrationResponseEntryEdit concentrationResponseEntryEdit = new ConcentrationResponseEntryEdit(concentrationUnit);
						concentrationResponseEntryEdit.setConcentrationResponseEntryMSD(concentrationResponseEntryOld);
						ConcentrationResponseEntryEditDialog dialog = new ConcentrationResponseEntryEditDialog(shell, concentrationResponseEntryEdit, "Edit the concentration response entry.");
						if(dialog.open() == IDialogConstants.OK_ID) {
							/*
							 * Save the edited response entry.
							 */
							IConcentrationResponseEntry concentrationResponseEntryNew = concentrationResponseEntryEdit.getConcentrationResponseEntryMSD();
							if(concentrationResponseEntryOld != null) {
								IConcentrationResponseEntries concentrationResponseEntriesMSD = quantitationCompoundMSD.getConcentrationResponseEntriesMSD();
								concentrationResponseEntriesMSD.remove(concentrationResponseEntryOld);
								concentrationResponseEntriesMSD.add(concentrationResponseEntryNew);
								quantitationCompoundMSD.updateConcentrationResponseEntries(concentrationResponseEntriesMSD);
								setTableViewerInput();
								triggerCompoundDocumentUpdateEvent();
							}
						}
					} else {
						showMessage(MESSAGE_BOX_TEXT, "Please select a concentration response entry.");
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation compound previously.");
				}
			}
		});
	}

	/**
	 * Remove the quantitation compound entry.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonRemove(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Delete");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IConcentrationResponseEntry> concentrationResponseEntriesMSD = getSelectedConcentrationResponseEntriesMSD();
				if(concentrationResponseEntriesMSD.size() > 0) {
					/*
					 * Remove the selected entries
					 */
					int decision = showQuestion(MESSAGE_BOX_TEXT, "Would you like to delete the selected concentration response entries?");
					if(decision == SWT.YES) {
						quantitationCompoundMSD.getConcentrationResponseEntriesMSD().removeAll(concentrationResponseEntriesMSD);
						setTableViewerInput();
						triggerCompoundDocumentUpdateEvent();
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select concentration response entries to delete.");
				}
			}
		});
	}

	/**
	 * Remove all quantitation compound entries.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonRemoveAll(Composite parent, GridData gridData) {

		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(gridData);
		button.setText("Delete All");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Remove all
				 */
				int decision = showQuestion(MESSAGE_BOX_TEXT, "Would you like to delete all response entry?");
				if(decision == SWT.YES) {
					quantitationCompoundMSD.getConcentrationResponseEntriesMSD().clear();
					setTableViewerInput();
					triggerCompoundDocumentUpdateEvent();
				}
			}
		});
	}

	private void triggerCompoundDocumentUpdateEvent() {

		if(eventBroker != null && quantitationCompoundMSD != null && database != null) {
			/*
			 * Inform all other views about the selected quantitation compound.
			 * The element could be null.
			 */
			map.clear();
			map.put(IChemClipseQuantitationEvents.PROPERTY_QUANTITATION_COMPOUND_DOCUMENT, quantitationCompoundMSD);
			map.put(IChemClipseQuantitationEvents.PROPERTY_DATABASE, database);
			eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_COMPOUND_DOCUMENT_UPDATE, map);
		}
	}

	/**
	 * Returns the selected item or null if none is available.
	 * 
	 * @return
	 */
	private IConcentrationResponseEntry getSelectedConcentrationResponseEntryMSD() {

		IConcentrationResponseEntry concentrationResponseEntryMSD = null;
		Object element = getSelectedTableItem();
		if(element instanceof IConcentrationResponseEntry) {
			concentrationResponseEntryMSD = (IConcentrationResponseEntry)element;
		}
		return concentrationResponseEntryMSD;
	}

	/**
	 * Returns the selected items or an empty list none are available.
	 * 
	 * @return
	 */
	private List<IConcentrationResponseEntry> getSelectedConcentrationResponseEntriesMSD() {

		List<IConcentrationResponseEntry> concentrationResponseEntriesMSD = new ArrayList<IConcentrationResponseEntry>();
		List<Object> elements = getSelectedTableItems();
		for(Object element : elements) {
			if(element instanceof IConcentrationResponseEntry) {
				IConcentrationResponseEntry concentrationResponseEntryMSD = (IConcentrationResponseEntry)element;
				concentrationResponseEntriesMSD.add(concentrationResponseEntryMSD);
			}
		}
		return concentrationResponseEntriesMSD;
	}
}
