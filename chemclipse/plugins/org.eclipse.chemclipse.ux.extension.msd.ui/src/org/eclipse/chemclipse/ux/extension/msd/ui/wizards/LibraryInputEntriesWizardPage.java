/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.MassSpectrumFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.MassSpectrumFileExplorerLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class LibraryInputEntriesWizardPage extends WizardPage {

	private TreeViewer libraryViewer;

	protected LibraryInputEntriesWizardPage(String pageName, String title, String description) {
		super(pageName);
		setTitle(title);
		setDescription(description);
	}

	/**
	 * Returns the library viewer selection.
	 * 
	 * @return
	 */
	public ISelection getSelection() {

		return libraryViewer.getSelection();
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Chromatogram Tree Viewer
		 */
		libraryViewer = new TreeViewer(composite, SWT.MULTI);
		libraryViewer.setLabelProvider(new MassSpectrumFileExplorerLabelProvider());
		libraryViewer.setContentProvider(new MassSpectrumFileExplorerContentProvider());
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(libraryViewer);
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
