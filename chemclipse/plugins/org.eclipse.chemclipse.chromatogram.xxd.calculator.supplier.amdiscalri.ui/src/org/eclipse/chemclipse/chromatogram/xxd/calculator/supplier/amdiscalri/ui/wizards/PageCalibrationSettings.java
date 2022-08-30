/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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

import java.io.File;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PageCalibrationSettings extends AbstractExtendedWizardPage {

	private RetentionIndexWizardElements wizardElements;
	//
	private Button checkBoxUseExistingCalibrationFile;
	private Text textPathRetentionIndexFile;
	private Button buttonSelectCalibrationFile;
	//
	private Button buttonMSD;
	private Button buttonCSD;
	//
	private Button checkBoxUseExistingPeaks;

	public PageCalibrationSettings(RetentionIndexWizardElements wizardElements) {

		super(PageCalibrationSettings.class.getName());
		setTitle("Calibration Settings");
		setDescription("Please select the calibration settings.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		if(getMessage() == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			checkBoxUseExistingCalibrationFile.setSelection(wizardElements.isUseExistingRetentionIndexFile());
			setCalibrationFileSelection(wizardElements.isUseExistingRetentionIndexFile());
			checkBoxUseExistingPeaks.setSelection(wizardElements.isUseAlreadyDetectedPeaks());
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createCalibrationFileField(composite);
		createLabelDataField(composite);
		createButtonMSDField(composite);
		createButtonCSDField(composite);
		createPeakIdentificationField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createCalibrationFileField(Composite composite) {

		checkBoxUseExistingCalibrationFile = new Button(composite, SWT.CHECK);
		checkBoxUseExistingCalibrationFile.setText("Use existing *.cal file for improved detection");
		checkBoxUseExistingCalibrationFile.setEnabled(true);
		checkBoxUseExistingCalibrationFile.setLayoutData(getGridData());
		checkBoxUseExistingCalibrationFile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean useExistingCalibrationFile = checkBoxUseExistingCalibrationFile.getSelection();
				wizardElements.setUseExistingRetentionIndexFile(useExistingCalibrationFile);
				setCalibrationFileSelection(useExistingCalibrationFile);
				validateSelection();
			}
		});
		//
		textPathRetentionIndexFile = new Text(composite, SWT.BORDER);
		textPathRetentionIndexFile.setText("");
		textPathRetentionIndexFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textPathRetentionIndexFile.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				wizardElements.setPathRetentionIndexFile(textPathRetentionIndexFile.getText().trim());
				validateSelection();
			}
		});
		//
		buttonSelectCalibrationFile = new Button(composite, SWT.PUSH);
		buttonSelectCalibrationFile.setText("Select *.cal");
		buttonSelectCalibrationFile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.READ_ONLY);
				fileDialog.setText("Select an existing *.cal template file.");
				fileDialog.setFilterExtensions(new String[]{"*.cal;*.CAL"});
				fileDialog.setFilterNames(new String[]{"AMDIS Calibration (*.cal)"});
				fileDialog.setFilterPath(wizardElements.getFilterPathCalibrationFile());
				String pathRetentionIndexFile = fileDialog.open();
				if(pathRetentionIndexFile != null) {
					textPathRetentionIndexFile.setText(pathRetentionIndexFile);
					wizardElements.setFilterPathCalibrationFile(fileDialog.getFilterPath());
					wizardElements.setPathRetentionIndexFile(pathRetentionIndexFile);
					validateSelection();
				}
			}
		});
	}

	private void createLabelDataField(Composite composite) {

		Label label = new Label(composite, SWT.NONE);
		label.setText("Select whether to use MSD or CSD data to detect the RI pattern.");
		label.setLayoutData(getGridData());
	}

	private void createButtonMSDField(Composite composite) {

		buttonMSD = new Button(composite, SWT.RADIO);
		buttonMSD.setText("MSD (Quadrupole, IonTrap, ...) data");
		buttonMSD.setSelection(wizardElements.isUseMassSpectrometryData());
		buttonMSD.setLayoutData(getGridData());
		buttonMSD.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				wizardElements.setUseMassSpectrometryData(true);
			}
		});
	}

	private void createButtonCSDField(Composite composite) {

		buttonCSD = new Button(composite, SWT.RADIO);
		buttonCSD.setText("CSD (FID, ECD, ...) data");
		buttonCSD.setSelection(!wizardElements.isUseMassSpectrometryData());
		buttonCSD.setLayoutData(getGridData());
		buttonCSD.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				wizardElements.setUseMassSpectrometryData(false);
			}
		});
	}

	private void createPeakIdentificationField(Composite composite) {

		checkBoxUseExistingPeaks = new Button(composite, SWT.CHECK);
		checkBoxUseExistingPeaks.setText("Use existing peaks in chromatogram if available.");
		checkBoxUseExistingPeaks.setEnabled(true);
		checkBoxUseExistingPeaks.setLayoutData(getGridData());
		checkBoxUseExistingPeaks.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				wizardElements.setUseAlreadyDetectedPeaks(checkBoxUseExistingPeaks.getSelection());
			}
		});
	}

	private GridData getGridData() {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.verticalIndent = 5;
		return gridData;
	}

	private void setCalibrationFileSelection(boolean enabled) {

		textPathRetentionIndexFile.setEnabled(enabled);
		buttonSelectCalibrationFile.setEnabled(enabled);
	}

	private void validateSelection() {

		String message = null;
		/*
		 * *.cal file
		 */
		if(wizardElements.isUseExistingRetentionIndexFile()) {
			String pathCalibrationFile = wizardElements.getPathRetentionIndexFile();
			if(pathCalibrationFile.equals("")) {
				message = "Please select an existing calibration (*.cal) file.";
			} else {
				File file = new File(pathCalibrationFile);
				if(!file.exists()) {
					message = "The selected *.cal doesn't exist.";
				}
			}
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}