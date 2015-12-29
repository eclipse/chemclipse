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
package org.eclipse.chemclipse.database.ui.swt;

import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.IDatabaseProxy;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.database.ui.internal.provider.ContentProvider;
import org.eclipse.chemclipse.database.ui.internal.provider.DatabaseLabelProvider;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DatabasesUI {

	private static final Logger logger = Logger.getLogger(DatabasesUI.class);
	private ExtendedTableViewer tableViewer;
	private String[] titles = {"Database Name", "Database URL"};
	private int bounds[] = {100, 100};
	private IDatabases databases;
	// UI Strings
	final private String stringMessageBoxTitle = "Open database";
	final private String stringMessageBoxText = "Do you want to open database : ";

	// End of UI Strings
	public DatabasesUI(Composite parent, int style, final IDatabases databases) {
		parent.setLayout(new FillLayout());
		//
		this.databases = databases;
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new DatabaseLabelProvider());
		try {
			tableViewer.setInput(databases.listAvailableDatabaseProxies());
		} catch(NoDatabaseAvailableException e1) {
			logger.warn(e1);
		}
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.addCopyToClipboardListener(titles);
		/*
		 * Support double clicks
		 */
		tableViewer.getTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				Object element = selection.getFirstElement();
				if(element instanceof IDatabaseProxy) {
					IDatabaseProxy databaseProxy = (IDatabaseProxy)element;
					String databaseName = databaseProxy.getDatabaseName();
					Shell shell = Display.getCurrent().getActiveShell();
					MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
					dialog.setText(stringMessageBoxTitle);
					dialog.setMessage(stringMessageBoxText + databaseName);
					int returnCode = dialog.open();
					if(returnCode == SWT.OK) {
						handleDatabase(databaseProxy, shell);
					}
				}
			}
		});
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
		try {
			tableViewer.setInput(databases.listAvailableDatabaseProxies());
		} catch(NoDatabaseAvailableException e) {
			logger.warn(e);
		}
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	private void handleDatabase(IDatabaseProxy databaseProxy, Shell shell) {

		IProcessingInfo processingInfo = new ProcessingInfo();
		String databaseName = databaseProxy.getDatabaseName();
		try {
			databases.getDatabase().close();
			databases.setDatabaseInPreferenceSupplier(databaseProxy);
			databases.getDatabase(databaseProxy);
			processingInfo.addInfoMessage("Changed database", "Connected to: " + databaseName);
		} catch(NoDatabaseAvailableException e1) {
			MessageDialog.openError(shell, "Could not connect to database", "Could not connect to database: " + databaseName + "\nReason: " + e1.getMessage());
			processingInfo.addErrorMessage("Could not connect to database: " + databaseName, e1.getMessage());
		} finally {
			ProcessingInfoViewSupport.updateProcessingInfo(processingInfo, false);
		}
	}
}
