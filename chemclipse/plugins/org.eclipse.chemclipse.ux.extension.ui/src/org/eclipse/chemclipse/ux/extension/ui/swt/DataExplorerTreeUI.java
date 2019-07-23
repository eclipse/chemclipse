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
import java.util.Collections;

import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DataExplorerTreeUI {

	public static enum DataExplorerTreeRoot {
		NONE(""), DRIVES("Drives"), HOME("Home"), USER_LOCATION("User Location");

		private String label;

		private DataExplorerTreeRoot(String label) {
			this.label = label;
		}

		@Override
		public String toString() {

			return this != NONE ? label : super.toString();
		}
	}

	private TreeViewer treeViewer;
	private DataExplorerTreeRoot root;

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot root) {
		this(parent, root, Collections.emptyList());
	}

	public DataExplorerTreeUI(Composite parent, DataExplorerTreeRoot root, Collection<? extends ISupplierFileIdentifier> identifier) {
		this.root = root;
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.VIRTUAL);
		treeViewer.setUseHashlookup(true);
		treeViewer.setExpandPreCheckFilters(true);
		DataExplorerContentProvider contentProvider = new DataExplorerContentProvider(identifier);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new DataExplorerLabelProvider(contentProvider));
		switch(root) {
			case DRIVES:
				treeViewer.setInput(File.listRoots());
				break;
			case HOME:
				treeViewer.setInput(new File[]{new File(UserManagement.getUserHome())});
				break;
			case USER_LOCATION:
				treeViewer.setInput(new File[]{getUserLocation()});
				break;
			case NONE:
				// fall through...
			default:
				break;
		}
	}

	public DataExplorerContentProvider getContentProvider() {

		return (DataExplorerContentProvider)treeViewer.getContentProvider();
	}

	public DataExplorerTreeRoot getRoot() {

		return root;
	}

	public TreeViewer getTreeViewer() {

		return treeViewer;
	}

	private File getUserLocation() {

		String userLocationPath = PreferenceSupplier.getUserLocationPath();
		File userLocation = new File(userLocationPath);
		if(!userLocation.exists()) {
			userLocation = new File(UserManagement.getUserHome());
		}
		return userLocation;
	}

	public void expandLastDirectoryPath() {

		String directoryPath;
		switch(root) {
			case DRIVES:
				directoryPath = PreferenceSupplier.getSelectedDrivePath();
				break;
			case HOME:
				directoryPath = PreferenceSupplier.getSelectedHomePath();
				break;
			case USER_LOCATION:
				directoryPath = PreferenceSupplier.getSelectedUserLocationPath();
				break;
			case NONE:
			default:
				return;
		}
		File lastFile = new File(directoryPath);
		if(lastFile.exists()) {
			treeViewer.expandToLevel(lastFile, 1);
		}
	}

	public void saveLastDirectoryPath() {

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
				switch(root) {
					case DRIVES:
						PreferenceSupplier.setSelectedDrivePath(directoryPath.getAbsolutePath());
						break;
					case HOME:
						PreferenceSupplier.setSelectedHomePath(directoryPath.getAbsolutePath());
						break;
					case USER_LOCATION:
						PreferenceSupplier.setSelectedUserLocationPath(directoryPath.getAbsolutePath());
						break;
					case NONE:
					default:
						return;
				}
			}
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
		return counter;
	}
}
