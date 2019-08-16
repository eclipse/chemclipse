/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.ui.internal.wizards;

import org.eclipse.jface.wizard.Wizard;

import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.model.IChromatogramReportSupplierEntry;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class ChromatogramReportEntriesWizard extends Wizard {

	private ChromatogramReportEntriesWizardPage reportEntriesPage;
	private IChromatogramReportSupplierEntry reportEntry = null;

	public ChromatogramReportEntriesWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Returns the chromatogram report entry.
	 * May return null.
	 * 
	 * @return IChromatogramReportEntry
	 */
	public IChromatogramReportSupplierEntry getChromatogramReportEntry() {

		return reportEntry;
	}

	@Override
	public boolean performFinish() {

		try {
			reportEntry = reportEntriesPage.getChromatogramReportEntry();
		} catch(NoReportSupplierAvailableException e) {
			reportEntry = null;
		}
		return true;
	}

	@Override
	public void addPages() {

		reportEntriesPage = new ChromatogramReportEntriesWizardPage("Chromatogram Report Supplier");
		addPage(reportEntriesPage);
	}
}
