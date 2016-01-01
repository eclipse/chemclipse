/*******************************************************************************
 * Copyright (c) 2015, 2016 Daniel Mariano.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Daniel Mariano - initial API and implementation
 * Dr. Philip Wenig - minor improvements
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.jface.wizard.Wizard;

public class TimeRangeWizard extends Wizard {

	protected TimeRangeWizardPage timeRangeWizardPage;
	public String textRetentionTimeRange;

	public TimeRangeWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {

		return "Peak Intensity Table";
	}

	@Override
	public void addPages() {

		timeRangeWizardPage = new TimeRangeWizardPage();
		addPage(timeRangeWizardPage);
	}

	@Override
	public boolean performFinish() {

		textRetentionTimeRange = timeRangeWizardPage.getTextRetentionTimeRange();
		return true;
	}

	/*
	 * Gets first page of wizard
	 */
	public TimeRangeWizardPage getPageOne() {

		return timeRangeWizardPage;
	}

	/*
	 * Gets user input of first text field
	 */
	public String getTextOne() {

		return textRetentionTimeRange;
	}
}
