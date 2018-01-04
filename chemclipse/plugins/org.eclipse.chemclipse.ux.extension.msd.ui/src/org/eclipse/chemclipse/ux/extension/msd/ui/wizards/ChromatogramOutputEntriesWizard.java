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

import org.eclipse.jface.wizard.Wizard;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.model.IChromatogramOutputEntry;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramOutputEntriesWizard extends Wizard {

	private ChromatogramOutputEntriesWizardPage outputEntriesPage;
	private IChromatogramOutputEntry outputEntry = null;

	public ChromatogramOutputEntriesWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Returns the chromatogram output entry.
	 * May return null.
	 * 
	 * @return IChromatogramOutputEntry
	 */
	public IChromatogramOutputEntry getChromatogramOutputEntry() {

		return outputEntry;
	}

	@Override
	public boolean performFinish() {

		try {
			outputEntry = outputEntriesPage.getChromatogramOutputEntry();
		} catch(NoConverterAvailableException e) {
			outputEntry = null;
		}
		return true;
	}

	@Override
	public void addPages() {

		outputEntriesPage = new ChromatogramOutputEntriesWizardPage("Output Chromatogram Formats");
		addPage(outputEntriesPage);
	}
}
