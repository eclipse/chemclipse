/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class PagePeakAssignment extends AbstractExtendedWizardPage {

	private IRetentionIndexWizardElements wizardElements;

	public PagePeakAssignment(IRetentionIndexWizardElements wizardElements) {
		//
		super(PagePeakAssignment.class.getName());
		setTitle("Peak Assigment");
		setDescription("Please assign the alkanes.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			System.out.println(wizardElements.getFileName());
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		validateSelection();
		setControl(composite);
	}

	private void validateSelection() {

		String message = null;
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
