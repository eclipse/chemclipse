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

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.CalibrationFileTableViewerUI;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class PageCalibrationTable extends AbstractExtendedWizardPage {

	private IRetentionIndexWizardElements wizardElements;
	private CalibrationFileTableViewerUI calibrationFileTableViewerUI;

	public PageCalibrationTable(IRetentionIndexWizardElements wizardElements) {
		//
		super(PageCalibrationTable.class.getName());
		setTitle("Calibration Table");
		setDescription("Please verify the calibration table.");
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
		createCheckBoxField(composite);
		createTableField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createCheckBoxField(Composite composite) {

		Button buttonValidateRetentionIndices = new Button(composite, SWT.CHECK);
		buttonValidateRetentionIndices.setText("Retention indices are valid.");
		buttonValidateRetentionIndices.setSelection(false);
		buttonValidateRetentionIndices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonValidateRetentionIndices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				validateSelection();
			}
		});
	}

	private void createTableField(Composite composite) {

		calibrationFileTableViewerUI = new CalibrationFileTableViewerUI(composite);
		calibrationFileTableViewerUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void validateSelection() {

		String message = null;
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
