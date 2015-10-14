/*******************************************************************************
 * Copyright (c) 2015 Daniel Mariano.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Daniel Mariano - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.jface.wizard.Wizard;

public class PeakIntensityTableTimeRangeWizard extends Wizard {

	protected PeakIntensityTableTimeRangeWizardPage one;
	public String textOne;

	public PeakIntensityTableTimeRangeWizard() {

		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {

		return "Peak Intensity Table";
	}

	@Override
	public void addPages() {

		one = new PeakIntensityTableTimeRangeWizardPage();
		addPage(one);
	}

	@Override
	public boolean performFinish() {

		textOne = one.getText1();
		return true;
	}

	/*
	 * Gets first page of wizard
	 */
	public PeakIntensityTableTimeRangeWizardPage getPageOne() {

		return one;
	}

	/*
	 * Gets user input of first text field
	 */
	public String getTextOne() {

		return textOne;
	}
}
