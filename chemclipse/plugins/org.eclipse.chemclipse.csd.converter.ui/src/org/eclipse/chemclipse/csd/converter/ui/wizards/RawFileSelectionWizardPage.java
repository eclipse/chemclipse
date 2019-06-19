/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - support new lazy table model
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.chemclipse.xxd.process.files.SupplierFileIdentifier;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

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
		chromatogramViewer = new TreeViewer(composite, SWT.MULTI | SWT.VIRTUAL);
		chromatogramViewer.setUseHashlookup(true);
		ISupplierFileIdentifier supplierFileIdentifier = new SupplierFileIdentifier(DataType.CSD);
		chromatogramViewer.setLabelProvider(new DataExplorerLabelProvider(supplierFileIdentifier));
		chromatogramViewer.setContentProvider(new DataExplorerContentProvider(supplierFileIdentifier));
		chromatogramViewer.setInput(File.listRoots());
		/*
		 * Set the control.
		 */
		setControl(composite);
	}
}
