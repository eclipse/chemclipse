/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerLabelProvider;
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

	private static final Logger logger = Logger.getLogger(ChromatogramInputEntriesWizardPage.class);
	private TreeViewer chromatogramViewer;
	private IChromatogramWizardElements chromatogramWizardElements;

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements) {
		//
		this(chromatogramWizardElements, "Open Chromatogram (CSD) File(s)", "Select a chromatogram/chromatograms file to open.");
	}

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements, String title, String description) {
		//
		super(ChromatogramInputEntriesWizardPage.class.getName());
		setTitle(title);
		setDescription(description);
		this.chromatogramWizardElements = chromatogramWizardElements;
	}

	/**
	 * The given directory will be expanded if available.
	 * 
	 * @param directoryPath
	 */
	public void expandTree(String directoryPath) {

		try {
			File elementOrTreePath = new File(directoryPath);
			chromatogramViewer.expandToLevel(elementOrTreePath, 1);
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Chromatogram Tree Viewer
		 */
		chromatogramViewer = new TreeViewer(composite, SWT.MULTI);
		chromatogramViewer.setLabelProvider(new ChromatogramFileExplorerLabelProvider(ChromatogramSupport.getInstanceIdentifier()));
		chromatogramViewer.setContentProvider(new ChromatogramFileExplorerContentProvider(ChromatogramSupport.getInstanceIdentifier()));
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
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(chromatogramViewer);
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
