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

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.DatabaseTableComparator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.DatabasesContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.DatabasesLabelProvider;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.entries.DatabaseNewEntry;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.database.ui.runnables.dialogs.DatabaseNewDialog;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
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

public class DatabasesUI extends AbstractTableViewerUI {

	private static final Logger logger = Logger.getLogger(DatabasesUI.class);
	private static final String MESSAGE_BOX_TEXT = "Quantitation Tables";
	private IEventBroker eventBroker;
	private Button buttonNewDatabase;
	private Button buttonSelectDatabase;
	private Button buttonDeleteDatabase;

	public DatabasesUI(Composite parent, int style, IEventBroker eventBroker) {
		parent.setLayout(new FillLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		addList(composite);
		addButtons(composite);
		checkButtons();
		//
		this.eventBroker = eventBroker;
	}

	@Override
	public void setFocus() {

		super.setFocus();
		checkButtons();
		setTableViewerInput();
	}

	private void addList(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		gridDataTable.grabExcessHorizontalSpace = true;
		gridDataTable.grabExcessVerticalSpace = true;
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(gridDataTable);
		//
		String[] titles = {"Quantitation Table", "URL"};
		int bounds[] = {100, 100};
		IStructuredContentProvider contentProvider = new DatabasesContentProvider();
		LabelProvider labelProvider = new DatabasesLabelProvider();
		DatabaseTableComparator viewerTableComparator = new DatabaseTableComparator();
		//
		createTableViewer(composite, gridDataTable, contentProvider, labelProvider, viewerTableComparator, titles, bounds);
		setTableViewerInput();
	}

	/**
	 * If a remote database is used, disable the buttons to select, delete or add
	 * a database.
	 */
	private void checkButtons() {

		if(PreferenceSupplier.useDatabaseRemote()) {
			/*
			 * Remote
			 */
			buttonNewDatabase.setEnabled(false);
			buttonDeleteDatabase.setEnabled(false);
		} else {
			/*
			 * Local
			 */
			buttonNewDatabase.setEnabled(true);
			buttonDeleteDatabase.setEnabled(true);
		}
	}

	private void setTableViewerInput() {

		IQuantDatabases databasesQuant = new QuantDatabases();
		try {
			getTableViewer().setInput(databasesQuant.listAvailableDatabaseProxies());
		} catch(NoDatabaseAvailableException e) {
			logger.warn(e);
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
		addButtonSelect(composite, gridData);
		addButtonNew(composite, gridData);
		addButtonRemove(composite, gridData);
	}

	/**
	 * Select a database.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonSelect(Composite parent, GridData gridData) {

		buttonSelectDatabase = new Button(parent, SWT.PUSH);
		buttonSelectDatabase.setLayoutData(gridData);
		buttonSelectDatabase.setText("Select");
		buttonSelectDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String databaseName = getDatabaseNameFromTableSelection();
				if(databaseName != null) {
					setDatabase(databaseName);
					showMessage(MESSAGE_BOX_TEXT, "The quantitation table has been selected successfully: " + databaseName);
				}
			}
		});
	}

	/**
	 * Add a new database.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonNew(Composite parent, GridData gridData) {

		buttonNewDatabase = new Button(parent, SWT.PUSH);
		buttonNewDatabase.setLayoutData(gridData);
		buttonNewDatabase.setText("New");
		buttonNewDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				DatabaseNewEntry databaseNewEntry = new DatabaseNewEntry();
				IQuantDatabases databasesQuant = new QuantDatabases();
				DatabaseNewDialog newDatabaseDialog = new DatabaseNewDialog(shell, databasesQuant, databaseNewEntry, "Create a new quantitation database");
				int returnCode = newDatabaseDialog.open();
				if(returnCode == IDialogConstants.OK_ID) {
					String databaseName = databaseNewEntry.getDatabaseName();
					String description = databaseNewEntry.getDatabaseDescription();
					try {
						databasesQuant.createDatabase(databaseName, description);
						setDatabase(databaseName);
						setTableViewerInput();
						showMessage(MESSAGE_BOX_TEXT, "The quantitation table has been created successfully: " + databaseName);
					} catch(NoDatabaseAvailableException e1) {
						logger.warn(e1);
					}
				}
			}
		});
	}

	/**
	 * Remove a database.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonRemove(Composite parent, GridData gridData) {

		buttonDeleteDatabase = new Button(parent, SWT.PUSH);
		buttonDeleteDatabase.setLayoutData(gridData);
		buttonDeleteDatabase.setText("Delete");
		buttonDeleteDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String databaseName = getDatabaseNameFromTableSelection();
				Shell shell = Display.getCurrent().getActiveShell();
				if(databaseName != null) {
					int decision = showQuestion(MESSAGE_BOX_TEXT, "Do you really want to delete the quantitation table: " + databaseName);
					if(decision == SWT.YES) {
						/*
						 * Database that shall be deleted.
						 */
						DatabasePathHelper pathHelper = new DatabasePathHelper(QuantDatabases.IDENTIFIER_QUANTITATION);
						String localDatabase = pathHelper.getLocalDatabasePath(databaseName);
						MessageBox messageBox = new MessageBox(shell, SWT.OK);
						messageBox.setText(MESSAGE_BOX_TEXT);
						messageBox.setMessage("Please delete the database after program exit manually under:\r\n" + localDatabase);
						messageBox.open();
					}
				}
			}
		});
	}

	/*
	 * Sets the selected database.
	 */
	private void setDatabase(String databaseName) {

		/*
		 * If local is used.
		 */
		// if(!PreferenceSupplier.useDatabaseRemote()) {
		// PreferenceSupplier.setSelectedDBLocal(databaseName);
		// }
		IPreferenceSupplier ps = PreferenceSupplier.INSTANCE();
		if(ps instanceof PreferenceSupplier) {
			PreferenceSupplier preferenceSupplier = (PreferenceSupplier)ps;
			preferenceSupplier.setSelectedDatabase(databaseName);
			/*
			 * Send a message to inform e.g. the QuantitationCompoundsUI.
			 */
			if(eventBroker != null) {
				eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_TABLE_UPDATE, preferenceSupplier.getDatabaseProxy());
			}
		}
	}

	/*
	 * May return null.
	 */
	private String getDatabaseNameFromTableSelection() {

		String databaseName = null;
		ISelection selection = getTableViewer().getSelection();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			Object object = structuredSelection.getFirstElement();
			if(object instanceof IDatabaseProxy) {
				databaseName = ((IDatabaseProxy)object).getDatabaseName();
			}
		}
		return databaseName;
	}
}
