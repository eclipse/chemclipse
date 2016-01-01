/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO merge with ProfileMassSpectrumUIWithLabel
 */
public class ScanMassSpectrumCycleNumberUIWithLabel extends Composite implements IChromatogramSelectionMSDUpdateNotifier {

	private ScanMassSpectrumUI massSpectrumUI;
	private Label label;
	private Button buttonPreviousScan;
	private Button buttonNextScan;
	private IScanMSD massSpectrum;
	private int selectedIndex;
	private List<IScan> scanCycleScans;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public ScanMassSpectrumCycleNumberUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = new DecimalFormat("0.0##");
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		initialize(parent);
	}

	private void initialize(Composite parent) {

		GridData gridData;
		//
		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new GridLayout(1, true));
		// -------------------------------------------Label
		Composite labelbar = new Composite(composite, SWT.FILL);
		labelbar.setLayout(new GridLayout(3, false));
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		labelbar.setLayoutData(gridData);
		/*
		 * The label with scan, retention time and retention index.
		 */
		label = new Label(labelbar, SWT.NONE);
		label.setText("");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		//
		buttonPreviousScan = new Button(labelbar, SWT.PUSH);
		buttonPreviousScan.setText("Previous Scan");
		buttonPreviousScan.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectedIndex--;
				if(selectedIndex < 0) {
					selectedIndex = 0;
				}
				updateButtonsAndSetCycleScan(selectedIndex);
			}
		});
		//
		buttonNextScan = new Button(labelbar, SWT.PUSH);
		buttonNextScan.setText("Next Scan");
		buttonNextScan.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectedIndex++;
				if(scanCycleScans != null && selectedIndex >= scanCycleScans.size()) {
					selectedIndex = scanCycleScans.size() - 1;
				}
				updateButtonsAndSetCycleScan(selectedIndex);
			}
		});
		// -------------------------------------------CycleNumber MassSpectra
		massSpectrumUI = new ScanMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		massSpectrumUI.setLayoutData(gridData);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(chromatogramSelection != null) {
			/*
			 * Do not load the same mass spectrum twice if it has already been
			 * loaded.
			 */
			if(massSpectrum != chromatogramSelection.getSelectedScan()) {
				massSpectrum = chromatogramSelection.getSelectedScan();
				int cycleNumber = massSpectrum.getCycleNumber();
				scanCycleScans = chromatogramSelection.getChromatogram().getScanCycleScans(cycleNumber);
				selectedIndex = 0;
				updateButtonsAndSetCycleScan(selectedIndex);
			}
		}
	}

	private void updateButtonsAndSetCycleScan(int selectedIndex) {

		if(scanCycleScans != null) {
			if(scanCycleScans.size() <= 1) {
				buttonPreviousScan.setEnabled(false);
				buttonNextScan.setEnabled(false);
			} else {
				if(selectedIndex == 0) {
					buttonPreviousScan.setEnabled(false);
					buttonNextScan.setEnabled(true);
				} else if(selectedIndex == scanCycleScans.size() - 1) {
					buttonPreviousScan.setEnabled(true);
					buttonNextScan.setEnabled(false);
				} else {
					buttonPreviousScan.setEnabled(true);
					buttonNextScan.setEnabled(true);
				}
			}
			setSelectedScanCycleScan(selectedIndex);
		}
	}

	private void setSelectedScanCycleScan(int selectedIndex) {

		if(scanCycleScans != null && selectedIndex >= 0 && selectedIndex < scanCycleScans.size()) {
			IScan scan = scanCycleScans.get(selectedIndex);
			if(scan instanceof IScanMSD) {
				IScanMSD massSpectrumScanCycle = (IScanMSD)scan;
				setMassSpectrumLabel(massSpectrumScanCycle, selectedIndex + 1, scanCycleScans.size());
				massSpectrumUI.update(massSpectrumScanCycle, true);
			}
		}
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum, int selectedScan, int sizeCycleNumberScans) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Check if the mass spectrum is a scan.
		 */
		builder.append("Cycle Number: ");
		builder.append(massSpectrum.getCycleNumber());
		builder.append(" | ");
		builder.append("Scan: ");
		builder.append(massSpectrum.getScanNumber());
		builder.append(" | ");
		builder.append("RT: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" | ");
		builder.append("Signal: ");
		builder.append(decimalFormat.format(massSpectrum.getTotalSignal()));
		builder.append(" | ");
		builder.append("Time Segment Id: ");
		builder.append(massSpectrum.getTimeSegmentId());
		builder.append(" | ");
		builder.append("Cyle (");
		builder.append(selectedScan);
		builder.append("/");
		builder.append(sizeCycleNumberScans);
		builder.append(")");
		/*
		 * Set the label text.
		 */
		label.setText(builder.toString());
	}
}
