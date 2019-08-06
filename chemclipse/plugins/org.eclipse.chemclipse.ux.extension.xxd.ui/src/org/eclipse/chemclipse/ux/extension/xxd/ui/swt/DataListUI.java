/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.columns.SimpleColumnDefinition;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class DataListUI implements ConfigurableUI<DataListUIConfig> {

	private ExtendedTableViewer tableViewer;
	private List<File> files = new ArrayList<File>();
	private Consumer<Boolean> dirtyListener;
	private Composite control;
	private InputWizardSettings inputWizardSettings;
	private Composite toolbarMain;

	public DataListUI(Composite parent, Consumer<Boolean> dirtyListener, IPreferenceStore preferenceStore, String userPathKey, DataType... dataTypes) {
		this.dirtyListener = dirtyListener;
		inputWizardSettings = InputWizardSettings.create(preferenceStore, userPathKey, dataTypes);
		inputWizardSettings.setTitle("Select files");
		inputWizardSettings.setDescription("Select items(s) to process.");
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(1, true));
		createToolbarTop(control);
		createTableViewer(control);
		createToolbarBottom(control);
	}

	private void createTableViewer(Composite composite) {

		tableViewer = new ExtendedTableViewer(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(files);
		DataExplorerLabelProvider dataExplorerLabelProvider = new DataExplorerLabelProvider(inputWizardSettings.getSupplierCache());
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				dataExplorerLabelProvider.dispose();
			}
		});
		tableViewer.addColumn(new SimpleColumnDefinition<>("Name", 250, new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof File) {
					return getName((File)element);
				}
				return "-";
			}

			@Override
			public Image getImage(Object element) {

				Image image = dataExplorerLabelProvider.getImage(element);
				return image;
			}
		}, null, this::getName));
		tableViewer.addColumn(new SimpleColumnDefinition<>("Path", 300, new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof File) {
					return getPath((File)element);
				}
				return "-";
			}
		}, null, this::getPath));
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createToolbarTop(Composite parent) {

		toolbarMain = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		toolbarMain.setLayoutData(gridData);
		toolbarMain.setLayout(new GridLayout(1, false));
		Button button = new Button(toolbarMain, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = inputWizardSettings.getPreferencePage();
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				preferenceDialog.open();
			}
		});
	}

	private void createToolbarBottom(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		toolBar.setLayoutData(gridData);
		createMoveUpButton(toolBar);
		createMoveDownButton(toolBar);
		createRemoveButton(toolBar);
		createAddButton(toolBar);
	}

	private ToolItem createMoveUpButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the item(s) up.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = tableViewer.getTable();
				ISelection selection = tableViewer.getSelection();
				for(int index : table.getSelectionIndices()) {
					Collections.swap(files, index, index - 1);
				}
				updateList(true);
				tableViewer.setSelection(selection);
			}
		});
		//
		return item;
	}

	private ToolItem createMoveDownButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the item(s) down.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = tableViewer.getTable();
				int[] indices = table.getSelectionIndices();
				ISelection selection = tableViewer.getSelection();
				for(int i = indices.length - 1; i >= 0; i--) {
					int index = indices[i];
					Collections.swap(files, index, index + 1);
				}
				updateList(true);
				tableViewer.setSelection(selection);
			}
		});
		return item;
	}

	private ToolItem createRemoveButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Remove the selected item(s).");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(Object object : tableViewer.getStructuredSelection().toArray()) {
					files.remove(object);
				}
				tableViewer.setSelection(StructuredSelection.EMPTY);
				updateList(true);
			}
		});
		return item;
	}

	private ToolItem createAddButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Add item(s) to the list.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Set<File> newFiles = InputEntriesWizard.openWizard(getControl().getShell(), inputWizardSettings).keySet();
				if(!newFiles.isEmpty()) {
					files.addAll(newFiles);
					updateList(true);
				}
			}
		});
		return item;
	}

	/**
	 * set the initial file list for this control and clears the dirty flag
	 * 
	 * @param files
	 */
	public void setFiles(Collection<File> files) {

		this.files.clear();
		if(files != null) {
			this.files.addAll(files);
		}
		updateList(false);
	}

	private void updateList(boolean dirtyStatus) {

		dirtyListener.accept(dirtyStatus);
		tableViewer.refresh();
	}

	public String getPath(File file) {

		return file.getAbsolutePath();
	}

	public String getName(File file) {

		if(file.exists()) {
			return file.getName();
		} else {
			return "File doesn't exist.";
		}
	}

	public Control getControl() {

		return control;
	}

	public List<File> getFiles() {

		return Collections.unmodifiableList(files);
	}

	@Override
	public DataListUIConfig getConfig() {

		return new DataListUIConfig() {

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.isVisible();
			}

			@Override
			public IPreferencePage[] getPreferencePages() {

				return new IPreferencePage[]{inputWizardSettings.getPreferencePage()};
			}

			@Override
			public void applySettings() {

				// nothing to do
			}
		};
	}
}
