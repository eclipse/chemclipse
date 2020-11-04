/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.ui.provider.LazyFileExplorerContentProvider;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class MultiDataExplorerTreeUI {

	private static final String TAB_KEY_SUFFIX = "selectedTab";
	//
	private final TabFolder tabFolder;
	private final DataExplorerTreeUI[] dataExplorerTreeUIs;
	private final IPreferenceStore preferenceStore;
	//
	private final SupplierFileIdentifierCache supplierFileIdentifierCache = new SupplierFileIdentifierCache(LazyFileExplorerContentProvider.MAX_CACHE_SIZE);

	public MultiDataExplorerTreeUI(Composite parent, IPreferenceStore preferenceStore) {

		this(parent, DataExplorerTreeRoot.getDefaultRoots(), preferenceStore);
	}

	public MultiDataExplorerTreeUI(Composite parent, DataExplorerTreeRoot[] roots, IPreferenceStore preferenceStore) {

		this.preferenceStore = preferenceStore;
		//
		tabFolder = new TabFolder(parent, SWT.NONE);
		dataExplorerTreeUIs = new DataExplorerTreeUI[roots.length];
		for(int i = 0; i < roots.length; i++) {
			dataExplorerTreeUIs[i] = createDataExplorerTreeUI(tabFolder, roots[i]);
		}
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

	public void setSupplierFileIdentifier(Collection<? extends ISupplierFileIdentifier> supplierFileEditorSupportList) {

		supplierFileIdentifierCache.setIdentifier(supplierFileEditorSupportList);
		for(DataExplorerTreeUI dataExplorerTreeUI : dataExplorerTreeUIs) {
			dataExplorerTreeUI.getTreeViewer().refresh();
		}
	}

	public void expandLastDirectoryPath() {

		for(DataExplorerTreeUI dataExplorerTreeUI : dataExplorerTreeUIs) {
			String preferenceKey = getPreferenceKey(dataExplorerTreeUI.getRoot());
			dataExplorerTreeUI.expandLastDirectoryPath(preferenceStore, preferenceKey);
		}
	}

	public void saveLastDirectoryPath() {

		for(DataExplorerTreeUI dataExplorerTreeUI : dataExplorerTreeUIs) {
			dataExplorerTreeUI.saveLastDirectoryPath(preferenceStore, getPreferenceKey(dataExplorerTreeUI.getRoot()));
		}
		//
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

	protected Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> getIdentifierSupplier() {

		return supplierFileIdentifierCache;
	}

	protected void handleDoubleClick(File file, DataExplorerTreeUI treeUI) {

	}

	protected void handleSelection(File[] files, DataExplorerTreeUI treeUI) {

	}

	protected String getSelectedTabPreferenceKey() {

		return getPreferenceKey(DataExplorerTreeRoot.USER_LOCATION) + TAB_KEY_SUFFIX;
	}

	protected String getUserLocationPreferenceKey() {

		return PreferenceConstants.P_USER_LOCATION_PATH;
	}

	protected String getPreferenceKey(DataExplorerTreeRoot root) {

		return root.getPreferenceKeyDefaultPath();
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

	private DataExplorerTreeUI createDataExplorerTreeUI(TabFolder tabFolder, DataExplorerTreeRoot root) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(root.toString());
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		//
		DataExplorerTreeUI dataExplorerTreeUI = new DataExplorerTreeUI(composite, root, getIdentifierSupplier());
		TreeViewer treeViewer = dataExplorerTreeUI.getTreeViewer();
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object[] array = treeViewer.getStructuredSelection().toArray();
				File[] files = new File[array.length];
				for(int i = 0; i < files.length; i++) {
					files[i] = (File)array[i];
				}
				handleSelection(files, dataExplorerTreeUI);
			}
		};
		//
		treeViewer.addSelectionChangedListener(selectionChangedListener);
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				File file = (File)((IStructuredSelection)event.getSelection()).getFirstElement();
				handleDoubleClick(file, dataExplorerTreeUI);
			}
		});
		//
		initTabComponent(composite, dataExplorerTreeUI);
		tabItem.setControl(composite);
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TabItem[] selection = tabFolder.getSelection();
				for(TabItem item : selection) {
					if(item == tabItem) {
						selectionChangedListener.selectionChanged(null);
					}
				}
			}
		});
		//
		return dataExplorerTreeUI;
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
}
