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
import java.util.Collection;

import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerTreeUI.DataExplorerTreeRoot;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class MultiDataExplorerTreeUI {

	private static final DataExplorerTreeRoot[] DEFAULT_ROOTS = {DataExplorerTreeRoot.DRIVES, DataExplorerTreeRoot.HOME, DataExplorerTreeRoot.USER_LOCATION};
	private static final String DEFAULT_KEY = "MultiDataExplorerTreeUI.selectedTab";
	private TabFolder tabFolder;
	private DataExplorerTreeUI[] treeUIs;
	private IPreferenceStore preferenceStore;
	private String preferenceKey;

	public MultiDataExplorerTreeUI(Composite parent, IPreferenceStore preferenceStore) {
		this(parent, DEFAULT_ROOTS, preferenceStore, DEFAULT_KEY);
	}

	public MultiDataExplorerTreeUI(Composite parent, DataExplorerTreeRoot[] roots, IPreferenceStore preferenceStore, String preferenceKey) {
		this.preferenceStore = preferenceStore;
		this.preferenceKey = preferenceKey;
		tabFolder = new TabFolder(parent, SWT.NONE);
		treeUIs = new DataExplorerTreeUI[roots.length];
		for(int i = 0; i < roots.length; i++) {
			treeUIs[i] = createTab(tabFolder, roots[i]);
		}
	}

	public void setSupplierFileIdentifier(Collection<? extends ISupplierFileIdentifier> supplierFileEditorSupportList) {

		for(DataExplorerTreeUI ui : treeUIs) {
			ui.getContentProvider().setSupplierFileIdentifier(supplierFileEditorSupportList);
		}
	}

	public void expandLastDirectoryPath() {

		for(DataExplorerTreeUI ui : treeUIs) {
			ui.expandLastDirectoryPath(preferenceStore);
		}
		tabFolder.setSelection(preferenceStore.getInt(preferenceKey));
	}

	private DataExplorerTreeUI createTab(TabFolder tabFolder, DataExplorerTreeRoot root) {

		TabItem tab = new TabItem(tabFolder, SWT.NONE);
		tab.setText(root.toString());
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		DataExplorerTreeUI treeUI = new DataExplorerTreeUI(composite, root);
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
						preferenceStore.setValue(preferenceKey, tabFolder.indexOf(tab));
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
