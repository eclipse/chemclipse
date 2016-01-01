/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.ui.wizards;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerLabelProvider;

public class RawFileSelectionWizardPage extends WizardPage {

	private TreeViewer chromatogramViewer;

	public RawFileSelectionWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
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
		chromatogramViewer.setInput(EFS.getLocalFileSystem());
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
