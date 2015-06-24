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
 * Dr. Janos Binder - refactoring database access
 *******************************************************************************/
package org.eclipse.chemclipse.database.ui.runnables.dialogs;

import java.util.Arrays;

import org.eclipse.chemclipse.database.documents.ILibraryDescriptionDocument;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.IDatabase;
import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.IDatabaseSettings;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.database.model.entries.DatabaseSelectEntry;
import org.eclipse.chemclipse.database.ui.internal.provider.DatabaseProxyComparator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DatabaseSelectDialog extends Dialog {

	private static final Logger logger = Logger.getLogger(DatabaseSelectDialog.class);
	private String title;
	private String message;
	private Label messageLabel;
	private ComboViewer databaseComboViewer;
	private Text databaseDescriptionText;
	private static final String ERROR_MESSAGE = "Please select a database from the list.";
	private DatabaseSelectEntry databaseSelectEntry;
	private IDatabases databases;
	private IDatabaseSettings databaseSettings;
	private String selectedDatabase;

	public DatabaseSelectDialog(Shell parentShell, IDatabases databases, IDatabaseSettings databaseSettings, DatabaseSelectEntry databaseSelectEntry, String title) {

		super(parentShell);
		this.title = title;
		this.message = "Select a database.";
		this.databases = databases;
		this.databaseSelectEntry = databaseSelectEntry;
		this.databaseSettings = databaseSettings;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		if(title != null) {
			shell.setText(title);
		}
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {

		/*
		 * Set values only if the OK button has been clicked.
		 */
		if(buttonId == IDialogConstants.OK_ID) {
			databaseSelectEntry.setDatabaseName(selectedDatabase);
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected Control createButtonBar(Composite parent) {

		Control control = super.createButtonBar(parent);
		// validateDatabaseName();
		return control;
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		layoutData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		createMessage(composite, layoutData);
		createDatabaseNameCombo(composite, layoutData);
		createDatabaseDescriptionText(composite);
		// validateDatabaseName();
		return composite;
	}

	/**
	 * Sets the dialog message.
	 */
	private void createMessage(Composite parent, GridData layoutData) {

		if(message != null) {
			messageLabel = new Label(parent, SWT.WRAP);
			messageLabel.setText(message);
			messageLabel.setLayoutData(layoutData);
			messageLabel.setFont(parent.getFont());
		}
	}

	private void createDatabaseNameCombo(Composite parent, GridData layoutData) {

		databaseComboViewer = new ComboViewer(parent, SWT.NONE);
		Combo combo = databaseComboViewer.getCombo();
		combo.setLayoutData(layoutData);
		setItemsComboViewer(databaseComboViewer);
	}

	private void createDatabaseDescriptionText(Composite parent) {

		GridData gridData;
		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_END);
		gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		gridData.heightHint = 20;
		gridData.verticalIndent = 10;
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(gridData);
		label.setText("Database Description:");
		/*
		 * The database description
		 */
		gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		gridData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		gridData.heightHint = 100;
		databaseDescriptionText = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		databaseDescriptionText.setLayoutData(gridData);
		databaseDescriptionText.setText("Description");
	}

	private void validateDatabaseName(final IDatabaseProxy databaseProxy) {

		if(databaseProxy == null || databaseProxy.getDatabaseUrl().equals("")) {
			/*
			 * This is a valid trial version. The ok button must be enabled.
			 */
			setErrorMessage(ERROR_MESSAGE);
		} else {
			try {
				IDatabase db = databases.getDatabase(databaseProxy);
				ILibraryDescriptionDocument libraryDescriptionDocument = db.getLibraryDescriptionDocument();
				if(libraryDescriptionDocument != null) {
					databaseDescriptionText.setText(libraryDescriptionDocument.getDescription());
				}
				setErrorMessage("");
				// TODO can be improved by passing the databaseProxy object
				selectedDatabase = databaseProxy.getDatabaseUrl();
			} catch(NoDatabaseAvailableException e1) {
				setErrorMessage("The selected database is not available.");
			} finally {
			}
		}
	}

	private void setErrorMessage(String errorMessage) {

		Control button = getButton(IDialogConstants.OK_ID);
		/*
		 * Show the product error message if available.
		 */
		if(errorMessage != null && !errorMessage.equals("")) {
			/*
			 * Set the error message.
			 */
			messageLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			messageLabel.setText(errorMessage);
			/*
			 * There is an error. Disable the ok button.
			 */
			if(button != null) {
				button.setEnabled(false);
			}
		} else {
			/*
			 * Remove the error message.
			 */
			if(messageLabel != null && !messageLabel.isDisposed()) {
				messageLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				messageLabel.setText(message);
			}
			/*
			 * If there is no error, the input must be valid.
			 */
			if(button != null) {
				button.setEnabled(true);
			}
		}
	}

	private void setItemsComboViewer(ComboViewer comboViewer) {

		IDatabaseProxy[] databaseMap = new IDatabaseProxy[]{};
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IDatabaseProxy) {
					IDatabaseProxy proxy = (IDatabaseProxy)element;
					return proxy.getDatabaseName();
				}
				return super.getText(element);
			}
		});
		/*
		 * Select a database
		 */
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.size() > 0) {
					Object entry = selection.getFirstElement();
					if(entry instanceof IDatabaseProxy) {
						IDatabaseProxy proxy = (IDatabaseProxy)entry;
						validateDatabaseName(proxy);
					}
				}
			}
		});
		try {
			databaseMap = databases.listAvailableDatabaseProxies(databaseSettings).toArray(new IDatabaseProxy[]{});
		} catch(NoDatabaseAvailableException e) {
			logger.warn(e);
		}
		Arrays.<IDatabaseProxy> sort(databaseMap, new DatabaseProxyComparator());
		comboViewer.setInput(databaseMap);
	}
}
