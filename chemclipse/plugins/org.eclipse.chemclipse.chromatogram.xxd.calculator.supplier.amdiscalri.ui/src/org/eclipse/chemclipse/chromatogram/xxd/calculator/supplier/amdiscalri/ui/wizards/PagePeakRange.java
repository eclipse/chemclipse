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

import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.SelectedPeakChromatogramUI;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PagePeakRange extends AbstractExtendedWizardPage {

	private IRetentionIndexWizardElements wizardElements;

	public PagePeakRange(IRetentionIndexWizardElements wizardElements) {
		//
		super(PagePeakRange.class.getName());
		setTitle("Peak Start");
		setDescription("Please select the start peak.");
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
		createChromatogramField(composite);
		createStartIndexPeakField(composite);
		createStopIndexPeakField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createChromatogramField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new FillLayout());
		SelectedPeakChromatogramUI selectedPeakChromatogramUI = new SelectedPeakChromatogramUI(parent, SWT.NONE);
	}

	private void createStartIndexPeakField(Composite composite) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("Start Index Peak");
		label.setLayoutData(getGridData());
		//
		Combo combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(getGridData());
		combo.setItems(wizardElements.getAvailableStandards());
	}

	private void createStopIndexPeakField(Composite composite) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("Stop Index Peak");
		label.setLayoutData(getGridData());
		//
		Combo combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(getGridData());
		combo.setItems(wizardElements.getAvailableStandards());
	}

	private GridData getGridData() {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalIndent = 5;
		return gridData;
	}

	private void validateSelection() {

		String message = null;
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
