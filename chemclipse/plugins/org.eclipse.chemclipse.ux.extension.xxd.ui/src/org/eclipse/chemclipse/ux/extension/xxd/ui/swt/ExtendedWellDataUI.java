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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePCR;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class ExtendedWellDataUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_HEADER_ENTRIES = "Header Entries";
	private static final String HEADER_ENTRY = "Header Entry";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfoTop = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfoBottom = new AtomicReference<>();
	private Button buttonToolbarSearch;
	private AtomicReference<Composite> toolbarSearch = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<DataMapSupportUI> toolbarEdit = new AtomicReference<>();
	private Button buttonDelete;
	//
	private Button buttonTableEdit;
	private AtomicReference<WellDataListUI> tableViewer = new AtomicReference<>();
	//
	private IWell well;
	private boolean editable;

	public ExtendedWellDataUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		updateInput();
		return true;
	}

	public void update(IWell well) {

		this.well = well;
		this.editable = (well != null);
		updateInput();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfoTop(this);
		createToolbarEdit(this);
		createToolbarSearch(this);
		createWellDataTable(this);
		createToolbarInfoBottom(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfoTop, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableToolbar(toolbarInfoBottom, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		//
		enableEdit(tableViewer, buttonTableEdit, IMAGE_EDIT_ENTRY, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, Arrays.asList(toolbarInfoTop, toolbarInfoBottom), IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonTableEdit = createButtonToggleEditTable(composite, tableViewer, IMAGE_EDIT_ENTRY);
		buttonDelete = createButtonDelete(composite);
		createSettingsButton(composite);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePagePCR.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

	}

	private void createToolbarInfoTop(Composite parent) {

		toolbarInfoTop.set(createToolbarInfo(parent));
	}

	private void createToolbarInfoBottom(Composite parent) {

		toolbarInfoBottom.set(createToolbarInfo(parent));
	}

	private InformationUI createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return informationUI;
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

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected entrie(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteHeaderEntries(e.display.getActiveShell());
			}
		});
		return button;
	}

	private void createWellDataTable(Composite parent) {

		WellDataListUI wellDataListUI = new WellDataListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = wellDataListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonDelete();
			}
		});
		/*
		 * Add the delete targets support.
		 */
		Shell shell = wellDataListUI.getTable().getShell();
		ITableSettings tableSettings = wellDataListUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		wellDataListUI.applySettings(tableSettings);
		//
		tableViewer.set(wellDataListUI);
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Data Entrie(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_HEADER_ENTRIES;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteHeaderEntries(shell);
			}
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void deleteHeaderEntries(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Data Entrie(s)");
		messageBox.setMessage("Would you like to delete the selected data entrie(s)?");
		if(messageBox.open() == SWT.YES) {
			if(well != null) {
				Iterator iterator = tableViewer.get().getStructuredSelection().iterator();
				Set<String> keysNotRemoved = new HashSet<String>();
				while(iterator.hasNext()) {
					Object mapObject = iterator.next();
					if(mapObject instanceof Map.Entry) {
						Map.Entry<String, String> entry = (Map.Entry<String, String>)mapObject;
						String key = entry.getKey();
						try {
							well.removeData(key);
						} catch(InvalidHeaderModificationException e) {
							keysNotRemoved.add(key);
						}
					}
				}
				/*
				 * Show a message if certain keys couldn't be removed.
				 */
				if(keysNotRemoved.size() > 0) {
					MessageDialog.openWarning(DisplayUtils.getShell(), HEADER_ENTRY, "The following keys can't be removed: " + keysNotRemoved);
				}
				//
				updateInput();
			}
		}
	}

	private void updateInput() {

		if(well == null) {
			toolbarEdit.get().setInput(null);
			tableViewer.get().setInput(null);
		} else {
			toolbarEdit.get().setInput(well.getData());
			tableViewer.get().setInput(well);
			//
			WellDataListUI wellDataListUI = tableViewer.get();
			wellDataListUI.sortTable();
			Table table = wellDataListUI.getTable();
			if(table.getItemCount() > 0) {
				table.setSelection(0);
			}
		}
		//
		updateWidgets();
		updateLabel();
	}

	private void updateLabel() {

		toolbarInfoTop.get().setText(well != null ? well.getLabel() : "");
		toolbarInfoBottom.get().setText(well != null ? "Number of Entries: " + well.getData().size() : "");
	}

	private void updateWidgets() {

		boolean enabled = editable;
		//
		buttonTableEdit.setEnabled(enabled);
		enableButtonDelete();
	}

	private void enableButtonDelete() {

		buttonDelete.setEnabled(false);
		if(well != null) {
			Object object = tableViewer.get().getStructuredSelection().getFirstElement();
			if(object instanceof Map.Entry) {
				buttonDelete.setEnabled(true);
			}
		}
	}
}
