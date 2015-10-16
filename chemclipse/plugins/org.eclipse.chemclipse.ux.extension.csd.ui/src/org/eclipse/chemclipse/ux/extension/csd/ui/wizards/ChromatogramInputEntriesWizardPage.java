/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramInputEntriesWizardPage extends WizardPage {

	private TreeViewer chromatogramViewer;

	/**
	 * @param pageName
	 */
	protected ChromatogramInputEntriesWizardPage(String pageName, String title, String description) {

		super(pageName);
		setTitle(title);
		setDescription(description);
	}

	/**
	 * Returns the chromatogram viewer selection.
	 * 
	 * @return
	 */
	public ISelection getSelection() {

		return chromatogramViewer.getSelection();
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
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(chromatogramViewer);
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
