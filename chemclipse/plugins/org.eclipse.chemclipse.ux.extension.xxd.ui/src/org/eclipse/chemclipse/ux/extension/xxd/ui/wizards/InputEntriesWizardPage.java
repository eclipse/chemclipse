/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - support new lazy table model
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard.TreeSelection;
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

	private InputWizardSettings inputWizardSettings;
	private TreeSelection treeSelection;

	public InputEntriesWizardPage(InputWizardSettings inputWizardSettings) {
		//
		super(InputEntriesWizardPage.class.getName());
		this.inputWizardSettings = inputWizardSettings;
		//
		setTitle(inputWizardSettings.getTitle());
		setDescription(inputWizardSettings.getDescription());
		this.treeSelection = TreeSelection.NONE;
	}

	public void saveSelectedPath() {

		inputWizardSettings.saveSelectedPath(getTreeSelection());
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return inputWizardSettings.getChromatogramWizardElements();
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
		TreeSelection defaultTree = inputWizardSettings.getDefaultTree();
		if(!defaultTree.equals(TreeSelection.NONE)) {
			tabFolder.setSelection(defaultTree.ordinal());
		}
		setControl(composite);
	}

	private void createDrivesTreeViewer(TabFolder tabFolder) {

		TabItem tabDrives = new TabItem(tabFolder, SWT.NONE);
		tabDrives.setText("Drives");
		//
		String directoryPath = inputWizardSettings.getSelectedDrivePath();
		Composite composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new FillLayout());
		createDirectoryTree(composite, File.listRoots(), directoryPath, TreeSelection.DRIVES);
		//
		tabDrives.setControl(composite);
	}

	private void createHomeTreeViewer(TabFolder tabFolder) {

		TabItem tabHome = new TabItem(tabFolder, SWT.NONE);
		tabHome.setText("Home");
		//
		String directoryPath = inputWizardSettings.getSelectedHomePath();
		Composite composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new FillLayout());
		createDirectoryTree(composite, new File[]{new File(UserManagement.getUserHome())}, directoryPath, TreeSelection.HOME);
		//
		tabHome.setControl(composite);
	}

	private void createUserLocationTreeViewer(TabFolder tabFolder) {

		TabItem tabUserLocation = new TabItem(tabFolder, SWT.NONE);
		tabUserLocation.setText("User Location");
		//
		String directoryPath = inputWizardSettings.getSelectedUserLocationPath();
		Composite composite = new Composite(tabFolder, SWT.None);
		composite.setLayout(new FillLayout());
		createDirectoryTree(composite, new File[]{getUserLocation()}, directoryPath, TreeSelection.USER_LOCATION);
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

	public TreeViewer createDirectoryTree(Composite composite, File[] input, String expandToDirectoryPath, TreeSelection actuelSelection) {

		//
		TreeViewer chromatogramViewer = new TreeViewer(composite, SWT.MULTI | SWT.VIRTUAL);
		chromatogramViewer.setUseHashlookup(true);
		chromatogramViewer.setLabelProvider(new DataExplorerLabelProvider(inputWizardSettings.getSupplierFileIdentifierList()));
		chromatogramViewer.setContentProvider(new DataExplorerContentProvider(inputWizardSettings.getSupplierFileIdentifierList()));
		chromatogramViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				treeSelection = actuelSelection;
				inputWizardSettings.getChromatogramWizardElements().clearSelectedChromatograms();
				ISelection selection = chromatogramViewer.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				for(Object element : structuredSelection.toList()) {
					inputWizardSettings.getChromatogramWizardElements().addSelectedChromatogram(element.toString());
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
