/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabaseProxy;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller.QuantitationCompoundEntryEdit;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationCompoundContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationCompoundLabelProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.QuantitationCompoundTableComparator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables.dialogs.QuantitationCompoundEditDialog;
import org.eclipse.chemclipse.database.documents.ILibraryDescriptionDocument;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class QuantitationCompoundsUI extends AbstractTableViewerUI {

	private static final Logger logger = Logger.getLogger(QuantitationCompoundsUI.class);
	private static final String MESSAGE_BOX_TEXT = "Quantitation Compounds";
	private Label label;
	private IEventBroker eventBroker;
	private IQuantDatabase database;
	private Map<String, Object> map;

	public QuantitationCompoundsUI(Composite parent, int style, IEventBroker eventBroker) {
		parent.setLayout(new FillLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		addHeader(composite);
		addList(composite);
		addButtons(composite);
		//
		this.eventBroker = eventBroker;
		map = new HashMap<String, Object>();
		subscribeToDatabaseUpdates();
		// setDatabaseInput();
	}

	@Override
	public void setFocus() {

		super.setFocus();
		setTableViewerInput();
	}

	private void addHeader(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		GridData gridDataHeader = new GridData(GridData.FILL_HORIZONTAL);
		gridDataHeader.horizontalSpan = 2;
		gridDataHeader.grabExcessHorizontalSpace = true;
		gridDataHeader.heightHint = 30;
		composite.setLayoutData(gridDataHeader);
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridDataHeader.grabExcessHorizontalSpace = true;
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridData);
		label.setText("Please select a quantitation table.");
	}

	/**
	 * Shows the quantitation compounds.
	 * 
	 * @param parent
	 */
	private void addList(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(gridDataTable);
		//
		String[] titles = {"Name", "Chemical Class", "Concentration Unit", "Calibration Method", "Cross Zero", "Use TIC", "Retention Time (RT)", "RT (-)", "RT (+)", "Retention Index (RI)", "RI (-)", "RI (+)"};
		int bounds[] = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
		IStructuredContentProvider contentProvider = new QuantitationCompoundContentProvider();
		LabelProvider labelProvider = new QuantitationCompoundLabelProvider();
		QuantitationCompoundTableComparator viewerTableComparator = new QuantitationCompoundTableComparator();
		//
		createTableViewer(composite, gridDataTable, contentProvider, labelProvider, viewerTableComparator, titles, bounds);
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				triggerCompoundDocumentUpdateEvent();
			}
		});
		setTableViewerInput();
	}

	private void setTableViewerInput() {

		if(database != null) {
			List<IQuantitationCompoundDocument> documents = database.getQuantitationCompoundDocuments();
			if(documents.size() > 0) {
				/*
				 * Display the elements.
				 */
				getTableViewer().setInput(documents);
				triggerCompoundDocumentUpdateEvent();
			} else {
				/*
				 * Clear all elements.
				 */
				getTableViewer().setInput(null);
			}
		}
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
	 * Add a new quantitation compound.
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

				if(database != null) {
					/*
					 * Try to get a new entry.
					 */
					Shell shell = Display.getCurrent().getActiveShell();
					QuantitationCompoundEntryEdit quantitationCompoundEntryEdit = new QuantitationCompoundEntryEdit();
					QuantitationCompoundEditDialog dialog = new QuantitationCompoundEditDialog(shell, quantitationCompoundEntryEdit, "Create a new quantitation compound.", database, true);
					if(dialog.open() == IDialogConstants.OK_ID) {
						try {
							IQuantitationCompoundMSD quantitationCompoundMSD = quantitationCompoundEntryEdit.getQuantitationCompoundMSD();
							database.createQuantitationCompoundDocument(quantitationCompoundMSD);
							setTableViewerInput();
						} catch(QuantitationCompoundAlreadyExistsException e1) {
							logger.warn(e1);
							showMessage(MESSAGE_BOX_TEXT, "The quantitation compound already exists in the database.");
						}
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "No valid database has been selected.");
				}
			}
		});
	}

	/**
	 * Edit a quantitation compound.
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

				IQuantitationCompoundDocument quantitationCompoundDocument = getSelectedQuantitationCompoundDocument();
				if(quantitationCompoundDocument != null) {
					if(database != null) {
						/*
						 * Catch if the database is not available.
						 */
						Shell shell = Display.getCurrent().getActiveShell();
						QuantitationCompoundEntryEdit quantitationCompoundEntryEdit = new QuantitationCompoundEntryEdit();
						IQuantitationCompoundMSD quantitationCompoundOld = quantitationCompoundDocument.getQuantitationCompound();
						quantitationCompoundEntryEdit.setQuantitationCompoundMSD(quantitationCompoundOld);
						QuantitationCompoundEditDialog dialog = new QuantitationCompoundEditDialog(shell, quantitationCompoundEntryEdit, "Edit the quantitation compound.", database, false);
						/*
						 * Yes, edit the document.
						 */
						if(dialog.open() == IDialogConstants.OK_ID) {
							/*
							 * Get the edited compound and set the quantitation signals
							 * and concentration response entries.
							 */
							IQuantitationCompoundMSD quantitationCompoundNew = quantitationCompoundEntryEdit.getQuantitationCompoundMSD();
							quantitationCompoundNew.updateQuantitationSignals(quantitationCompoundOld.getQuantitationSignals());
							quantitationCompoundNew.updateConcentrationResponseEntries(quantitationCompoundOld.getConcentrationResponseEntries());
							/*
							 * Update the quantitation compound document
							 */
							quantitationCompoundDocument.updateQuantitationCompound(quantitationCompoundNew);
							setTableViewerInput();
						}
					} else {
						showMessage(MESSAGE_BOX_TEXT, "No valid database has been selected.");
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation compound to edit.");
				}
			}
		});
	}

	/**
	 * Remove a quantitation compound.
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

				List<IQuantitationCompoundDocument> quantitationCompoundDocuments = getSelectedQuantitationCompoundDocuments();
				if(quantitationCompoundDocuments.size() > 0) {
					/*
					 * Try to delete the selected documents.
					 */
					if(database != null) {
						database.deleteQuantitationCompoundDocuments(quantitationCompoundDocuments);
						setTableViewerInput();
					} else {
						showMessage(MESSAGE_BOX_TEXT, "No valid database has been selected.");
					}
				} else {
					showMessage(MESSAGE_BOX_TEXT, "Please select a quantitation document.");
				}
			}
		});
	}

	/**
	 * Remove a quantitation compound.
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

				if(showQuestion(MESSAGE_BOX_TEXT, "Would you like to delete all quantitation compounds?") == SWT.YES) {
					if(database != null) {
						database.deleteAllQuantitationCompoundDocuments();
						setTableViewerInput();
					} else {
						showMessage(MESSAGE_BOX_TEXT, "No valid database has been selected.");
					}
				}
			}
		});
	}

	/**
	 * Returns the selected item or null if none is available.
	 * 
	 * @return
	 */
	private IQuantitationCompoundDocument getSelectedQuantitationCompoundDocument() {

		IQuantitationCompoundDocument quantitationCompoundDocument = null;
		Object element = getSelectedTableItem();
		if(element instanceof IQuantitationCompoundDocument) {
			quantitationCompoundDocument = (IQuantitationCompoundDocument)element;
		}
		return quantitationCompoundDocument;
	}

	private List<IQuantitationCompoundDocument> getSelectedQuantitationCompoundDocuments() {

		List<IQuantitationCompoundDocument> quantitationCompoundDocuments = new ArrayList<IQuantitationCompoundDocument>();
		List<Object> elements = getSelectedTableItems();
		for(Object element : elements) {
			if(element instanceof IQuantitationCompoundDocument) {
				IQuantitationCompoundDocument quantitationCompoundDocument = (IQuantitationCompoundDocument)element;
				quantitationCompoundDocuments.add(quantitationCompoundDocument);
			}
		}
		return quantitationCompoundDocuments;
	}

	private void triggerCompoundDocumentUpdateEvent() {

		Object element = getSelectedTableItem();
		if(eventBroker != null && database != null) {
			/*
			 * Inform all other views about the selected quantitation compound.
			 * The element could be null.
			 */
			map.clear();
			map.put(IChemClipseQuantitationEvents.PROPERTY_QUANTITATION_COMPOUND_DOCUMENT, element);
			map.put(IChemClipseQuantitationEvents.PROPERTY_DATABASE, database);
			eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_COMPOUND_DOCUMENT_UPDATE, map);
		}
	}

	private void subscribeToDatabaseUpdates() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram overview updates.
			 */
			EventHandler eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					Object quantDatabaseProxy = event.getProperty(IChemClipseQuantitationEvents.PROPERTY_QUANTITATION_TABLE_NAME);
					if(quantDatabaseProxy instanceof IQuantDatabaseProxy) {
						/*
						 * Update the database.
						 */
						setDatabaseInput((IQuantDatabaseProxy)quantDatabaseProxy);
					} else {
						/*
						 * If a null value will be published.
						 */
						clearDatabaseInput();
					}
				}
			};
			eventBroker.subscribe(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_TABLE_UPDATE, eventHandler);
		}
	}

	private void setDatabaseInput(IQuantDatabaseProxy quantDatabaseProxy) {

		/*
		 * Set the selected database entries.
		 */
		try {
			IQuantDatabases databases = new QuantDatabases();
			database = databases.getQuantDatabase(quantDatabaseProxy);
			ILibraryDescriptionDocument libraryDescriptionDocument = database.getLibraryDescriptionDocument();
			if(libraryDescriptionDocument != null) {
				label.setText("Quantitation Table: " + database.getLibraryDescriptionDocument().getName());
			} else {
				label.setText("Remote Quantitation Table");
			}
			setTableViewerInput();
		} catch(NoDatabaseAvailableException e) {
			logger.warn(e);
			clearDatabaseInput();
		}
	}

	private void clearDatabaseInput() {

		database = null;
		label.setText("The quantitation table is not available.");
		getTableViewer().setInput(null);
		//
		if(eventBroker != null) {
			map.clear();
			map.put(IChemClipseQuantitationEvents.PROPERTY_QUANTITATION_COMPOUND_DOCUMENT, null);
			map.put(IChemClipseQuantitationEvents.PROPERTY_DATABASE, null);
			eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_COMPOUND_DOCUMENT_UPDATE, map);
		}
	}
}
