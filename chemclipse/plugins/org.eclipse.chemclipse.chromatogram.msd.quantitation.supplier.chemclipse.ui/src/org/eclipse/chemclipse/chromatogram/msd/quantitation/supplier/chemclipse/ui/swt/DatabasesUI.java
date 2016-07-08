/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabaseProxy;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabases;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.NoQuantitationTableAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationTableAlreadyExistsException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events.IChemClipseQuantitationEvents;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.DatabaseTableComparator;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.DatabasesContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider.DatabasesLabelProvider;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DatabasesUI extends AbstractTableViewerUI {

	private static final Logger logger = Logger.getLogger(DatabasesUI.class);
	private static final String MESSAGE_BOX_TEXT = "Quantitation Tables";
	private IEventBroker eventBroker;

	public DatabasesUI(Composite parent, int style, IEventBroker eventBroker) {
		parent.setLayout(new FillLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		addList(composite);
		addButtons(composite);
		//
		this.eventBroker = eventBroker;
	}

	@Override
	public void setFocus() {

		super.setFocus();
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
		String[] titles = {"Quantitation Table", "Path"};
		int bounds[] = {100, 100};
		IStructuredContentProvider contentProvider = new DatabasesContentProvider();
		LabelProvider labelProvider = new DatabasesLabelProvider();
		DatabaseTableComparator viewerTableComparator = new DatabaseTableComparator();
		//
		createTableViewer(composite, gridDataTable, contentProvider, labelProvider, viewerTableComparator, titles, bounds);
		getTableViewer().getTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				IQuantDatabaseProxy databaseProxy = getSelectedDatabaseProxy();
				if(databaseProxy != null) {
					setDatabase(databaseProxy.getDatabaseName());
					showMessage(MESSAGE_BOX_TEXT, "The quantitation table has been selected successfully: " + databaseProxy.getDatabaseName());
				}
			}
		});
		setTableViewerInput();
	}

	private void setTableViewerInput() {

		try {
			getTableViewer().setInput(QuantDatabases.listAvailableDatabaseProxies());
		} catch(NoQuantitationTableAvailableException e) {
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
		addButtonSelect(composite);
		addButtonNew(composite);
		addButtonRemove(composite);
		addButtonSave(composite);
	}

	/**
	 * Select a database.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonSelect(Composite parent) {

		Button buttonSelectDatabase = new Button(parent, SWT.PUSH);
		buttonSelectDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonSelectDatabase.setText("Select");
		buttonSelectDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IQuantDatabaseProxy databaseProxy = getSelectedDatabaseProxy();
				if(databaseProxy != null) {
					setDatabase(databaseProxy.getDatabaseName());
					showMessage(MESSAGE_BOX_TEXT, "The quantitation table has been selected successfully: " + databaseProxy.getDatabaseName());
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
	private void addButtonNew(Composite parent) {

		Button buttonNewDatabase = new Button(parent, SWT.PUSH);
		buttonNewDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonNewDatabase.setText("New");
		buttonNewDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				DatabaseNameDialog databaseNameDialog = new DatabaseNameDialog(shell);
				databaseNameDialog.create();
				if(databaseNameDialog.open() == Window.OK) {
					try {
						QuantDatabases.createDatabase(databaseNameDialog.getDatabaseName().trim());
						setTableViewerInput();
					} catch(QuantitationTableAlreadyExistsException e1) {
						showMessage("Quantitation Table", "The database already exists.");
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
	private void addButtonRemove(Composite parent) {

		Button buttonDeleteDatabase = new Button(parent, SWT.PUSH);
		buttonDeleteDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonDeleteDatabase.setText("Delete");
		buttonDeleteDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				showMessage("Quantitation Table", "Please delete the database manually under the given path.");
			}
		});
	}

	/**
	 * Remove a database.
	 * 
	 * @param parent
	 * @param gridData
	 */
	private void addButtonSave(Composite parent) {

		Button buttonSaveDatabases = new Button(parent, SWT.PUSH);
		buttonSaveDatabases.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonSaveDatabases.setText("Save");
		buttonSaveDatabases.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				QuantDatabases.persistDatabases();
			}
		});
	}

	/*
	 * Sets the selected database.
	 */
	private void setDatabase(String databaseName) {

		/*
		 * Send a message to inform e.g. the QuantitationCompoundsUI.
		 */
		PreferenceSupplier.setSelectedQuantitationTable(databaseName);
		if(eventBroker != null) {
			eventBroker.send(IChemClipseQuantitationEvents.TOPIC_QUANTITATION_TABLE_UPDATE, PreferenceSupplier.getSelectedQuantitationTable());
		}
	}

	/*
	 * May return null.
	 */
	private IQuantDatabaseProxy getSelectedDatabaseProxy() {

		ISelection selection = getTableViewer().getSelection();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			Object object = structuredSelection.getFirstElement();
			if(object instanceof IQuantDatabaseProxy) {
				return (IQuantDatabaseProxy)object;
			}
		}
		return null;
	}
}
