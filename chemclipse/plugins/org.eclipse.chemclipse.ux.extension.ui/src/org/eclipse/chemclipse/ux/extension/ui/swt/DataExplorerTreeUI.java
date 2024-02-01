/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

public class DataExplorerTreeUI {

	private AtomicReference<TreeViewer> treeViewerControl = new AtomicReference<>();
	//
	private DataExplorerTreeRoot dataExplorerTreeRoot = null;
	private File directory = null;
	private IPreferenceStore preferenceStore = null;
	private String preferenceKey = null;
	private DataExplorerLabelProvider labelProvider;

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot dataExplorerTreeRoot, Collection<? extends ISupplierFileIdentifier> identifier) {

		this(parent, dataExplorerTreeRoot, IdentifierCacheSupport.createIdentifierCache(identifier));
	}

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot dataExplorerTreeRoot, Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {

		this.dataExplorerTreeRoot = dataExplorerTreeRoot;
		createTreeViewer(parent, identifier);
		initialize();
	}

	public void updateDirectory(File directory) {

		this.directory = directory;
		updateDirectory();
	}

	public DataExplorerTreeRoot getRoot() {

		return dataExplorerTreeRoot;
	}

	public TreeViewer getTreeViewer() {

		return treeViewerControl.get();
	}

	public void expandLastDirectoryPath(IPreferenceStore preferenceStore) {

		String preferenceKey = dataExplorerTreeRoot.getPreferenceKeyDefaultPath();
		expandLastDirectoryPath(preferenceStore, preferenceKey);
	}

	public void expandLastDirectoryPath(IPreferenceStore preferenceStore, String preferenceKey) {

		this.preferenceStore = preferenceStore;
		this.preferenceKey = preferenceKey;
		//
		File selection = new File(preferenceStore.getString(preferenceKey));
		if(selection.exists()) {
			/*
			 * Expand Level
			 */
			TreeViewer treeViewer = treeViewerControl.get();
			treeViewer.expandToLevel(selection, 1);
			treeViewer.setSelection(new StructuredSelection(selection), true);
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

		Object object = treeViewerControl.get().getStructuredSelection().getFirstElement();
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
			//
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

	private void initialize() {

		DataExplorerDragListener dragListener = new DataExplorerDragListener(treeViewerControl.get());
		Transfer[] transferTypes = new Transfer[]{FileTransfer.getInstance()};
		treeViewerControl.get().addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, transferTypes, dragListener);
	}

	private void createTreeViewer(Composite parent, Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {

		TreeViewer treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.VIRTUAL);
		//
		treeViewer.setUseHashlookup(true);
		treeViewer.setExpandPreCheckFilters(true);
		treeViewer.setContentProvider(new DataExplorerContentProvider(identifier));
		labelProvider = new DataExplorerLabelProvider(identifier);
		treeViewer.getTree().addListener(SWT.SetData, createLabelListener());
		treeViewer.getTree().addListener(SWT.Selection, createLabelListener());
		setInput(treeViewer);
		treeViewerControl.set(treeViewer);
	}

	private Listener createLabelListener() {

		return new Listener() {

			@Override
			public void handleEvent(Event event) {

				TreeItem item = (TreeItem)event.item;
				if(item == null || item.getData() == null) {
					return;
				}
				item.setText(labelProvider.getText(item.getData()));
				// Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=573090
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						item.setImage(labelProvider.getImage(item.getData()));
					}
				});
			}
		};
	}

	private void setInput(TreeViewer treeViewer) {

		treeViewer.setInput(dataExplorerTreeRoot.getRootContent());
		updateDirectory();
		if(preferenceStore != null && preferenceKey != null) {
			expandLastDirectoryPath(preferenceStore, preferenceKey);
		}
	}

	private void updateDirectory() {

		if(directory != null && directory.exists()) {
			treeViewerControl.get().setInput(new File[]{directory});
		}
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