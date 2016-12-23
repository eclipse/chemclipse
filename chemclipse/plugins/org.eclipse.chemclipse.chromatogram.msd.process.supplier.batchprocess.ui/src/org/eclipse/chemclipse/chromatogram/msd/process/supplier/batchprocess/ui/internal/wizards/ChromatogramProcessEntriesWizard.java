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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.internal.wizards;

import org.eclipse.jface.wizard.Wizard;

import org.eclipse.chemclipse.chromatogram.msd.process.model.IChromatogramProcessEntry;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramProcessEntriesWizard extends Wizard {

	private ChromatogramProcessEntriesWizardPage processEntriesPage;
	private IChromatogramProcessEntry chromatogramProcessEntry;

	/**
	 * If a new process entry shall be added, use chromatogramProcessEntry = null.
	 * 
	 * @param chromatogramProcessEntry
	 */
	public ChromatogramProcessEntriesWizard(IChromatogramProcessEntry chromatogramProcessEntry) {
		super();
		setNeedsProgressMonitor(true);
		this.chromatogramProcessEntry = chromatogramProcessEntry;
	}

	/**
	 * Returns the chromatogram process entry.
	 * May return null.
	 * 
	 * @return IChromatogramProcessEntry
	 */
	public IChromatogramProcessEntry getChromatogramProcessEntry() {

		return chromatogramProcessEntry;
	}

	@Override
	public boolean performFinish() {

		chromatogramProcessEntry = processEntriesPage.getChromatogramProcessEntry();
		return true;
	}

	@Override
	public void addPages() {

		processEntriesPage = new ChromatogramProcessEntriesWizardPage("Chromatogram Processors", chromatogramProcessEntry);
		addPage(processEntriesPage);
	}
}
