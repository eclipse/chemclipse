/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.l10n.Messages;
import org.eclipse.chemclipse.ux.extension.ui.listener.DataExplorerDragListener;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DataExplorerTreeUI {

	private final TreeViewer treeViewer;
	private final DataExplorerTreeRoot dataExplorerTreeRoot;
	private IPreferenceStore preferenceStore = null;
	private String preferenceKey = null;

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot dataExplorerTreeRoot, Collection<? extends ISupplierFileIdentifier> identifier) {

		this(parent, dataExplorerTreeRoot, IdentifierCacheSupport.createIdentifierCache(identifier));
	}

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot dataExplorerTreeRoot, Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {

		this.dataExplorerTreeRoot = dataExplorerTreeRoot;
		treeViewer = createTreeViewer(parent, identifier);
		//
		DataExplorerDragListener dragListener = new DataExplorerDragListener(treeViewer);
		Transfer[] transferTypes = new Transfer[]{FileTransfer.getInstance()};
		this.treeViewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, transferTypes, dragListener);
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

		this.preferenceStore = preferenceStore;
		this.preferenceKey = preferenceKey;
		//
		File lastFile = new File(preferenceStore.getString(preferenceKey));
		if(lastFile.exists()) {
			/*
			 * Expand Level
			 */
			treeViewer.expandToLevel(lastFile, 1);
			treeViewer.setSelection(new StructuredSelection(lastFile), true);
			//
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

		Object object = treeViewer.getStructuredSelection().getFirstElement();
		if(object instanceof File file) {
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
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Messages.storingPreferencesFailed, e));
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

		/*
		 * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=573090
		 */
		DisplayUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				treeViewer.setInput(dataExplorerTreeRoot.getRootContent());
				if(preferenceStore != null && preferenceKey != null) {
					expandLastDirectoryPath(preferenceStore, preferenceKey);
				}
			}
		});
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
		//
		return counter;
	}
}