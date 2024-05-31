/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.ui.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.model.IdentifierFile;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.ui.swt.IdentifierFileListUI;
import org.eclipse.chemclipse.converter.support.FileExtensionCompiler;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverterSupport;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class IdentifierTableEditor extends FieldEditor {

	private Composite compositeButtons;
	private Button buttonAddFile;
	private Button buttonAddDirectory;
	private Button buttonRemove;
	private Button buttonRemoveAll;
	//
	private String[] fileExtensions = new String[]{};
	private String[] fileNames = new String[]{};
	//
	private IdentifierFileListUI identifierFileListUI;
	private IdentifierFileListUtil identifierFileListUtil = new IdentifierFileListUtil();

	public IdentifierTableEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
		initializeFileExtensions();
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {

		super.setEnabled(enabled, parent);
		getTableControl(parent).getTable().setEnabled(enabled);
		buttonAddFile.setEnabled(enabled);
		buttonAddDirectory.setEnabled(enabled);
		buttonRemove.setEnabled(enabled);
		buttonRemoveAll.setEnabled(enabled);
	}

	@Override
	public void setFocus() {

		if(identifierFileListUI != null) {
			identifierFileListUI.getTable().setFocus();
		}
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}

	protected IdentifierFileListUI getListUI() {

		return identifierFileListUI;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		Control control = getLabelControl();
		((GridData)control.getLayoutData()).horizontalSpan = numColumns;
		((GridData)identifierFileListUI.getTable().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doLoad() {

		if(identifierFileListUI != null) {
			String storedContent = getPreferenceStore().getString(getPreferenceName());
			if(storedContent == null || "".equals(storedContent)) {
				identifierFileListUI.setInput(null);
			} else {
				identifierFileListUI.setInput(getDatabaseFiles(storedContent));
			}
		}
	}

	@Override
	protected void doLoadDefault() {

		if(identifierFileListUI != null) {
			String storedContent = getPreferenceStore().getDefaultString(getPreferenceName());
			if(storedContent == null || "".equals(storedContent)) {
				identifierFileListUI.setInput(null);
			} else {
				identifierFileListUI.setInput(getDatabaseFiles(storedContent));
			}
		}
	}

	@Override
	protected void doStore() {

		TableItem[] tableItems = identifierFileListUI.getTable().getItems();
		List<String> files = new ArrayList<>();
		for(int i = 0; i < tableItems.length; i++) {
			Object object = tableItems[i].getData();
			if(object instanceof IdentifierFile calibrationFile) {
				File file = calibrationFile.getFile();
				if(file != null && file.exists()) {
					files.add(file.getAbsolutePath());
				}
			}
		}
		//
		String[] items = files.toArray(new String[files.size()]);
		String storedContent = identifierFileListUtil.createList(items);
		if(storedContent != null) {
			getPreferenceStore().setValue(getPreferenceName(), storedContent);
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		Control control = getLabelControl(parent);
		GridData gridDataControl = new GridData();
		gridDataControl.horizontalSpan = numColumns;
		control.setLayoutData(gridDataControl);
		//
		identifierFileListUI = createDataSection(parent);
		compositeButtons = createButtonSection(parent);
	}

	private IdentifierFileListUI createDataSection(Composite parent) {

		IdentifierFileListUI identifierFileListUI = getTableControl(parent);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		identifierFileListUI.getTable().setLayoutData(gridData);
		//
		return identifierFileListUI;
	}

	private Composite createButtonSection(Composite parent) {

		Composite composite = getButtonControl(parent);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		composite.setLayoutData(gridData);
		//
		return composite;
	}

	private void createButtons(Composite composite) {

		buttonAddFile = createButtonAddFile(composite, "Add DB (File)", "Add a new database."); //$NON-NLS-1$
		buttonAddDirectory = createButtonAddDirectory(composite, "Add DB (Directory)", "Add a new database."); //$NON-NLS-1$
		buttonRemove = createButtonRemove(composite, "Remove", "Remove the selected databases."); //$NON-NLS-1$
		buttonRemoveAll = createButtonRemoveAll(composite, "Remove All", "Remove all databases."); //$NON-NLS-1$
	}

	private Button createButtonAddFile(Composite parent, String text, String tooltip) {

		Button button = createButton(parent, text, tooltip);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addFileDB(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private Button createButtonAddDirectory(Composite parent, String text, String tooltip) {

		Button button = createButton(parent, text, tooltip);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addDirectoryDB(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent, String text, String tooltip) {

		Button button = createButton(parent, text, tooltip);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				remove();
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent, String text, String tooltip) {

		Button button = createButton(parent, text, tooltip);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				removeAll();
			}
		});
		//
		return button;
	}

	private Button createButton(Composite parent, String text, String tooltip) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		//
		return button;
	}

	private void addFileDB(Shell shell) {

		setPresentsDefaultValue(false);
		IdentifierFile databaseFile = selectDatabaseFile(shell);
		if(databaseFile != null) {
			identifierFileListUI.add(databaseFile);
			selectionChanged();
		}
	}

	private void addDirectoryDB(Shell shell) {

		setPresentsDefaultValue(false);
		IdentifierFile databaseFile = selectDatabaseDirectory(shell);
		if(databaseFile != null) {
			identifierFileListUI.add(databaseFile);
			selectionChanged();
		}
	}

	private void remove() {

		setPresentsDefaultValue(false);
		Table table = identifierFileListUI.getTable();
		int[] indices = table.getSelectionIndices();
		//
		int offset = 0;
		for(int index : indices) {
			index -= offset;
			if(index >= 0) {
				table.remove(index);
				offset++;
			}
		}
		//
		selectionChanged();
	}

	private void removeAll() {

		setPresentsDefaultValue(false);
		identifierFileListUI.getTable().removeAll();
	}

	private void selectionChanged() {

		int index = identifierFileListUI.getTable().getSelectionIndex();
		buttonRemove.setEnabled(index >= 0);
		buttonRemoveAll.setEnabled(index >= 0);
	}

	private IdentifierFile selectDatabaseFile(Shell shell) {

		IdentifierFile identifierFile = null;
		//
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setText("Select a Database");
		fileDialog.setFilterPath(PreferenceSupplier.getFilterPathIdentifierFiles());
		fileDialog.setFilterExtensions(fileExtensions);
		fileDialog.setFilterNames(fileNames);
		//
		String filterPath = fileDialog.open();
		if(filterPath != null) {
			File file = new File(filterPath);
			PreferenceSupplier.setFilterPathIdentifierFiles(file.getParentFile().getAbsolutePath());
			identifierFile = addIdentifier(file);
		}
		//
		return identifierFile;
	}

	private IdentifierFile selectDatabaseDirectory(Shell shell) {

		IdentifierFile identifierFile = null;
		//
		DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.OPEN);
		directoryDialog.setText("Select a Database");
		directoryDialog.setFilterPath(PreferenceSupplier.getFilterPathIdentifierFiles());
		//
		String filterPath = directoryDialog.open();
		if(filterPath != null) {
			File file = new File(filterPath);
			PreferenceSupplier.setFilterPathIdentifierFiles(file.getAbsolutePath());
			identifierFile = addIdentifier(file);
		}
		//
		return identifierFile;
	}

	private IdentifierFile addIdentifier(File file) {

		IdentifierFile identifierFile = new IdentifierFile(file);
		/*
		 * Don't databases files twice.
		 */
		TableItem[] tableItems = identifierFileListUI.getTable().getItems();
		exitloop:
		for(TableItem tableItem : tableItems) {
			Object object = tableItem.getData();
			if(object instanceof IdentifierFile newIdentifierFile) {
				if(newIdentifierFile.equals(identifierFile)) {
					identifierFile = null;
					break exitloop;
				}
			}
		}
		//
		return identifierFile;
	}

	private List<IdentifierFile> getDatabaseFiles(String storedContent) {

		String[] files = identifierFileListUtil.parseString(storedContent);
		List<IdentifierFile> identifierFiles = new ArrayList<>();
		for(String file : files) {
			File database = new File(file);
			if(database.exists()) {
				identifierFiles.add(new IdentifierFile(database));
			}
		}
		return identifierFiles;
	}

	private Composite getButtonControl(Composite parent) {

		if(compositeButtons == null) {
			compositeButtons = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			compositeButtons.setLayout(layout);
			//
			createButtons(compositeButtons);
			compositeButtons.addDisposeListener(event -> {
				buttonAddFile = null;
				buttonRemove = null;
				buttonRemoveAll = null;
				compositeButtons = null;
			});
		} else {
			checkParent(compositeButtons, parent);
		}
		//
		selectionChanged();
		return compositeButtons;
	}

	private IdentifierFileListUI getTableControl(Composite parent) {

		if(identifierFileListUI == null) {
			identifierFileListUI = new IdentifierFileListUI(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			Table table = identifierFileListUI.getTable();
			table.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					selectionChanged();
				}
			});
		} else {
			checkParent(identifierFileListUI.getTable(), parent);
		}
		//
		return identifierFileListUI;
	}

	private void initializeFileExtensions() {

		DatabaseConverterSupport databaseConverterSupport = DatabaseConverter.getDatabaseConverterSupport();
		List<String> extensions = new ArrayList<>();
		List<String> names = new ArrayList<>();
		/*
		 * Defaults
		 */
		extensions.add("*.*");
		names.add("All Files (*.*)");
		/*
		 * Extensions
		 */
		List<ISupplier> suppliers = databaseConverterSupport.getSupplier();
		for(ISupplier supplier : suppliers) {
			if(supplier.isImportable()) {
				String directory = supplier.getDirectoryExtension();
				if(directory == null || "".equals(directory)) {
					FileExtensionCompiler fileExtensionCompiler = new FileExtensionCompiler(supplier.getFileExtension(), true);
					extensions.add(fileExtensionCompiler.getCompiledFileExtension());
					names.add(supplier.getFilterName());
				}
			}
		}
		//
		this.fileExtensions = extensions.toArray(new String[extensions.size()]);
		this.fileNames = names.toArray(new String[names.size()]);
	}
}