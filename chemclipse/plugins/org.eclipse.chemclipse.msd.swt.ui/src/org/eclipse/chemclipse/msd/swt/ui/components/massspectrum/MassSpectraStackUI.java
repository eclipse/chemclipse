/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MassSpectraStackUI extends Composite {

	private SimpleMassSpectrumUI noiseMassSpectrumUI;
	//
	private IMassSpectra massSpectra;
	private IScanMSD actualMassSpectrum;
	private int index = 1;
	//
	private Button buttonPrevious;
	private Button buttonNext;
	private Label labelDetails;
	private DecimalFormat decimalFormat;

	public MassSpectraStackUI(Composite parent, int style) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
		initialize(parent);
	}

	public void update(IMassSpectra massSpectra, boolean forceReload) {

		this.massSpectra = massSpectra;
		index = 1;
		setMassSpectrum(index);
	}

	// -----------------------------------------private methods
	/**
	 * Initializes the widget.
	 */
	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new GridLayout(1, true));
		/*
		 * Buttons
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		compositeButtons.setLayoutData(gridData);
		compositeButtons.setLayout(new GridLayout(2, false));
		/*
		 * Previous mass spectrum.
		 */
		buttonPrevious = new Button(compositeButtons, SWT.NONE);
		buttonPrevious.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREVIOUS, IApplicationImage.SIZE_16x16));
		buttonPrevious.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setPreviousMassSpectrum();
			}
		});
		/*
		 * Next mass spectrum.
		 */
		buttonNext = new Button(compositeButtons, SWT.NONE);
		buttonNext.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_NEXT, IApplicationImage.SIZE_16x16));
		buttonNext.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setNextMassSpectrum();
			}
		});
		/*
		 * Mass Spectrum Info
		 */
		labelDetails = new Label(composite, SWT.NONE);
		labelDetails.setText("");
		labelDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Mass Spectrum
		 */
		noiseMassSpectrumUI = new SimpleMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, MassValueDisplayPrecision.NOMINAL);
		noiseMassSpectrumUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private final void setPreviousMassSpectrum() {

		index--;
		int min = 1;
		if(index < min) {
			index = min;
		}
		setMassSpectrum(index);
	}

	private final void setNextMassSpectrum() {

		index++;
		int max = massSpectra.size();
		if(index > max) {
			index = max;
		}
		setMassSpectrum(index);
	}

	/**
	 * Sets the mass spectrum.
	 */
	private void setMassSpectrum(int index) {

		int min = 1;
		int max = massSpectra.size();
		//
		if(index == min) {
			buttonPrevious.setEnabled(false);
			buttonNext.setEnabled(true);
		} else if(index == max) {
			buttonPrevious.setEnabled(true);
			buttonNext.setEnabled(false);
		} else {
			buttonPrevious.setEnabled(true);
			buttonNext.setEnabled(true);
		}
		/*
		 * Set the next mass spectrum.
		 */
		IScanMSD massSpectrum = massSpectra.getMassSpectrum(index);
		if(massSpectrum != null) {
			actualMassSpectrum = massSpectrum;
			setLabelDetails();
			noiseMassSpectrumUI.update(massSpectrum, true);
		}
	}

	private void setLabelDetails() {

		/*
		 * Mass Spectrum
		 */
		StringBuilder builder = new StringBuilder();
		IChromatogram chromatogram = actualMassSpectrum.getParentChromatogram();
		if(chromatogram != null) {
			builder.append("Chromatogram: ");
			builder.append(chromatogram.getName());
			builder.append(" | ");
		}
		builder.append("RT: ");
		builder.append(decimalFormat.format(actualMassSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" | ");
		builder.append("RI: ");
		if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
			builder.append(Integer.toString((int)actualMassSpectrum.getRetentionIndex()));
		} else {
			builder.append(decimalFormat.format(actualMassSpectrum.getRetentionIndex()));
		}
		builder.append(" | ");
		builder.append("TIC: ");
		builder.append(actualMassSpectrum.getTotalSignal());
		labelDetails.setText(builder.toString());
	}
}
