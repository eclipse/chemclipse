/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.calibration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ExtendedRetentionIndexListUI extends Composite implements IExtendedPartUI {

	private Composite toolbarInfoTop;
	private RetentionIndexUI retentionIndexUI;
	private Composite toolbarInfoBottom;
	//
	private Label labelInfoTop;
	private Label labelInfoBottom;
	//
	private Button buttonAddLibrary;
	private Button buttonRemoveLibrary;
	//
	private ComboViewer comboViewerSeparationColumn;
	//
	private File retentionIndexFile;
	private ISeparationColumnIndices separationColumnIndices = null;

	public ExtendedRetentionIndexListUI(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void setInput(List<IRetentionIndexEntry> retentionIndexEntries) {

		retentionIndexUI.addRetentionIndexEntries(retentionIndexEntries);
	}

	public void setFile(File file) {

		retentionIndexFile = file;
		boolean enabled = (file != null);
		buttonAddLibrary.setEnabled(enabled);
		buttonRemoveLibrary.setEnabled(enabled);
	}

	public void setInput(ISeparationColumnIndices separationColumnIndices) {

		this.separationColumnIndices = separationColumnIndices;
		retentionIndexUI.setInput(separationColumnIndices);
		ISeparationColumn separationColumn = null;
		if(this.separationColumnIndices != null) {
			separationColumn = separationColumnIndices.getSeparationColumn();
		}
		setSeparationColumnSelection(separationColumn);
		updateLabel();
	}

	public RetentionIndexTableViewerUI getRetentionIndexTableViewerUI() {

		return retentionIndexUI.getRetentionIndexTableViewerUI();
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
		} else {
			comboViewerSeparationColumn.getCombo().setItems(new String[]{});
		}
	}

	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createToolbarMain(composite);
		toolbarInfoTop = createToolbarInfoTop(composite);
		retentionIndexUI = createRetentionIndexUI(composite);
		toolbarInfoBottom = createToolbarInfoBottom(composite);
		//
		comboViewerSeparationColumn.setInput(SeparationColumnFactory.getSeparationColumns());
		buttonAddLibrary.setEnabled(false);
		buttonRemoveLibrary.setEnabled(false);
		//
		PartSupport.setCompositeVisibility(toolbarInfoTop, true);
		PartSupport.setCompositeVisibility(toolbarInfoBottom, true);
		//
		retentionIndexUI.setSearchVisibility(false);
		retentionIndexUI.setEditVisibility(false);
		retentionIndexUI.enableTableEdit(false);
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
		//
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

				boolean visible = retentionIndexUI.toggleSearchVisibility();
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = retentionIndexUI.toggleEditVisibility();
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImage.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				retentionIndexUI.toggleTableEdit();
				button.setImage(ApplicationImageFactory.getInstance().getImage((retentionIndexUI.isEnabled()) ? IApplicationImage.IMAGE_EDIT_ENTRY_ACTIVE : IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImage.SIZE_16x16));
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

				UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_RI_LIBRARY_ADD_ADD_TO_PROCESS, retentionIndexFile);
				MessageDialog.openConfirm(e.display.getActiveShell(), "RI Calculator", "The RI library has been added.");
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

				UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_RI_LIBRARY_REMOVE_FROM_PROCESS, retentionIndexFile);
				MessageDialog.openConfirm(e.display.getActiveShell(), "RI Calculator", "The RI library has been removed.");
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageSystem.class, PreferencePage.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
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

	private RetentionIndexUI createRetentionIndexUI(Composite parent) {

		RetentionIndexUI retentionIndexUI = new RetentionIndexUI(parent, SWT.NONE);
		retentionIndexUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		retentionIndexUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update(Display display) {

				updateLabel();
			}
		});
		return retentionIndexUI;
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
		RetentionIndexTableViewerUI tableViewer = getRetentionIndexTableViewerUI();
		labelInfoTop.setText("Separation Column: " + builder.toString());
		String editInformation = getRetentionIndexTableViewerUI().isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
		labelInfoBottom.setText("Retention Indices: " + tableViewer.getTable().getItemCount() + " [" + retentionIndexUI.getSearchText() + "] - " + editInformation);
	}

	private void applySettings() {

	}
}
