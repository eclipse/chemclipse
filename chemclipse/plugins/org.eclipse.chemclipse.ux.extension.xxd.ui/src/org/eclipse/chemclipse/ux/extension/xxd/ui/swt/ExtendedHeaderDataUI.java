/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
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
import org.eclipse.chemclipse.support.updates.IUpdateListener;
import org.eclipse.chemclipse.swt.ui.components.DataMapSupportUI;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ExtendedHeaderDataUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_HEADER_ENTRIES = "Header Entries";
	//
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarSearch = new AtomicReference<>();
	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarEdit = new AtomicReference<>();
	private AtomicReference<DataMapSupportUI> toolbarEdit = new AtomicReference<>();
	private AtomicReference<Button> buttonTableEdit = new AtomicReference<>();
	private AtomicReference<Button> buttonDelete = new AtomicReference<>();
	private AtomicReference<HeaderDataListUI> tableViewer = new AtomicReference<>();
	private AtomicReference<Text> miscellaneousControl = new AtomicReference<>();
	private AtomicReference<Control> findingsControl = new AtomicReference<>();
	//
	private IMeasurementInfo measurementInfo = null;

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

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch.get(), IMAGE_SEARCH, TOOLTIP_EDIT, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit.get(), IMAGE_EDIT, TOOLTIP_EDIT, false);
		enableEdit(tableViewer, buttonTableEdit.get(), IMAGE_EDIT_ENTRY, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createButtonToggleInfo(composite);
		createButtonToggleSearch(composite);
		createButtonToggleEdit(composite);
		createButtonToggleTableEdit(composite);
		createButtonDelete(composite);
	}

	private void createButtonToggleInfo(Composite parent) {

		buttonToolbarInfo.set(createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO));
	}

	private void createButtonToggleSearch(Composite parent) {

		buttonToolbarSearch.set(createButtonToggleToolbar(parent, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH));
	}

	private void createButtonToggleEdit(Composite parent) {

		buttonToolbarEdit.set(createButtonToggleToolbar(parent, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT));
	}

	private void createButtonToggleTableEdit(Composite parent) {

		buttonTableEdit.set(createButtonToggleEditTable(parent, tableViewer, IMAGE_EDIT_ENTRY));
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
				updateInfoToolbar();
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createToolbarEdit(Composite parent) {

		DataMapSupportUI headerMapSupportUI = new DataMapSupportUI(parent, SWT.NONE);
		headerMapSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		headerMapSupportUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateInput();
			}
		});
		//
		toolbarEdit.set(headerMapSupportUI);
	}

	private void createButtonDelete(Composite parent) {

		Button button = createButton(parent, "", "Delete the selected header entries.", IApplicationImage.IMAGE_DELETE);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteEntries(e.display.getActiveShell());
			}
		});
		//
		buttonDelete.set(button);
	}

	private void createTabFolderSection(Composite parent) {

		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateInput();
			}
		});
		/*
		 * Tabs
		 */
		createHeaderDataTable(tabFolder);
		createTextMiscellaneous(tabFolder);
		createTextFindings(tabFolder);
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
		 * Update Content
		 */
		headerDataListUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateInput();
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

	private void createTextMiscellaneous(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Miscellaneous");
		//
		Text text = new Text(tabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(measurementInfo != null) {
					measurementInfo.setMiscInfo(text.getText().trim());
				}
			}
		});
		//
		tabItem.setControl(text);
		miscellaneousControl.set(text);
	}

	private void createTextFindings(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Findings");
		//
		Control editor;
		boolean useRichTextEditor = isUseRichTextEditor();
		if(useRichTextEditor) {
			/*
			 * RTF
			 */
			RichTextEditor richTextEditor = new RichTextEditor(tabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
			richTextEditor.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {

					if(measurementInfo != null) {
						measurementInfo.setFindings(richTextEditor.getText().trim());
					}
				}
			});
			editor = richTextEditor;
		} else {
			/*
			 * ASCII
			 */
			Text plainTextEditor = new Text(tabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
			plainTextEditor.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {

					if(measurementInfo != null) {
						measurementInfo.setFindings(plainTextEditor.getText().trim());
					}
				}
			});
			editor = plainTextEditor;
		}
		//
		tabItem.setControl(editor);
		findingsControl.set(editor);
	}

	private boolean isUseRichTextEditor() {

		boolean useRichTextEditor = PreferenceSupplier.isHeaderDataUseRichTextEditor();
		if(useRichTextEditor) {
			/*
			 * TODO: Check if WebKit is available - otherwise disable the rich text option
			 * ---
			 * org.eclipse.swt.SWTError: No more handles because there is no underlying browser available.
			 * Please ensure that WebKit with its GTK 3.x/4.x bindings is installed.
			 */
		}
		//
		return useRichTextEditor;
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Header Entries";
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

	private void deleteEntries(Shell shell) {

		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Header Entries");
		messageBox.setMessage("Would you like to delete the selected header entries?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete
			 */
			if(measurementInfo != null) {
				Iterator<?> iterator = tableViewer.get().getStructuredSelection().iterator();
				Set<String> keysNotRemoved = new HashSet<>();
				while(iterator.hasNext()) {
					Object mapObject = iterator.next();
					if(mapObject instanceof Map.Entry<?, ?> entry) {
						String key = (String)entry.getKey();
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
				if(!keysNotRemoved.isEmpty()) {
					MessageDialog.openWarning(DisplayUtils.getShell(), DataMapSupportUI.HEADER_ENTRY, "The following keys can't be removed: " + keysNotRemoved);
				}
				//
				updateInput();
			}
		}
	}

	private void updateInput() {

		/*
		 * Refresh the edit toolbar.
		 */
		DataMapSupportUI dataMapSupportUI = toolbarEdit.get();
		if(dataMapSupportUI != null) {
			dataMapSupportUI.setInput(measurementInfo);
		}
		/*
		 * Refresh the data.
		 */
		updateData();
		updateInfoToolbar();
	}

	private void updateData() {

		enableButtonDelete();
		//
		if(measurementInfo != null) {
			/*
			 * Key Value Table
			 */
			int sizeEntries = measurementInfo.getHeaderDataMap().size();
			int selectionIndex = tableViewer.get().getTable().getSelectionIndex();
			tableViewer.get().setInput(measurementInfo);
			tableViewer.get().sortTable();
			if(selectionIndex >= 0 && selectionIndex < sizeEntries) {
				tableViewer.get().getTable().select(selectionIndex);
			}
			/*
			 * Tabs
			 */
			miscellaneousControl.get().setText(measurementInfo.getMiscInfo());
			updateFindings(measurementInfo.getFindings());
		} else {
			tableViewer.get().setInput(null);
			miscellaneousControl.get().setText("");
			updateFindings("");
		}
	}

	private void updateFindings(String content) {

		Control control = findingsControl.get();
		if(control instanceof RichTextEditor richTextEditor) {
			richTextEditor.setText(content);
		} else if(control instanceof Text plainTextEditor) {
			plainTextEditor.setText(content);
		}
	}

	private void updateInfoToolbar() {

		if(measurementInfo != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("Number Entries:");
			builder.append(" ");
			builder.append(tableViewer.get().getTable().getItems().length);
			builder.append(" / ");
			builder.append(measurementInfo.getHeaderDataMap().size());
			toolbarInfo.get().setText(builder.toString());
		} else {
			toolbarInfo.get().setText("--");
		}
	}

	private void enableButtonDelete() {

		buttonDelete.get().setEnabled(false);
		if(measurementInfo != null) {
			Object object = tableViewer.get().getStructuredSelection().getFirstElement();
			if(object instanceof Map.Entry<?, ?> entry) {
				boolean enabled = !measurementInfo.isKeyProtected(entry.getKey().toString());
				buttonDelete.get().setEnabled(enabled);
			}
		}
	}
}