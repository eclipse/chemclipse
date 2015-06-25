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
package org.eclipse.chemclipse.database.ui.fieldeditors;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.database.model.DatabaseSettings;
import org.eclipse.chemclipse.database.model.IDatabaseSettings;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.database.model.entries.DatabaseNewEntry;
import org.eclipse.chemclipse.database.model.entries.DatabaseSelectEntry;
import org.eclipse.chemclipse.database.preferences.IDatabasePreferences;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.database.ui.runnables.CreateNewDatabaseRunnable;
import org.eclipse.chemclipse.database.ui.runnables.dialogs.DatabaseNewDialog;
import org.eclipse.chemclipse.database.ui.runnables.dialogs.DatabaseSelectDialog;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DatabaseFieldEditor extends StringFieldEditor {

	private static final Logger logger = Logger.getLogger(DatabaseFieldEditor.class);
	private IDatabases databases;
	private IDatabasePreferences databasePreferences;
	private String title;

	public DatabaseFieldEditor(String name, String labelText, IDatabases databases, IDatabasePreferences databasePreferences, String title, Composite parent) {

		super(name, labelText, parent);
		this.databases = databases;
		this.databasePreferences = databasePreferences;
		this.title = title;
	}

	@Override
	protected boolean checkState() {

		Text textControl = getTextControl();
		if(textControl == null) {
			return false;
		}
		String stringValue = textControl.getText();
		if(isValidDatabase(stringValue)) {
			clearErrorMessage();
			this.doStore();
			return true;
		} else {
			setAndShowErrorMessage();
		}
		return false;
	}

	private boolean isValidDatabase(String stringValue) {

		if(stringValue != null && !stringValue.equals("") && DatabasePathHelper.isValidDatabasePath(stringValue)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void createControl(Composite parent) {

		super.createControl(parent);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessVerticalSpace = false;
		/*
		 * Select existing Database
		 */
		Button buttonSelectDatabase = new Button(parent, SWT.NONE);
		buttonSelectDatabase.setText("Select");
		buttonSelectDatabase.setLayoutData(gridData);
		buttonSelectDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				DatabaseSelectEntry databaseSelectEntry = new DatabaseSelectEntry();
				/*
				 * Check whether we work with local or remote databases
				 */
				IDatabaseSettings ds;
				if(databasePreferences.isRemoteConnection()) {
					ds = new DatabaseSettings(DatabasePathHelper.REMOTE_DB_PREFIX + databasePreferences.getSelectedServer(), databasePreferences.getSelectedUserRemote(), databasePreferences.getSelectedPasswordRemote());
				} else {
					ds = new DatabaseSettings(DatabasePathHelper.LOCAL_DB_PREFIX, "", "");
				}
				DatabaseSelectDialog databaseSelectDialog = new DatabaseSelectDialog(shell, databases, ds, databaseSelectEntry, title);
				int returnCode = databaseSelectDialog.open();
				if(returnCode == IDialogConstants.OK_ID) {
					String name = databaseSelectEntry.getDatabaseName();
					setDatabaseName(name);
				}
			}
		});
		/*
		 * Add new Database
		 */
		Button buttonAddDatabase = new Button(parent, SWT.NONE);
		buttonAddDatabase.setText("New");
		buttonAddDatabase.setLayoutData(gridData);
		buttonAddDatabase.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				DatabaseNewEntry databaseNewEntry = new DatabaseNewEntry();
				DatabaseNewDialog newDatabaseDialog = new DatabaseNewDialog(shell, databases, databaseNewEntry, title);
				int returnCode = newDatabaseDialog.open();
				if(returnCode == IDialogConstants.OK_ID) {
					String name = databaseNewEntry.getDatabaseName();
					String description = databaseNewEntry.getDatabaseDescription();
					IRunnableWithProgress runnable = new CreateNewDatabaseRunnable(databases, name, description);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
					try {
						/*
						 * Use true, true ... instead of false, true ... if the progress bar
						 * should be shown in action.
						 */
						monitor.run(true, true, runnable);
						setDatabaseName(name);
					} catch(InvocationTargetException ex) {
						logger.warn(ex);
						logger.warn(ex.getCause());
					} catch(InterruptedException ex) {
						logger.warn(ex);
					}
					// databases.createDatabaseLocal(name, description);
				}
			}
		});
	}

	private void setDatabaseName(String name) {

		databasePreferences.setSelectedDatabase(name);
		/*
		 * Check the text control.
		 */
		Text textControl = getTextControl();
		textControl.setText(name);
		if(checkState()) {
			textControl.setFocus();
			/*
			 * Activate the OK button
			 */
			DialogPage dialogPage = getPage();
			if(dialogPage instanceof PreferencePage) {
				((PreferencePage)dialogPage).setValid(true);
			}
		}
	}

	@Override
	protected void doLoad() {

		Text textControl = getTextControl();
		if(textControl != null) {
			String value = getPreferenceStore().getString(getPreferenceName());
			textControl.setText(value);
		}
		super.doLoad();
	}

	@Override
	protected void doLoadDefault() {

		Text textControl = getTextControl();
		if(textControl != null) {
			String value = getPreferenceStore().getDefaultString(getPreferenceName());
			textControl.setText(value);
		}
		valueChanged();
	}

	@Override
	protected void doStore() {

		Text textControl = getTextControl();
		if(textControl != null) {
			String value = textControl.getText().trim();
			getPreferenceStore().setValue(getPreferenceName(), value);
		}
	}

	private void setAndShowErrorMessage() {

		showErrorMessage("The selected database does not exist. Please use the add new or select button.");
	}
}
