/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
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
	private final List<ISeparationColumn> separationColumns = SeparationColumnFactory.getSeparationColumns();
	//
	private File retentionIndexFile;
	private ISeparationColumnIndices separationColumnIndices = null;

	public ExtendedRetentionIndexListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
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
			if(!separationColumns.contains(separationColumn)) {
				separationColumns.add(0, separationColumn);
			}
			comboViewerSeparationColumn.setInput(separationColumns);
			comboViewerSeparationColumn.setSelection(new StructuredSelection(separationColumn));
		} else {
			comboViewerSeparationColumn.getCombo().setItems(new String[]{});
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createToolbarMain(composite);
		toolbarInfoTop = createToolbarInfoTop(composite);
		retentionIndexUI = createRetentionIndexUI(composite);
		toolbarInfoBottom = createToolbarInfoBottom(composite);
		//
		initialize();
	}

	private void initialize() {

		comboViewerSeparationColumn.setInput(separationColumns);
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
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
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
		button.setToolTipText(ExtensionMessages.toggleInfoToolbar);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PartSupport.toggleCompositeVisibility(toolbarInfoTop);
				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfoBottom);
				button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImageProvider.SIZE_16x16, visible));
			}
		});
		//
		return button;
	}

	private ComboViewer createComboViewerSeparationColumn(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn separationColumn) {
					return SeparationColumnFactory.getColumnLabel(separationColumn, 25);
				}
				return null;
			}
		});
		//
		combo.setToolTipText(ExtensionMessages.selectChromatogramColumn);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn separationColumn && separationColumnIndices != null) {
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
		button.setToolTipText(ExtensionMessages.toggleSearchToolbar);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = retentionIndexUI.toggleSearchVisibility();
				button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImageProvider.SIZE_16x16, visible));
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarModify(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(ExtensionMessages.toggleModifyToolbar);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = retentionIndexUI.toggleEditVisibility();
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ACTIVE, IApplicationImageProvider.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImageProvider.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(ExtensionMessages.toggleEditToolbar);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				retentionIndexUI.toggleTableEdit();
				button.setImage(ApplicationImageFactory.getInstance().getImage((retentionIndexUI.isEnabled()) ? IApplicationImage.IMAGE_EDIT_ENTRY_ACTIVE : IApplicationImage.IMAGE_EDIT_ENTRY, IApplicationImageProvider.SIZE_16x16));
				updateLabel();
			}
		});
		//
		return button;
	}

	private Button createButtonAddLibraryToProcess(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(ExtensionMessages.addLibraryToSearch);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_RI_LIBRARY_ADD_ADD_TO_PROCESS, retentionIndexFile);
				MessageDialog.openConfirm(e.display.getActiveShell(), ExtensionMessages.retentionIndexCalculator, ExtensionMessages.retentionIndexLibraryAdded);
			}
		});
		//
		return button;
	}

	private Button createButtonRemoveLibraryFromProcess(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(ExtensionMessages.removeLibraryFromSearch);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REMOVE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_RI_LIBRARY_REMOVE_FROM_PROCESS, retentionIndexFile);
				MessageDialog.openConfirm(e.display.getActiveShell(), ExtensionMessages.retentionIndexCalculator, ExtensionMessages.retentionIndexLibraryRemoved);
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
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoTop = new Label(composite, SWT.NONE);
		labelInfoTop.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
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
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfoBottom = new Label(composite, SWT.NONE);
		labelInfoBottom.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		labelInfoBottom.setText("");
		labelInfoBottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private void updateLabel() {

		/*
		 * Separation Column Details
		 */
		Object object = comboViewerSeparationColumn.getStructuredSelection().getFirstElement();
		StringBuilder builder = new StringBuilder();
		if(object instanceof ISeparationColumn separationColumn) {
			builder.append("Separation Column: ");
			builder.append(separationColumn.getName());
			builder.append(" ");
			builder.append("[");
			builder.append(getSeparationColumnDetails(separationColumn));
			builder.append("]");
		}
		/*
		 * Top/Bottom Label
		 */
		RetentionIndexTableViewerUI tableViewer = getRetentionIndexTableViewerUI();
		labelInfoTop.setText(builder.toString());
		String editInformation = getRetentionIndexTableViewerUI().isEditEnabled() ? ExtensionMessages.editEnabled : ExtensionMessages.editDisabled;
		labelInfoBottom.setText("Retention Indices: " + tableViewer.getTable().getItemCount() + " [" + retentionIndexUI.getSearchText() + "] - " + editInformation);
	}

	private String getSeparationColumnDetails(ISeparationColumn separationColumn) {

		StringBuilder builder = new StringBuilder();
		//
		builder.append(separationColumn.getSeparationColumnType().label());
		builder.append(" ");
		builder.append(separationColumn.getLength());
		builder.append(" ");
		builder.append(separationColumn.getDiameter());
		builder.append(" ");
		builder.append(separationColumn.getPhase());
		//
		return builder.toString().trim();
	}

	private void applySettings() {

	}
}
