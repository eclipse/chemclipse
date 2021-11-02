/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui.fieldeditors;

import java.io.File;
import java.util.Map;

import org.eclipse.chemclipse.converter.model.SeparationColumnMapping;
import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.converter.ui.swt.ColumExtractorTable;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ColumnExtractorFieldEditor extends FieldEditor {

	private static final String ADD = "Add";
	private static final String ADD_TOOLTIP = "Add a new keyword";
	private static final String REMOVE = "Remove";
	private static final String REMOVE_TOOLTIP = "Remove the selected keyword(s)";
	private static final String REMOVE_ALL = "Remove All";
	private static final String REMOVE_ALL_TOOLTIP = "Remove All Keywords";
	private static final String IMPORT = "Import";
	private static final String EXPORT = "Export";
	//
	private static final String IMPORT_TITLE = "Import Keywords";
	private static final String EXPORT_TITLE = "Export Keywords";
	private static final String DIALOG_TITLE = "Keywords(s)";
	private static final String MESSAGE_ADD = "You can create a new keyword here.";
	private static final String MESSAGE_REMOVE = "Do you want to delete the selected keyword(s)?";
	private static final String MESSAGE_REMOVE_ALL = "Do you want to delete all keyword(s)?";
	private static final String MESSAGE_EXPORT_SUCCESSFUL = "Keywords have been exported successfully.";
	private static final String MESSAGE_EXPORT_FAILED = "Failed to export the keywords.";
	//
	private static final String FILTER_EXTENSION = "*.txt";
	private static final String FILTER_NAME = "Separation Column Mapping (*.txt)";
	private static final String FILE_NAME = "SeparationColumnMapping.txt";
	//
	private static final String CATEGORY = "Column Mapping";
	private static final String DELETE = "Delete";
	//
	private static final int NUMBER_COLUMNS = 2;
	//
	private Composite composite;
	private ColumExtractorTable extendedTable;
	private SeparationColumnMapping mapping = new SeparationColumnMapping();

	public ColumnExtractorFieldEditor(String name, String labelText, Composite parent) {

		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		getLabelControl(parent);
		//
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUMBER_COLUMNS, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		//
		createTableSection(composite);
		createButtonGroup(composite);
	}

	@Override
	public int getNumberOfControls() {

		return 1;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {

		if(numColumns >= 2) {
			GridData gridData = (GridData)composite.getLayoutData();
			gridData.horizontalSpan = numColumns - 1;
			gridData.grabExcessHorizontalSpace = true;
		}
	}

	@Override
	protected void doLoad() {

		String entries = getPreferenceStore().getString(getPreferenceName());
		mapping.load(entries);
		setTableViewerInput();
	}

	@Override
	protected void doLoadDefault() {

		String entries = getPreferenceStore().getDefaultString(getPreferenceName());
		mapping.loadDefault(entries);
		setTableViewerInput();
	}

	@Override
	protected void doStore() {

		getPreferenceStore().setValue(getPreferenceName(), mapping.save());
	}

	private void createTableSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);
		//
		extendedTable = new ColumExtractorTable(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		//
		Shell shell = extendedTable.getTable().getShell();
		ITableSettings tableSettings = extendedTable.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		extendedTable.applySettings(tableSettings);
		//
		setTableViewerInput();
	}

	private void createButtonGroup(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(gridData);
		//
		setButtonLayoutData(createButtonAdd(composite));
		setButtonLayoutData(createButtonRemove(composite));
		setButtonLayoutData(createButtonRemoveAll(composite));
		setButtonLayoutData(createButtonImport(composite));
		setButtonLayoutData(createButtonExport(composite));
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(ADD);
		button.setToolTipText(ADD_TOOLTIP);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				InputDialog dialog = new InputDialog(button.getShell(), DIALOG_TITLE, MESSAGE_ADD, "DB5", new IInputValidator() {

					@Override
					public String isValid(String newText) {

						if("".equals(newText)) {
							return "Please type in a keyword.";
						} else if(newText.contains(SeparationColumnMapping.SEPARATOR_ENTRY)) {
							return "The keyword must not contain the following character: '" + SeparationColumnMapping.SEPARATOR_ENTRY + "'";
						} else if(newText.contains(SeparationColumnMapping.SEPARATOR_TOKEN)) {
							return "The keyword must not contain the following character: '" + SeparationColumnMapping.SEPARATOR_TOKEN + "'";
						} else {
							if(mapping.containsKey(newText)) {
								return "The keyword already exists.";
							}
						}
						return null;
					}
				});
				if(IDialogConstants.OK_ID == dialog.open()) {
					String item = dialog.getValue().trim();
					if(!"".equals(item)) {
						if(!mapping.keySet().contains(item)) {
							mapping.put(item, SeparationColumnType.DEFAULT.value());
							setTableViewerInput();
						}
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonRemove(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(REMOVE);
		button.setToolTipText(REMOVE_TOOLTIP);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteItems(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(REMOVE_ALL);
		button.setToolTipText(REMOVE_ALL_TOOLTIP);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(button.getShell(), DIALOG_TITLE, MESSAGE_REMOVE_ALL)) {
					mapping.clear();
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonImport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(IMPORT);
		button.setToolTipText(IMPORT_TITLE);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText(IMPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFilterPath(PreferenceSupplier.getListPathImport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathImport(fileDialog.getFilterPath());
					File file = new File(path);
					mapping.importItems(file);
					setTableViewerInput();
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExport(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(EXPORT);
		button.setToolTipText(EXPORT_TITLE);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(e.widget.getDisplay().getActiveShell(), SWT.SAVE);
				fileDialog.setOverwrite(true);
				fileDialog.setText(EXPORT_TITLE);
				fileDialog.setFilterExtensions(new String[]{FILTER_EXTENSION});
				fileDialog.setFilterNames(new String[]{FILTER_NAME});
				fileDialog.setFileName(FILE_NAME);
				fileDialog.setFilterPath(PreferenceSupplier.getListPathExport());
				String path = fileDialog.open();
				if(path != null) {
					PreferenceSupplier.setListPathExport(fileDialog.getFilterPath());
					File file = new File(path);
					if(mapping.exportItems(file)) {
						MessageDialog.openInformation(button.getShell(), EXPORT_TITLE, MESSAGE_EXPORT_SUCCESSFUL);
					} else {
						MessageDialog.openWarning(button.getShell(), EXPORT_TITLE, MESSAGE_EXPORT_FAILED);
					}
				}
			}
		});
		//
		return button;
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return DELETE;
			}

			@Override
			public String getCategory() {

				return CATEGORY;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteItems(shell);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					deleteItems(shell);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void deleteItems(Shell shell) {

		if(MessageDialog.openQuestion(shell, DIALOG_TITLE, MESSAGE_REMOVE)) {
			IStructuredSelection structuredSelection = (IStructuredSelection)extendedTable.getSelection();
			for(Object object : structuredSelection.toArray()) {
				if(object instanceof Map.Entry) {
					mapping.remove(((Map.Entry)object).getKey());
				}
			}
			setTableViewerInput();
		}
	}

	private void setTableViewerInput() {

		extendedTable.setInput(mapping);
	}
}
