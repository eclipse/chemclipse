/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.wizards.InputEntriesWizard.TreeSelection;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class InputEntriesWizardPage extends WizardPage {

	//
	private final IChromatogramWizardElements chromatogramWizardElements;
	private IBaseLabelProvider labelProvider;
	private IContentProvider contentProvider;
	private String selectedDrivePath;
	private String selectedHomePath;
	private String selectedUserLocationPath;
	private TreeSelection treeSelection;
	private TreeSelection defaultTree;

	public InputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements, String title, String description, IBaseLabelProvider labelProvider, IContentProvider contentProvider, String selectedDrivePath, String selectedHomePath, String selectedUserLocationPath, TreeSelection defaultTree) {
		//
		super(InputEntriesWizardPage.class.getName());
		setTitle(title);
		setDescription(description);
		this.labelProvider = labelProvider;
		this.contentProvider = contentProvider;
		this.chromatogramWizardElements = chromatogramWizardElements;
		this.selectedDrivePath = selectedDrivePath;
		this.selectedHomePath = selectedHomePath;
		this.selectedUserLocationPath = selectedUserLocationPath;
		this.treeSelection = TreeSelection.NONE;
		this.defaultTree = defaultTree;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		createDrivesTreeViewer(tabFolder);
		createHomeTreeViewer(tabFolder);
		createUserLocationTreeViewer(tabFolder);
		if(!defaultTree.equals(TreeSelection.NONE)) {
			tabFolder.setSelection(defaultTree.ordinal());
		}
		setControl(composite);
	}

	private void createDrivesTreeViewer(TabFolder tabFolder) {

		TabItem tabDrives = new TabItem(tabFolder, SWT.NONE);
		tabDrives.setText("Drives");
		//
		String directoryPath = selectedDrivePath;
		Composite composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new FillLayout());
		createDirectoryTree(composite, getDrives(), directoryPath, TreeSelection.DRIVES);
		//
		tabDrives.setControl(composite);
	}

	private void createHomeTreeViewer(TabFolder tabFolder) {

		TabItem tabHome = new TabItem(tabFolder, SWT.NONE);
		tabHome.setText("Home");
		//
		String directoryPath = selectedHomePath;
		Composite composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new FillLayout());
		createDirectoryTree(composite, new File(UserManagement.getUserHome()), directoryPath, TreeSelection.HOME);
		//
		tabHome.setControl(composite);
	}

	private void createUserLocationTreeViewer(TabFolder tabFolder) {

		TabItem tabUserLocation = new TabItem(tabFolder, SWT.NONE);
		tabUserLocation.setText("User Location");
		//
		String directoryPath = selectedUserLocationPath;
		Composite composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new FillLayout());
		createDirectoryTree(composite, getUserLocation(), directoryPath, TreeSelection.USER_LOCATION);
		//
		tabUserLocation.setControl(composite);
	}

	private File getUserLocation() {

		String userLocationPath = PreferenceSupplier.getUserLocationPath();
		File userLocation = new File(userLocationPath);
		if(!userLocation.exists()) {
			userLocation = new File(UserManagement.getUserHome());
		}
		return userLocation;
	}

	private IFileSystem getDrives() {

		return EFS.getLocalFileSystem();
	}

	public TreeViewer createDirectoryTree(Composite composite, Object input, String expandToDirectoryPath, TreeSelection actuelSelection) {

		//
		TreeViewer chromatogramViewer = new TreeViewer(composite, SWT.MULTI);
		chromatogramViewer.setLabelProvider(labelProvider);
		chromatogramViewer.setContentProvider(contentProvider);
		chromatogramViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				treeSelection = actuelSelection;
				chromatogramWizardElements.clearSelectedChromatograms();
				ISelection selection = chromatogramViewer.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				for(Object element : structuredSelection.toList()) {
					chromatogramWizardElements.addSelectedChromatogram(element.toString());
				}
			}
		});
		/*
		 * Load the content asynchronously.
		 * To expand the tree, the element or tree path needs to be set here.
		 */
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(chromatogramViewer, input, expandToDirectoryPath);
		return chromatogramViewer;
	}

	public TreeSelection getTreeSelection() {

		return treeSelection;
	}
}
