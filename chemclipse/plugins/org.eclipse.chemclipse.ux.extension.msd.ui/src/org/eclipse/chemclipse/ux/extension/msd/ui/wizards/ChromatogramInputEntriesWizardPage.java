/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport;
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

	private TreeViewer chromatogramViewer;
	private IChromatogramWizardElements chromatogramWizardElements;

	/**
	 * @param pageName
	 */
	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements) {
		//
		super(ChromatogramInputEntriesWizardPage.class.getName());
		setTitle("Open Chromatogram (MSD) File(s)");
		setDescription("Select a chromatogram/chromatograms file to open.");
		this.chromatogramWizardElements = chromatogramWizardElements;
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
