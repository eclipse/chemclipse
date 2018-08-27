/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import javax.inject.Inject;

import org.eclipse.chemclipse.model.exceptions.InvalidHeaderModificationException;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class ExtendedWellDataUI {

	private static final String MENU_CATEGORY_HEADER_ENTRIES = "Header Entries";
	private static final String HEADER_ENTRY = "Header Entry";
	//
	private Label labelInfoTop;
	private Composite toolbarInfoTop;
	private Label labelInfoBottom;
	private Composite toolbarInfoBottom;
	private Composite toolbarSearch;
	private Composite toolbarModify;
	private Button buttonToggleEditModus;
	private Text textHeaderKey;
	private Text textHeaderValue;
	private Button buttonAddHeaderEntry;
	private Button buttonDeleteHeaderEntry;
	private WellDataListUI wellDataListUI;
	//
	private IWell well;
	private boolean editable;

	@Inject
	public ExtendedWellDataUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateHeaderData();
	}

	public void update(IWell well) {

		this.well = well;
		this.editable = (well != null);
		updateHeaderData();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfoTop = createToolbarInfoTop(parent);
		toolbarSearch = createToolbarSearch(parent);
		toolbarModify = createToolbarModify(parent);
		wellDataListUI = createWellDataTable(parent);
		toolbarInfoBottom = createToolbarInfoBottom(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfoTop, true);
		PartSupport.setCompositeVisibility(toolbarInfoBottom, true);
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarModify, false);
		//
		wellDataListUI.setEditEnabled(false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarSearch(composite);
		createButtonToggleToolbarModify(composite);
		buttonToggleEditModus = createButtonToggleEditModus(composite);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfoTop);
				PartSupport.toggleCompositeVisibility(toolbarInfoBottom);
				//
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle search toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSearch);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarModify(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle modify toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarModify);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleEditModus(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Enable/disable to edit the table.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !wellDataListUI.isEditEnabled();
				wellDataListUI.setEditEnabled(editEnabled);
				updateLabel();
			}
		});
		//
		return button;
	}

	private Composite createToolbarInfoTop(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoTop = new Label(composite, SWT.NONE);
		labelInfoTop.setText("");
		labelInfoTop.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarInfoBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoBottom = new Label(composite, SWT.NONE);
		labelInfoBottom.setText("");
		labelInfoBottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				wellDataListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Composite createToolbarModify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		textHeaderKey = createTextHeaderKey(composite);
		textHeaderValue = createTextHeaderValue(composite);
		buttonAddHeaderEntry = createButtonAdd(composite);
		buttonDeleteHeaderEntry = createButtonDelete(composite);
		//
		return composite;
	}

	private Text createTextHeaderKey(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Set a new data key.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private Text createTextHeaderValue(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Set a new data value.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the data entry.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addHeaderEntry();
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected entrie(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteHeaderEntries();
			}
		});
		return button;
	}

	private WellDataListUI createWellDataTable(Composite parent) {

		WellDataListUI listUI = new WellDataListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		listUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Add the delete targets support.
		 */
		ITableSettings tableSettings = listUI.getTableSettings();
		addDeleteMenuEntry(tableSettings);
		listUI.applySettings(tableSettings);
		//
		return listUI;
	}

	private void addDeleteMenuEntry(ITableSettings tableSettings) {

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

				deleteHeaderEntries();
			}
		});
	}

	private void addHeaderEntry() {

		if(well != null) {
			String key = textHeaderKey.getText().trim();
			String value = textHeaderValue.getText().trim();
			//
			if("".equals(key)) {
				MessageDialog.openError(DisplayUtils.getShell(), HEADER_ENTRY, "The data key must be not empty.");
			} else if(well.getData().containsKey(key)) {
				MessageDialog.openError(DisplayUtils.getShell(), HEADER_ENTRY, "The data key already exists.");
			} else if("".equals(value)) {
				MessageDialog.openError(DisplayUtils.getShell(), HEADER_ENTRY, "The data value must be not empty.");
			} else {
				well.setData(key, value);
				textHeaderKey.setText("");
				textHeaderValue.setText("");
				updateHeaderData();
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void deleteHeaderEntries() {

		MessageBox messageBox = new MessageBox(DisplayUtils.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Data Entrie(s)");
		messageBox.setMessage("Would you like to delete the selected data entrie(s)?");
		if(messageBox.open() == SWT.YES) {
			if(well != null) {
				Iterator iterator = wellDataListUI.getStructuredSelection().iterator();
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
				updateHeaderData();
			}
		}
	}

	private void updateHeaderData() {

		updateWidgets();
		updateLabel();
		//
		wellDataListUI.sortTable();
		Table table = wellDataListUI.getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
		}
	}

	private void updateLabel() {

		if(well != null) {
			String text = "Position: " + (well.getPosition().getId() + 1) + " | Id:" + well.getSampleId();
			String editInformation = wellDataListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
			//
			labelInfoTop.setText(text + " - " + editInformation);
			labelInfoBottom.setText("Number of Entries: " + well.getData().size());
			wellDataListUI.setInput(well);
		} else {
			labelInfoTop.setText("");
			labelInfoBottom.setText("");
			wellDataListUI.setInput(null);
		}
	}

	private void updateWidgets() {

		boolean enabled = editable;
		//
		buttonToggleEditModus.setEnabled(enabled);
		textHeaderKey.setEnabled(enabled);
		textHeaderValue.setEnabled(enabled);
		buttonAddHeaderEntry.setEnabled(enabled);
		buttonDeleteHeaderEntry.setEnabled(enabled);
	}
}
