/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexExtractor;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.ExtendedRetentionIndexTableViewerUI;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.RetentionIndexTableViewerUI;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.SelectedPeakChromatogramUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class PageCalibrationTableMSD extends AbstractExtendedWizardPage {

	private IRetentionIndexWizardElements wizardElements;
	//
	private Button checkBoxValidateRetentionIndices;
	private SelectedPeakChromatogramUI selectedPeakChromatogramUI;
	private ExtendedRetentionIndexTableViewerUI extendedRetentionIndexTableViewerUI;

	public PageCalibrationTableMSD(IRetentionIndexWizardElements wizardElements) {
		//
		super(PageCalibrationTableMSD.class.getName());
		setTitle("Calibration Table");
		setDescription("Please verify the calibration table.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		if(wizardElements.isRetentionIndexDataValidated()) {
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
			IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
			if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getChromatogramMSD() != null) {
				IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
				selectedPeakChromatogramUI.updateSelection(chromatogramSelectionMSD, true);
				RetentionIndexExtractor retentionIndexExtractor = new RetentionIndexExtractor();
				List<IRetentionIndexEntry> extractedRetentionIndexEntries = retentionIndexExtractor.extract(chromatogramMSD);
				wizardElements.setExtractedRetentionIndexEntries(extractedRetentionIndexEntries);
				extendedRetentionIndexTableViewerUI.setInput(extractedRetentionIndexEntries);
			}
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		createCheckBoxField(composite);
		createChromatogramField(composite);
		createTableField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createCheckBoxField(Composite composite) {

		checkBoxValidateRetentionIndices = new Button(composite, SWT.CHECK);
		checkBoxValidateRetentionIndices.setText("Retention indices are valid.");
		checkBoxValidateRetentionIndices.setSelection(false);
		checkBoxValidateRetentionIndices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkBoxValidateRetentionIndices.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				wizardElements.setRetentionIndexDataIsValidated(checkBoxValidateRetentionIndices.getSelection());
				validateSelection();
			}
		});
	}

	private void createChromatogramField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new FillLayout());
		selectedPeakChromatogramUI = new SelectedPeakChromatogramUI(parent, SWT.BORDER);
	}

	private void createTableField(Composite composite) {

		extendedRetentionIndexTableViewerUI = new ExtendedRetentionIndexTableViewerUI(composite, SWT.NONE);
		extendedRetentionIndexTableViewerUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		RetentionIndexTableViewerUI retentionIndexTableViewerUI = extendedRetentionIndexTableViewerUI.getRetentionIndexTableViewerUI();
		retentionIndexTableViewerUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = retentionIndexTableViewerUI.getTable();
				int index = table.getSelectionIndex();
				TableItem tableItem = table.getItem(index);
				Object object = tableItem.getData();
				if(object instanceof IRetentionIndexEntry) {
					IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)object;
					int retentionTime = retentionIndexEntry.getRetentionTime();
					IChromatogramSelectionMSD chromatogramSelectionMSD = wizardElements.getChromatogramSelectionMSD();
					if(chromatogramSelectionMSD != null && chromatogramSelectionMSD.getChromatogramMSD() != null) {
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
						IChromatogramPeakMSD selectedPeak = getSelectedPeak(chromatogramMSD, retentionTime);
						if(selectedPeak != null) {
							chromatogramSelectionMSD.setSelectedPeak(selectedPeak);
							selectedPeakChromatogramUI.updateSelection(chromatogramSelectionMSD, true);
						}
					}
				}
			}
		});
	}

	/**
	 * May return null.
	 * 
	 * @param chromatogramMSD
	 * @param retentionTime
	 * @return {@link IChromatogramPeakMSD}
	 */
	private IChromatogramPeakMSD getSelectedPeak(IChromatogramMSD chromatogramMSD, int retentionTime) {

		for(IChromatogramPeakMSD peak : chromatogramMSD.getPeaks()) {
			if(peak.getPeakModel().getRetentionTimeAtPeakMaximum() == retentionTime) {
				return peak;
			}
		}
		return null;
	}

	private void validateSelection() {

		String message = null;
		if(!checkBoxValidateRetentionIndices.getSelection()) {
			message = "Please verify the data and activate the check box.";
		}
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
