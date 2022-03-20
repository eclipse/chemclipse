/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.impl.RetentionIndexExtractor;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.ri.io.StandardsReader;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.ExtendedRetentionIndexListUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.RetentionIndexTableViewerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.ChromatogramPeakChart;
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

public class PageCalibrationTable extends AbstractExtendedWizardPage {

	private RetentionIndexWizardElements wizardElements;
	//
	private Button checkBoxValidateRetentionIndices;
	private ChromatogramPeakChart chromatogramPeakChart;
	private ExtendedRetentionIndexListUI extendedRetentionIndexTableViewerUI;

	public PageCalibrationTable(RetentionIndexWizardElements wizardElements) {

		super(PageCalibrationTable.class.getName());
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

	@SuppressWarnings("rawtypes")
	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			IChromatogramSelection chromatogramSelection = wizardElements.getChromatogramSelection();
			if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				updateChromatogramChart(chromatogramSelection);
				RetentionIndexExtractor retentionIndexExtractor = new RetentionIndexExtractor();
				ISeparationColumnIndices separationColumnIndices = retentionIndexExtractor.extract(chromatogram);
				wizardElements.setSeparationColumnIndices(separationColumnIndices);
				extendedRetentionIndexTableViewerUI.setInput(separationColumnIndices);
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
		chromatogramPeakChart = new ChromatogramPeakChart(parent, SWT.BORDER);
	}

	private void createTableField(Composite composite) {

		extendedRetentionIndexTableViewerUI = new ExtendedRetentionIndexListUI(composite, SWT.NONE);
		extendedRetentionIndexTableViewerUI.setInput(new StandardsReader().getStandardsList());
		extendedRetentionIndexTableViewerUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		RetentionIndexTableViewerUI retentionIndexTableViewerUI = extendedRetentionIndexTableViewerUI.getRetentionIndexTableViewerUI();
		retentionIndexTableViewerUI.getTable().addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings({"rawtypes", "unchecked"})
			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = retentionIndexTableViewerUI.getTable();
				int index = table.getSelectionIndex();
				TableItem tableItem = table.getItem(index);
				Object object = tableItem.getData();
				if(object instanceof IRetentionIndexEntry) {
					IRetentionIndexEntry retentionIndexEntry = (IRetentionIndexEntry)object;
					int retentionTime = retentionIndexEntry.getRetentionTime();
					IChromatogramSelection chromatogramSelection = wizardElements.getChromatogramSelection();
					if(chromatogramSelection != null && chromatogramSelection.getChromatogram() != null) {
						IChromatogram chromatogram = chromatogramSelection.getChromatogram();
						IPeak selectedPeak = getSelectedPeak(chromatogram, retentionTime);
						if(selectedPeak != null) {
							chromatogramSelection.setSelectedPeak(selectedPeak);
							List<IPeak> selectedPeaks = new ArrayList<>();
							selectedPeaks.add(selectedPeak);
							updateSelectedPeaksInChart(selectedPeaks);
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
	private IPeak getSelectedPeak(IChromatogram<? extends IPeak> chromatogram, int retentionTime) {

		for(IPeak peak : chromatogram.getPeaks()) {
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

	@SuppressWarnings("rawtypes")
	private void updateChromatogramChart(IChromatogramSelection chromatogramSelection) {

		chromatogramPeakChart.updateChromatogram(chromatogramSelection);
	}

	private void updateSelectedPeaksInChart(List<IPeak> selectedPeaks) {

		chromatogramPeakChart.updatePeaks(selectedPeaks);
	}
}
