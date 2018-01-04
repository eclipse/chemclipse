/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramInputEntriesWizardPage extends WizardPage {

	private TreeViewer chromatogramViewer;
	//
	private final IChromatogramWizardElements chromatogramWizardElements;
	private final String expandToDirectoryPath;

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements) {
		//
		this(chromatogramWizardElements, "Open Chromatogram (WSD) File(s)", "Select a chromatogram/chromatograms file to open.");
	}

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements, String title, String description) {
		//
		this(chromatogramWizardElements, title, description, "");
	}

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements, String title, String description, String expandToDirectoryPath) {
		//
		super(ChromatogramInputEntriesWizardPage.class.getName());
		setTitle(title);
		setDescription(description);
		this.chromatogramWizardElements = chromatogramWizardElements;
		this.expandToDirectoryPath = expandToDirectoryPath;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Chromatogram Tree Viewer
		 */
		chromatogramViewer = new TreeViewer(composite, SWT.MULTI);
		chromatogramViewer.setLabelProvider(new SupplierFileExplorerLabelProvider(ChromatogramSupport.getInstanceIdentifier()));
		chromatogramViewer.setContentProvider(new SupplierFileExplorerContentProvider(ChromatogramSupport.getInstanceIdentifier()));
		chromatogramViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

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
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(chromatogramViewer, expandToDirectoryPath);
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
