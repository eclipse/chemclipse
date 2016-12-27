/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
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
import org.eclipse.jface.wizard.Wizard;

public class ChromatogramInputEntriesWizard extends Wizard {

	private ChromatogramInputEntriesWizardPage inputEntriesPage;
	private IChromatogramWizardElements chromatogramWizardElements;

	public ChromatogramInputEntriesWizard(IChromatogramWizardElements chromatogramWizardElements) {
		//
		super();
		setNeedsProgressMonitor(true);
		this.chromatogramWizardElements = chromatogramWizardElements;
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	@Override
	public void addPages() {

		inputEntriesPage = new ChromatogramInputEntriesWizardPage(chromatogramWizardElements);
		addPage(inputEntriesPage);
	}
}
