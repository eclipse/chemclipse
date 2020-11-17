/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IMeasurementInfo;
import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.DataMapSupportUI;
import org.eclipse.chemclipse.swt.ui.components.IHeaderListener;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ExtendedHeaderDataUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_HEADER_ENTRIES = "Header Entries";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarSearch;
	private AtomicReference<Composite> toolbarSearch = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<DataMapSupportUI> toolbarEdit = new AtomicReference<>();
	private Button buttonTableEdit;
	private Button buttonDelete;
	//
	private TabFolder tabFolder;
	private AtomicReference<HeaderDataListUI> tableViewer = new AtomicReference<>();
	private Text textMiscellaneous;
	//
	private IMeasurementInfo measurementInfo;

	public ExtendedHeaderDataUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(IMeasurementInfo measurementInfo) {

		this.measurementInfo = measurementInfo;
		updateInput();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarSearch(this);
		createToolbarEdit(this);
		createTabFolderSection(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_EDIT, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableEdit(tableViewer, buttonTableEdit, IMAGE_EDIT_ENTRY, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonTableEdit = createButtonToggleEditTable(composite, tableViewer, IMAGE_EDIT_ENTRY);
		buttonDelete = createButtonDelete(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				tableViewer.get().setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createToolbarEdit(Composite parent) {

		DataMapSupportUI headerMapSupportUI = new DataMapSupportUI(parent, SWT.NONE);
		headerMapSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		headerMapSupportUI.setHeaderListener(new IHeaderListener() {

			@Override
			public void update() {

				updateInput();
			}
		});
		//
		toolbarEdit.set(headerMapSupportUI);
	}

	private Button createButtonDelete(Composite parent) {

		Button button = createButton(parent, "", "Delete the selected header entrie(s).", IApplicationImage.IMAGE_DELETE);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteEntries(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private void createTabFolderSection(Composite parent) {

		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateInput();
			}
		});
		//
		createHeaderDataTable(tabFolder);
		textMiscellaneous = createTextMiscellaneous(tabFolder);
	}

	private void createHeaderDataTable(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Table");
		//
		HeaderDataListUI headerDataListUI = new HeaderDataListUI(tabFolder, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = headerDataListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonDelete();
			}
		});
		/*
		 * Add the delete support.
		 */
		Shell shell = headerDataListUI.getTable().getShell();
		ITableSettings tableSettings = headerDataListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		headerDataListUI.applySettings(tableSettings);
		//
		tabItem.setControl(headerDataListUI.getControl());
		tableViewer.set(headerDataListUI);
	}

	private Text createTextMiscellaneous(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Miscellaneous");
		//
		Text text = new Text(tabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(measurementInfo != null) {
					measurementInfo.setMiscInfo(text.getText().trim());
				}
			}
		});
		tabItem.setControl(text);
		//
		return text;
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Header Entrie(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_HEADER_ENTRIES;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteEntries(shell);
			}
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void deleteEntries(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Header Entrie(s)");
		messageBox.setMessage("Would you like to delete the selected header entrie(s)?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete
			 */
			if(measurementInfo != null) {
				Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
				Set<String> keysNotRemoved = new HashSet<String>();
				while(iterator.hasNext()) {
					Object mapObject = iterator.next();
					if(mapObject instanceof Map.Entry) {
						Map.Entry<String, String> entry = (Map.Entry<String, String>)mapObject;
						String key = entry.getKey();
						try {
							measurementInfo.removeHeaderData(key);
						} catch(InvalidHeaderModificationException e) {
							keysNotRemoved.add(key);
						}
					}
				}
				/*
				 * Show a message if certain keys couldn't be removed.
				 */
				if(keysNotRemoved.size() > 0) {
					MessageDialog.openWarning(DisplayUtils.getShell(), DataMapSupportUI.HEADER_ENTRY, "The following keys can't be removed: " + keysNotRemoved);
				}
				//
				updateInput();
			}
		}
	}

	private void updateInput() {

		DataMapSupportUI dataMapSupportUI = toolbarEdit.get();
		if(dataMapSupportUI != null) {
			dataMapSupportUI.setInput(measurementInfo != null ? measurementInfo.getHeaderDataMap() : null);
		}
		updateData();
	}

	private void updateData() {

		enableButtonDelete();
		//
		if(measurementInfo != null) {
			toolbarInfo.get().setText("Number of Entries: " + measurementInfo.getHeaderDataMap().size());
			tableViewer.get().setInput(measurementInfo);
			textMiscellaneous.setText(measurementInfo.getMiscInfo());
		} else {
			toolbarInfo.get().setText("--");
			tableViewer.get().setInput(null);
			textMiscellaneous.setText("");
		}
		//
		tableViewer.get().sortTable();
		Table table = tableViewer.get().getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
		}
	}

	@SuppressWarnings("rawtypes")
	private void enableButtonDelete() {

		buttonDelete.setEnabled(false);
		if(measurementInfo != null) {
			Object object = tableViewer.get().getStructuredSelection().getFirstElement();
			if(object instanceof Map.Entry) {
				Map.Entry entry = (Map.Entry)object;
				boolean enabled = !measurementInfo.isKeyProtected(entry.getKey().toString());
				buttonDelete.setEnabled(enabled);
			}
		}
	}
}
