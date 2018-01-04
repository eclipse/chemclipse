/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.jface.wizard.Wizard;

public class ChromatogramInputEntriesWizard extends Wizard {

	private ChromatogramInputEntriesWizardPage inputEntriesPage;
	private IChromatogramWizardElements chromatogramWizardElements;
	//
	private String title;
	private String description;
	private String expandToDirectoryPath;

	public ChromatogramInputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements) {
		this(chromatogramWizardElements, "", "");
	}

	public ChromatogramInputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements, String title, String description) {
		this(chromatogramWizardElements, title, description, "");
	}

	public ChromatogramInputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements, String title, String description, String expandToDirectoryPath) {
		super();
		setNeedsProgressMonitor(true);
		this.chromatogramWizardElements = chromatogramWizardElements;
		this.title = (title == null) ? "" : title;
		this.description = (description == null) ? "" : description;
		this.expandToDirectoryPath = (expandToDirectoryPath == null) ? "" : expandToDirectoryPath;
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	@Override
	public void addPages() {

		if(title.equals("") && description.equals("")) {
			inputEntriesPage = new ChromatogramInputEntriesWizardPage(chromatogramWizardElements);
		} else {
			inputEntriesPage = new ChromatogramInputEntriesWizardPage(chromatogramWizardElements, title, description, expandToDirectoryPath);
		}
		addPage(inputEntriesPage);
	}
}
