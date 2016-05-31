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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO merge with ProfileMassSpectrumUIWithLabel
 */
public class ScanMassSpectrumUIWithLabel extends Composite implements IChromatogramSelectionMSDUpdateNotifier {

	private ScanMassSpectrumUI massSpectrumUI;
	private Label label;
	private IScanMSD massSpectrum;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public ScanMassSpectrumUIWithLabel(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0##");
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		initialize(parent);
	}

	private void initialize(Composite parent) {

		GridLayout layout;
		GridData gridData;
		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// -------------------------------------------Label
		Composite labelbar = new Composite(composite, SWT.FILL);
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		labelbar.setLayout(layout);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		labelbar.setLayoutData(gridData);
		/*
		 * The label with scan, retention time and retention index.
		 */
		label = new Label(labelbar, SWT.NONE);
		label.setText("");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(gridData);
		// -------------------------------------------MassSpectrum
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
				setMassSpectrumLabel(massSpectrum);
				massSpectrumUI.update(chromatogramSelection, forceReload);
			}
		}
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Check if the mass spectrum is a scan.
		 */
		builder.append("Scan: ");
		builder.append(massSpectrum.getScanNumber());
		builder.append(" | ");
		builder.append("RT: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" | ");
		builder.append("RI: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
		builder.append(" | ");
		if(massSpectrum instanceof IVendorMassSpectrum) {
			IVendorMassSpectrum actualMassSpectrum = (IVendorMassSpectrum)massSpectrum;
			builder.append("Detector: MS");
			builder.append(actualMassSpectrum.getMassSpectrometer());
			builder.append(" | ");
			builder.append("Type: ");
			builder.append(actualMassSpectrum.getMassSpectrumTypeDescription());
			builder.append(" | ");
		}
		builder.append("Signal: ");
		builder.append((int)massSpectrum.getTotalSignal());
		/*
		 * Set the label text.
		 */
		label.setText(builder.toString());
	}
}
