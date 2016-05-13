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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

public class PageCalibrationSettings extends AbstractExtendedWizardPage {

	private IRetentionIndexWizardElements wizardElements;
	private Text textFileCal;

	public PageCalibrationSettings(IRetentionIndexWizardElements wizardElements) {
		//
		super(PageCalibrationSettings.class.getName());
		setTitle("Calibration Settings");
		setDescription("Please select the calibration settings.");
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
		composite.setLayout(new GridLayout(2, false));
		//
		createCalibrationFileField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createCalibrationFileField(Composite composite) {

		textFileCal = new Text(composite, SWT.BORDER);
		textFileCal.setText("");
		textFileCal.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textFileCal.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				validateSelection();
			}
		});
		//
		Button buttonStandards = new Button(composite, SWT.PUSH);
		buttonStandards.setText("Select *.cal");
		buttonStandards.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText("Select an existing *.cal template file.");
				fileDialog.setFilterExtensions(new String[]{"*.cal", "*.CAL"});
				fileDialog.setFilterNames(new String[]{"AMDIS Calibration *.cal", "AMDIS Calibration *.CAL"});
				fileDialog.setFilterPath(""); // TODO persist path
				String pathname = fileDialog.open();
				if(pathname != null) {
					fileDialog.getFilterPath(); // TODO persist path
					textFileCal.setText(pathname);
				}
			}
		});
	}

	private void validateSelection() {

		String message = null;
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
