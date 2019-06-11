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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Dr. Philip Wenig
 *
 */
public class ChromatogramMSDInputFilesWizardPage extends WizardPage {

	private TreeViewer chromatogramViewer;

	/**
	 * @param pageName
	 */
	protected ChromatogramMSDInputFilesWizardPage(String pageName) {
		super(pageName);
		setTitle("Chromatogram MSD Input Files");
		setDescription("This wizard lets you select several chormatogram MSD input files.");
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		/*
		 * Chromatogram Tree Viewer
		 */
		chromatogramViewer = new TreeViewer(composite, SWT.MULTI);
		ISupplierFileIdentifier instanceIdentifier = new EditorSupportFactory(DataType.MSD).getInstanceIdentifier();
		chromatogramViewer.setLabelProvider(new DataExplorerLabelProvider(instanceIdentifier));
		chromatogramViewer.setContentProvider(new DataExplorerContentProvider(instanceIdentifier));
		chromatogramViewer.setInput(EFS.getLocalFileSystem());
		/*
		 * Set the control.
		 */
		setControl(composite);
	}

	/**
	 * Returns the chromatogram viewer selection.
	 *
	 * @return
	 */
	public ISelection getSelection() {

		return chromatogramViewer.getSelection();
	}
}
