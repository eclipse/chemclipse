/*******************************************************************************
 * Copyright (c) 2010, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.swt;

import java.text.NumberFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;

/**
 * @author Philip (eselmeister) Wenig
 */
public class NoiseMassSpectraUI extends Composite {

	private SimpleMassSpectrumUI noiseMassSpectrumUI;
	private List<ICombinedMassSpectrum> massSpectra;
	private ICombinedMassSpectrum actualMassSpectrum;
	private int index = 0;
	private Button buttonPrevious;
	private Button buttonNext;
	private Text textGoto;
	private Button buttonGoto;
	private Label labelDetails;
	private NumberFormat numberFormat;

	public NoiseMassSpectraUI(Composite parent, int style) {

		super(parent, style);
		// TODO store NumberFormat of retention time in another class e.g. in model
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(3);
		numberFormat.setMaximumFractionDigits(3);
		initialize(parent);
	}

	public void update(List<ICombinedMassSpectrum> massSpectra, boolean forceReload) {

		this.massSpectra = massSpectra;
		index = 0;
		setMassSpectrum(index);
	}

	// -----------------------------------------private methods
	/**
	 * Initializes the widget.
	 */
	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout;
		GridData gridData;
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// ------------------------------------------------------------------------------------------Buttons
		Composite buttonbar = new Composite(composite, SWT.FILL);
		buttonbar.setLayout(new FillLayout());
		/*
		 * Previous mass spectrum.
		 */
		buttonPrevious = new Button(buttonbar, SWT.NONE);
		buttonPrevious.setText("Previous");
		buttonPrevious.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setPreviousMassSpectrum();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		/*
		 * Next mass spectrum.
		 */
		buttonNext = new Button(buttonbar, SWT.NONE);
		buttonNext.setText("Next");
		buttonNext.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setNextMassSpectrum();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		textGoto = new Text(buttonbar, SWT.BORDER);
		textGoto.setText("1");
		textGoto.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * If the user has released the enter key.
				 */
				if(e.keyCode == 13 | e.keyCode == 16777296) {
					setSelectedMassSpectrum(textGoto.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		/*
		 * Next mass spectrum.
		 */
		buttonGoto = new Button(buttonbar, SWT.NONE);
		buttonGoto.setText("Select");
		buttonGoto.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setSelectedMassSpectrum(textGoto.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		// ------------------------------------------------------------------------------------------Infos
		labelDetails = new Label(composite, SWT.NONE);
		labelDetails.setText("(0) 0 | Scan Range: | RT: ");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		labelDetails.setLayoutData(gridData);
		// ------------------------------------------------------------------------------------------Mass
		// Spectrum
		noiseMassSpectrumUI = new SimpleMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, MassValueDisplayPrecision.NOMINAL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		noiseMassSpectrumUI.setLayoutData(gridData);
	}

	private final void setPreviousMassSpectrum() {

		index--;
		int min = 0;
		if(index < min) {
			index = min;
		}
		setMassSpectrum(index);
	}

	private final void setNextMassSpectrum() {

		index++;
		int max = massSpectra.size() - 1;
		if(index > max) {
			index = max;
		}
		setMassSpectrum(index);
	}

	private final void setSelectedMassSpectrum(String number) {

		try {
			int selection = Integer.parseInt(number) - 1;
			index = selection;
			int min = 0;
			int max = massSpectra.size() - 1;
			if(index > max) {
				index = max;
			}
			if(index < min) {
				index = min;
			}
			setMassSpectrum(index);
		} catch(NumberFormatException e) {
			// do nothing
		}
	}

	/**
	 * Sets the mass spectrum.
	 */
	private void setMassSpectrum(int index) {

		int min = 0;
		int max = massSpectra.size() - 1;
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
		ICombinedMassSpectrum massSpectrum = this.massSpectra.get(index);
		if(massSpectrum != null) {
			actualMassSpectrum = massSpectrum;
			setDetailsLabel();
			noiseMassSpectrumUI.update(massSpectrum, true);
		}
	}

	private void setDetailsLabel() {

		int number = index + 1;
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(number);
		builder.append(") ");
		builder.append(massSpectra.size());
		builder.append(" | ");
		builder.append("Scan Range: ");
		builder.append(actualMassSpectrum.getStartScan());
		builder.append(" - ");
		builder.append(actualMassSpectrum.getStopScan());
		builder.append(" | ");
		builder.append("RT: ");
		builder.append(numberFormat.format(actualMassSpectrum.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" - ");
		builder.append(numberFormat.format(actualMassSpectrum.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		labelDetails.setText(builder.toString());
	}
}
