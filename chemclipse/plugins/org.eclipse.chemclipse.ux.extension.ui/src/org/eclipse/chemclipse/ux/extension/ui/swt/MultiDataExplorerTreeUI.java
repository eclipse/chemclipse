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
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.ui.provider.LazyFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerTreeUI.DataExplorerTreeRoot;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifierCache;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class MultiDataExplorerTreeUI {

	private static final DataExplorerTreeRoot[] DEFAULT_ROOTS = {DataExplorerTreeRoot.DRIVES, DataExplorerTreeRoot.HOME, DataExplorerTreeRoot.USER_LOCATION};
	private static final String TAB_KEY_SUFFIX = "selectedTab";
	private TabFolder tabFolder;
	private DataExplorerTreeUI[] treeUIs;
	private IPreferenceStore preferenceStore;
	private SupplierFileIdentifierCache identifierCache;

	public MultiDataExplorerTreeUI(Composite parent, IPreferenceStore preferenceStore) {
		this(parent, new SupplierFileIdentifierCache(LazyFileExplorerContentProvider.MAX_CACHE_SIZE), preferenceStore);
	}

	public MultiDataExplorerTreeUI(Composite parent, SupplierFileIdentifierCache identifierCache, IPreferenceStore preferenceStore) {
		this(parent, identifierCache, DEFAULT_ROOTS, preferenceStore);
	}

	public MultiDataExplorerTreeUI(Composite parent, SupplierFileIdentifierCache identifierCache, DataExplorerTreeRoot[] roots, IPreferenceStore preferenceStore) {
		this.identifierCache = identifierCache;
		this.preferenceStore = preferenceStore;
		tabFolder = new TabFolder(parent, SWT.NONE);
		treeUIs = new DataExplorerTreeUI[roots.length];
		for(int i = 0; i < roots.length; i++) {
			treeUIs[i] = createTab(tabFolder, roots[i]);
		}
	}

	protected Function<File, Collection<ISupplierFileIdentifier>> getIdentifierSupplier() {

		return identifierCache;
	}

	public void setSupplierFileIdentifier(Collection<? extends ISupplierFileIdentifier> supplierFileEditorSupportList) {

		identifierCache.setIdentifier(supplierFileEditorSupportList);
		for(DataExplorerTreeUI ui : treeUIs) {
			ui.getTreeViewer().refresh();
		}
	}

	public void expandLastDirectoryPath() {

		for(DataExplorerTreeUI ui : treeUIs) {
			String preferenceKey = getPreferenceKey(ui.getRoot());
			ui.expandLastDirectoryPath(preferenceStore, preferenceKey);
		}
		int index = preferenceStore.getInt(getSelectedTabPreferenceKey());
		tabFolder.setSelection(index);
	}

	public void saveLastDirectoryPath() {

		for(DataExplorerTreeUI ui : treeUIs) {
			ui.saveLastDirectoryPath(preferenceStore, getPreferenceKey(ui.getRoot()));
		}
		int index = tabFolder.getSelectionIndex();
		preferenceStore.setValue(getSelectedTabPreferenceKey(), index);
		if(preferenceStore.needsSaving()) {
			if(preferenceStore instanceof IPersistentPreferenceStore) {
				try {
					((IPersistentPreferenceStore)preferenceStore).save();
				} catch(IOException e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Storing preferences failed", e));
				}
			}
		}
	}

	protected String getSelectedTabPreferenceKey() {

		return getPreferenceKey(DataExplorerTreeRoot.USER_LOCATION) + TAB_KEY_SUFFIX;
	}

	protected String getUserLocationPreferenceKey() {

		return PreferenceConstants.P_USER_LOCATION_PATH;
	}

	protected String getPreferenceKey(DataExplorerTreeRoot root) {

		return DataExplorerTreeUI.getDefaultPathPreferenceKey(root);
	}

	private DataExplorerTreeUI createTab(TabFolder tabFolder, DataExplorerTreeRoot root) {

		TabItem tab = new TabItem(tabFolder, SWT.NONE);
		tab.setText(root.toString());
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		DataExplorerTreeUI treeUI = new DataExplorerTreeUI(composite, root, getIdentifierSupplier());
		TreeViewer treeViewer = treeUI.getTreeViewer();
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object[] array = treeViewer.getStructuredSelection().toArray();
				File[] files = new File[array.length];
				for(int i = 0; i < files.length; i++) {
					files[i] = (File)array[i];
				}
				handleSelection(files, treeUI);
			}
		};
		treeViewer.addSelectionChangedListener(selectionChangedListener);
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				File file = (File)((IStructuredSelection)event.getSelection()).getFirstElement();
				handleDoubleClick(file, treeUI);
			}
		});
		initTabComponent(composite, treeUI);
		tab.setControl(composite);
		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TabItem[] selection = tabFolder.getSelection();
				for(TabItem item : selection) {
					if(item == tab) {
						selectionChangedListener.selectionChanged(null);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		return treeUI;
	}

	protected void initTabComponent(Composite parent, DataExplorerTreeUI treeUI) {

		if(treeUI.getRoot() == DataExplorerTreeRoot.USER_LOCATION) {
			addUserLocationButton(parent, treeUI);
			File directory = new File(preferenceStore.getString(getUserLocationPreferenceKey()));
			if(directory.exists()) {
				treeUI.getTreeViewer().setInput(new File[]{directory});
			}
		}
	}

	private void addUserLocationButton(Composite parent, DataExplorerTreeUI treeUI) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Select User Location");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(e.display.getActiveShell(), SWT.READ_ONLY);
				directoryDialog.setText("Select a directory.");
				String pathname = directoryDialog.open();
				if(pathname != null) {
					File directory = new File(pathname);
					if(directory.exists()) {
						preferenceStore.setValue(getUserLocationPreferenceKey(), directory.getAbsolutePath());
						treeUI.getTreeViewer().setInput(new File[]{directory});
					}
				}
			}
		});
	}

	protected void handleDoubleClick(File file, DataExplorerTreeUI treeUI) {

	}

	protected void handleSelection(File[] files, DataExplorerTreeUI treeUI) {

	}

	public void setFocus() {

		tabFolder.setFocus();
		for(TabItem item : tabFolder.getSelection()) {
			item.getControl().setFocus();
		}
	}

	public Control getControl() {

		return tabFolder;
	}
}
