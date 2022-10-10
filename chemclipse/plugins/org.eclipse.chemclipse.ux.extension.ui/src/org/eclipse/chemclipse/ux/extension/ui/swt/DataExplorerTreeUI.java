/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvement identifier handling
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DataExplorerTreeUI {

	private final TreeViewer treeViewer;
	private final DataExplorerTreeRoot dataExplorerTreeRoot;

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot dataExplorerTreeRoot, Collection<? extends ISupplierFileIdentifier> identifier) {

		this(parent, dataExplorerTreeRoot, IdentifierCacheSupport.createIdentifierCache(identifier));
	}

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot dataExplorerTreeRoot, Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {

		this.dataExplorerTreeRoot = dataExplorerTreeRoot;
		treeViewer = createTreeViewer(parent, identifier);
	}

	public DataExplorerTreeRoot getRoot() {

		return dataExplorerTreeRoot;
	}

	public TreeViewer getTreeViewer() {

		return treeViewer;
	}

	public void expandLastDirectoryPath(IPreferenceStore preferenceStore) {

		String preferenceKey = dataExplorerTreeRoot.getPreferenceKeyDefaultPath();
		expandLastDirectoryPath(preferenceStore, preferenceKey);
	}

	public void expandLastDirectoryPath(IPreferenceStore preferenceStore, String preferenceKey) {

		File lastFile = new File(preferenceStore.getString(preferenceKey));
		if(lastFile.exists()) {
			// expand level
			treeViewer.expandToLevel(lastFile, 1);
			// select to scroll into view
			treeViewer.setSelection(new StructuredSelection(lastFile), true);
			// clear selection for unselected default view scrolled to last position
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					treeViewer.setSelection(StructuredSelection.EMPTY);
				}
			});
		}
	}

	public void saveLastDirectoryPath(IPreferenceStore preferenceStore) {

		String preferenceKey = getRoot().getPreferenceKeyDefaultPath();
		saveLastDirectoryPath(preferenceStore, preferenceKey);
	}

	public void saveLastDirectoryPath(IPreferenceStore preferenceStore, String preferenceKey) {

		File file = (File)treeViewer.getStructuredSelection().getFirstElement();
		if(file != null) {
			File directoryPath = null;
			if(file.isFile()) {
				/*
				 * Sometimes the data is stored
				 * in nested directories.
				 */
				File directory = file.getParentFile();
				if(directory != null) {
					File directoryRoot = directory.getParentFile();
					if(getNumberOfChildDirectories(directoryRoot) <= 1) {
						directoryPath = directoryRoot;
					} else {
						directoryPath = directory;
					}
				}
			} else {
				directoryPath = file;
			}
			if(directoryPath != null) {
				preferenceStore.setValue(preferenceKey, directoryPath.getAbsolutePath());
			}
		}
		//
		if(preferenceStore.needsSaving()) {
			if(preferenceStore instanceof IPersistentPreferenceStore persistentPreferenceStore) {
				try {
					persistentPreferenceStore.save();
				} catch(IOException e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Storing the preferences failed.", e));
				}
			}
		}
	}

	private TreeViewer createTreeViewer(Composite parent, Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {

		TreeViewer treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.VIRTUAL);
		//
		treeViewer.setUseHashlookup(true);
		treeViewer.setExpandPreCheckFilters(true);
		treeViewer.setContentProvider(new DataExplorerContentProvider(identifier));
		treeViewer.setLabelProvider(new DataExplorerLabelProvider(identifier));
		setInput(treeViewer);
		//
		return treeViewer;
	}

	private void setInput(TreeViewer treeViewer) {

		treeViewer.setInput(dataExplorerTreeRoot.getRootContent());
	}

	private int getNumberOfChildDirectories(File directory) {

		int counter = 0;
		if(directory != null) {
			for(File file : directory.listFiles()) {
				if(file.isDirectory()) {
					counter++;
				}
			}
		}
		return counter;
	}
}
