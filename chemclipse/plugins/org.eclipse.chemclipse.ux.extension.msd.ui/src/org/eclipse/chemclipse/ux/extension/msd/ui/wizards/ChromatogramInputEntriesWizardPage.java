/*******************************************************************************
 * Copyright (c) 2011, 2016 Dr. Philip Wenig.
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
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramInputEntriesWizardPage extends WizardPage {

	// private static final Logger logger = Logger.getLogger(ChromatogramInputEntriesWizardPage.class);
	private ChromatogramInputEntriesUI content;
	private final IChromatogramWizardElements chromatogramWizardElements;

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements) {
		//
		this(chromatogramWizardElements, "Open Chromatogram (MSD) File(s)", "Select a chromatogram/chromatograms file to open.");
	}

	public ChromatogramInputEntriesWizardPage(IChromatogramWizardElements chromatogramWizardElements, String title, String description) {
		//
		super(ChromatogramInputEntriesWizardPage.class.getName());
		setTitle(title);
		setDescription(description);
		this.chromatogramWizardElements = chromatogramWizardElements;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		content = new ChromatogramInputEntriesUI(composite, SWT.NONE).setChromatogramWizardElements(chromatogramWizardElements);
		setControl(composite);
	}

	/**
	 * The given directory will be expanded if available.
	 * 
	 * @param directoryPath
	 */
	public void expandTree(String directoryPath) {

		content.expandTree(directoryPath);
	}
}
