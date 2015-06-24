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
package org.eclipse.chemclipse.database.ui.runnables.dialogs;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.DatabaseSettings;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.database.model.entries.DatabaseNewEntry;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DatabaseNewDialog extends Dialog {

	private static final Logger logger = Logger.getLogger(DatabaseNewDialog.class);
	private String title;
	private String message;
	private Label messageLabel;
	private Text databaseNameText;
	private Text databaseDescriptionText;
	private DatabaseNewEntry databaseNewEntry;
	private List<String> availableDatabases;
	private IDatabases databases;

	public DatabaseNewDialog(Shell parentShell, IDatabases databases, DatabaseNewEntry databaseNewEntry, String title) {

		super(parentShell);
		this.title = title;
		this.message = "Create a new database.";
		this.databases = databases;
		this.databaseNewEntry = databaseNewEntry;
		this.availableDatabases = getAvailableDatabases();
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
			String formattedDatabaseName = createProperlyFormattedLocalDatabasePath(databaseNameText.getText().trim());
			databaseNewEntry.setDatabaseName(formattedDatabaseName);
			databaseNewEntry.setDatabaseDescription(databaseDescriptionText.getText());
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected Control createButtonBar(Composite parent) {

		Control control = super.createButtonBar(parent);
		validateDatabaseName();
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
		createDatabaseNameText(composite, layoutData);
		createDatabaseDescriptionText(composite);
		validateDatabaseName();
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

	private void createDatabaseNameText(Composite parent, GridData layoutData) {

		databaseNameText = new Text(parent, SWT.BORDER);
		databaseNameText.setLayoutData(layoutData);
		/*
		 * Don't type in a new database name.
		 */
		databaseNameText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validateDatabaseName();
			}
		});
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
		databaseDescriptionText = new Text(parent, SWT.MULTI | SWT.BORDER);
		databaseDescriptionText.setLayoutData(gridData);
		databaseDescriptionText.setText("Add a description here ...");
	}

	private void validateDatabaseName() {

		String databaseName = databaseNameText.getText().trim();
		if(databaseName == null || databaseName.equals("")) {
			/*
			 * This is a valid trial version. The ok button must be enabled.
			 */
			setErrorMessage("The database must have a name.");
		} else {
			if(availableDatabases.contains(databaseName)) {
				setErrorMessage("The database already exists.");
			} else {
				setErrorMessage("");
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

	private List<String> getAvailableDatabases() {

		/*
		 * Lists only local databases
		 */
		List<String> databaseNames = null;
		try {
			databaseNames = databases.getDatabaseNames(new DatabaseSettings(DatabasePathHelper.LOCAL_DB_PREFIX, "", ""));
		} catch(NoDatabaseAvailableException e) {
			logger.warn(e);
		}
		return databaseNames;
	}

	private String createProperlyFormattedLocalDatabasePath(String database) {

		DatabasePathHelper pathHelper = new DatabasePathHelper(databases.getIdentifier());
		File storagePath = pathHelper.getStoragePath();
		return DatabasePathHelper.LOCAL_DB_PREFIX + storagePath + File.separator + database;
	}
}
