/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnIndices;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ExtendedRetentionIndexListUI extends Composite {

	private static final Logger logger = Logger.getLogger(ExtendedRetentionIndexListUI.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private Composite toolbarInfoTop;
	private Composite toolbarSearch;
	private Composite toolbarModify;
	private Composite toolbarInfoBottom;
	//
	private Label labelInfoTop;
	private Label labelInfoBottom;
	private SearchSupportUI searchSupportUI;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private Button buttonAddLibrary;
	private Button buttonRemoveLibrary;
	//
	private ComboViewer comboViewerSeparationColumn;
	private Combo comboReferences;
	private Button buttonAddReference;
	private Text textRetentionTime;
	private Text textRetentionIndex;
	//
	private RetentionIndexTableViewerUI retentionIndexListUI;
	//
	private File retentionIndexFile;
	private ISeparationColumnIndices separationColumnIndices = new SeparationColumnIndices();
	private List<IRetentionIndexEntry> defaultRetentionIndexEntries;

	public ExtendedRetentionIndexListUI(Composite parent, int style) {
		super(parent, style);
		defaultRetentionIndexEntries = new ArrayList<>();
		initialize();
	}

	public void setDefaultRetentionIndexEntries(List<IRetentionIndexEntry> defaultRetentionIndexEntries) {

		this.defaultRetentionIndexEntries = defaultRetentionIndexEntries;
	}

	public void setFile(File file) {

		retentionIndexFile = file;
		boolean enabled = (file != null);
		buttonAddLibrary.setEnabled(enabled);
		buttonRemoveLibrary.setEnabled(enabled);
	}

	public void setInput(ISeparationColumnIndices separationColumnIndices) {

		this.separationColumnIndices = separationColumnIndices;
		ISeparationColumn separationColumn = separationColumnIndices.getSeparationColumn();
		setSeparationColumnSelection(separationColumn);
		retentionIndexListUI.setInput(separationColumnIndices);
		updateLabel();
	}

	public RetentionIndexTableViewerUI getRetentionIndexTableViewerUI() {

		return retentionIndexListUI;
	}

	private void setSeparationColumnSelection(ISeparationColumn separationColumn) {

		if(separationColumn != null) {
			String name = separationColumn.getName();
			int index = -1;
			exitloop:
			for(String item : comboViewerSeparationColumn.getCombo().getItems()) {
				index++;
				if(item.equals(name)) {
					break exitloop;
				}
			}
			//
			if(index >= 0) {
				comboViewerSeparationColumn.getCombo().select(index);
			}
		}
	}

	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createToolbarMain(composite);
		toolbarInfoTop = createToolbarInfoTop(composite);
		toolbarSearch = createToolbarSearch(composite);
		toolbarModify = createToolbarModify(composite);
		retentionIndexListUI = createTableField(composite);
		toolbarInfoBottom = createToolbarInfoBottom(composite);
		//
		comboViewerSeparationColumn.setInput(SeparationColumnFactory.getSeparationColumns());
		retentionIndexListUI.setEditEnabled(false);
		buttonAddLibrary.setEnabled(false);
		buttonRemoveLibrary.setEnabled(false);
		//
		PartSupport.setCompositeVisibility(toolbarInfoTop, true);
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarModify, false);
		PartSupport.setCompositeVisibility(toolbarInfoBottom, true);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(8, false));
		//
		createButtonToggleToolbarInfo(composite);
		comboViewerSeparationColumn = createComboViewerSeparationColumn(composite);
		createButtonToggleToolbarSearch(composite);
		createButtonToggleToolbarModify(composite);
		createButtonToggleToolbarEdit(composite);
		buttonAddLibrary = createButtonAddLibraryToProcess(composite);
		buttonRemoveLibrary = createButtonRemoveLibraryFromProcess(composite);
		createSettingsButton(composite);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.toggleCompositeVisibility(toolbarInfoTop);
				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfoBottom);
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

	private ComboViewer createComboViewerSeparationColumn(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn) {
					ISeparationColumn separationColumn = (ISeparationColumn)element;
					return separationColumn.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a chromatogram column.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn && separationColumnIndices != null) {
					ISeparationColumn separationColumn = (ISeparationColumn)object;
					separationColumnIndices.setSeparationColumn(separationColumn);
					updateLabel();
				}
			}
		});
		//
		return comboViewer;
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

	private Button createButtonToggleToolbarEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Enable/disable to edit the table.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !retentionIndexListUI.isEditEnabled();
				retentionIndexListUI.setEditEnabled(editEnabled);
				updateLabel();
			}
		});
		//
		return button;
	}

	private Button createButtonAddLibraryToProcess(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add the library to the list of searched databases.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				eventBroker.post(IChemClipseEvents.TOPIC_RI_LIBRARY_ADD_ADD_TO_PROCESS, retentionIndexFile);
				MessageDialog.openConfirm(e.widget.getDisplay().getActiveShell(), "RI Calculator", "The RI library has been added.");
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveLibraryFromProcess(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove the library from the list of searched databases.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REMOVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
				eventBroker.post(IChemClipseEvents.TOPIC_RI_LIBRARY_REMOVE_FROM_PROCESS, retentionIndexFile);
				MessageDialog.openConfirm(e.widget.getDisplay().getActiveShell(), "RI Calculator", "The RI library has been removed.");
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				IPreferencePage preferencePageMSD = new PreferencePage();
				preferencePageMSD.setTitle("Settings (MSD)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageSWT));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageMSD));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.widget.getDisplay().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarInfoTop(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoTop = new Label(composite, SWT.NONE);
		labelInfoTop.setBackground(Colors.WHITE);
		labelInfoTop.setText("");
		labelInfoTop.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarSearch(Composite parent) {

		searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setBackground(Colors.WHITE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				retentionIndexListUI.setSearchText(searchText, caseSensitive);
				updateLabel();
			}
		});
		//
		return searchSupportUI;
	}

	private Composite createToolbarModify(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(9, false));
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridDataComposite);
		//
		createComboReferences(composite);
		createLabelRetentionTime(composite);
		createTextRetentionTime(composite);
		createLabelRetentionIndex(composite);
		createTextRetentionIndex(composite);
		createButtonAddReference(composite);
		createButtonCancel(composite);
		createButtonDelete(composite);
		createButtonAdd(composite);
		//
		return composite;
	}

	private void createButtonCancel(Composite parent) {

		buttonCancel = new Button(parent, SWT.PUSH);
		buttonCancel.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				comboReferences.setText("");
				textRetentionTime.setText("");
				enableButtonFields(ACTION_CANCEL);
			}
		});
	}

	private void createButtonDelete(Composite parent) {

		buttonDelete = new Button(parent, SWT.PUSH);
		buttonDelete.setEnabled(false);
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = retentionIndexListUI.getTable();
				int index = table.getSelectionIndex();
				if(index >= 0) {
					MessageBox messageBox = new MessageBox(e.widget.getDisplay().getActiveShell(), SWT.ICON_WARNING);
					messageBox.setText("Delete reference(s)?");
					messageBox.setMessage("Would you like to delete the reference(s)?");
					if(messageBox.open() == SWT.OK) {
						//
						enableButtonFields(ACTION_DELETE);
						TableItem[] tableItems = table.getSelection();
						for(TableItem tableItem : tableItems) {
							Object object = tableItem.getData();
							if(object instanceof IRetentionIndexEntry) {
								IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)object;
								separationColumnIndices.remove(retentionIndexEntry);
							}
						}
						retentionIndexListUI.setInput(separationColumnIndices);
					}
				}
			}
		});
	}

	private void createButtonAdd(Composite parent) {

		buttonAdd = new Button(parent, SWT.PUSH);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_ADD);
			}
		});
	}

	private void createComboReferences(Composite parent) {

		comboReferences = new Combo(parent, SWT.BORDER);
		comboReferences.setText("");
		comboReferences.setItems(getAvailableStandards());
		comboReferences.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String name = comboReferences.getText().trim();
				IRetentionIndexEntry retentionIndexEntry = getRetentionIndexEntry(name);
				if(retentionIndexEntry != null) {
					textRetentionIndex.setText(Float.toString(retentionIndexEntry.getRetentionIndex()));
				} else {
					textRetentionIndex.setText("");
				}
			}
		});
	}

	private void createLabelRetentionTime(Composite parent) {

		Label labelRetentionTime = new Label(parent, SWT.NONE);
		labelRetentionTime.setText("RT:");
	}

	private void createTextRetentionTime(Composite parent) {

		textRetentionTime = new Text(parent, SWT.BORDER);
		textRetentionTime.setText("");
		textRetentionTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createLabelRetentionIndex(Composite parent) {

		Label labelRetentionIndex = new Label(parent, SWT.NONE);
		labelRetentionIndex.setText("RI:");
	}

	private void createTextRetentionIndex(Composite parent) {

		textRetentionIndex = new Text(parent, SWT.BORDER);
		textRetentionIndex.setText("");
		textRetentionIndex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonAddReference(Composite parent) {

		buttonAddReference = new Button(parent, SWT.PUSH);
		buttonAddReference.setText("");
		buttonAddReference.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE_ADD, IApplicationImage.SIZE_16x16));
		buttonAddReference.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonAddReference.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					enableButtonFields(ACTION_INITIALIZE);
					//
					String name = comboReferences.getText().trim();
					int retentionTime = (int)(Double.parseDouble(textRetentionTime.getText().trim()) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					float retentionIndex;
					String retentionIndexText = textRetentionIndex.getText().trim();
					if(retentionIndexText.equals("")) {
						IRetentionIndexEntry retentionIndexEntry = getRetentionIndexEntry(name);
						if(retentionIndexEntry != null) {
							retentionIndex = retentionIndexEntry.getRetentionIndex();
						} else {
							retentionIndex = 0.0f;
						}
					} else {
						retentionIndex = Float.parseFloat(retentionIndexText);
					}
					//
					comboReferences.setText("");
					textRetentionTime.setText("");
					textRetentionIndex.setText("");
					//
					IRetentionIndexEntry retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
					separationColumnIndices.put(retentionIndexEntry);
					retentionIndexListUI.setInput(separationColumnIndices);
				} catch(Exception e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private RetentionIndexTableViewerUI createTableField(Composite composite) {

		RetentionIndexTableViewerUI tableViewer = new RetentionIndexTableViewerUI(composite, SWT.BORDER | SWT.MULTI);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
			}
		});
		//
		return tableViewer;
	}

	private Composite createToolbarInfoBottom(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoBottom = new Label(composite, SWT.NONE);
		labelInfoBottom.setBackground(Colors.WHITE);
		labelInfoBottom.setText("");
		labelInfoBottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private void enableButtonFields(String action) {

		enableFields(false);
		switch(action) {
			case ACTION_INITIALIZE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_CANCEL:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_ADD:
				buttonCancel.setEnabled(true);
				comboReferences.setEnabled(true);
				textRetentionTime.setEnabled(true);
				textRetentionIndex.setEnabled(true);
				buttonAddReference.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				if(retentionIndexListUI.getTable().getSelectionIndex() >= 0) {
					buttonDelete.setEnabled(true);
				} else {
					buttonDelete.setEnabled(false);
				}
				break;
		}
	}

	private void enableFields(boolean enabled) {

		buttonCancel.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		//
		comboReferences.setEnabled(enabled);
		textRetentionTime.setEnabled(enabled);
		textRetentionIndex.setEnabled(enabled);
		buttonAddReference.setEnabled(enabled);
	}

	private void updateLabel() {

		Object object = comboViewerSeparationColumn.getStructuredSelection().getFirstElement();
		StringBuilder builder = new StringBuilder();
		if(object instanceof ISeparationColumn) {
			ISeparationColumn separationColumn = (ISeparationColumn)object;
			builder.append(separationColumn.getName());
			builder.append(" ");
			builder.append(separationColumn.getLength());
			builder.append(" ");
			builder.append(separationColumn.getDiameter());
			builder.append(" ");
			builder.append(separationColumn.getPhase());
		}
		//
		labelInfoTop.setText("Separation Column: " + builder.toString());
		String filterInformation = "[" + searchSupportUI.getSearchText() + "]";
		String editInformation = retentionIndexListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
		labelInfoBottom.setText("Retention Indices: " + getItemSize() + " " + filterInformation + " - " + editInformation);
	}

	private int getItemSize() {

		return retentionIndexListUI.getTable().getItemCount();
	}

	private void applySettings() {

	}

	private String[] getAvailableStandards() {

		int size = defaultRetentionIndexEntries.size();
		String[] availableStandards = new String[size];
		for(int i = 0; i < size; i++) {
			availableStandards[i] = defaultRetentionIndexEntries.get(i).getName();
		}
		return availableStandards;
	}

	private IRetentionIndexEntry getRetentionIndexEntry(String name) {

		for(IRetentionIndexEntry retentionIndexEntry : defaultRetentionIndexEntries) {
			if(retentionIndexEntry.getName().equals(name)) {
				return retentionIndexEntry;
			}
		}
		return null;
	}
}
