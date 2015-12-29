/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
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
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO merge with ProfileMassSpectrumUIWithLabel
 */
public class ScanMassSpectrumCycleNumberCombinedUIWithLabel extends Composite implements IChromatogramSelectionMSDUpdateNotifier {

	private ScanMassSpectrumUI massSpectrumUI;
	private Label label;
	private IScanMSD massSpectrum;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public ScanMassSpectrumCycleNumberCombinedUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
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
		labelbar.setLayout(new GridLayout(1, false));
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
				IScanMSD massSpectrum = chromatogramSelection.getSelectedScan();
				IChromatogram chromatogram = chromatogramSelection.getChromatogram();
				if(chromatogram != null && massSpectrum != null) {
					int cycleNumber = massSpectrum.getCycleNumber();
					if(cycleNumber > 1) {
						List<IScan> scans = chromatogram.getScanCycleScans(cycleNumber);
						IScanMSD massSpectrumCycleNumber = new ScanMSD();
						for(IScan scan : scans) {
							if(scan instanceof IScanMSD) {
								IScanMSD scanMSD = (IScanMSD)scan;
								List<IIon> ions = scanMSD.getIons();
								for(IIon ion : ions) {
									massSpectrumCycleNumber.addIon(ion, false);
								}
							}
						}
						setMassSpectrumLabel(massSpectrumCycleNumber, scans.size());
						massSpectrumUI.update(massSpectrumCycleNumber, forceReload);
					} else {
						setMassSpectrumLabel(massSpectrum, 1);
						massSpectrumUI.update(massSpectrum, forceReload);
					}
				}
			}
		}
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum, int sizeCycleNumberScans) {

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
		builder.append("Cyle Number Scans (");
		builder.append(sizeCycleNumberScans);
		builder.append(")");
		/*
		 * Set the label text.
		 */
		label.setText(builder.toString());
	}
}
