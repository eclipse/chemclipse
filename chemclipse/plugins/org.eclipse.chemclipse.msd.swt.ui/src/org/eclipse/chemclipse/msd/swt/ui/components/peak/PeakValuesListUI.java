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
package org.eclipse.chemclipse.msd.swt.ui.components.peak;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.msd.model.core.AbstractChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.IChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

public class PeakValuesListUI extends Composite implements IChromatogramSelectionMSDUpdateNotifier {

	private List list;
	private Clipboard clipboard;
	private DecimalFormat decimalFormat;
	private StringBuilder stringBuilder;

	public PeakValuesListUI(Composite parent, int style) {
		super(parent, style);
		decimalFormat = new DecimalFormat();
		stringBuilder = new StringBuilder();
		initialize(parent);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Do not use force reload, as we reload the values each time.
		 */
		IChromatogramPeakMSD peak = chromatogramSelection.getSelectedPeak();
		if(peak != null) {
			setPeakValues(peak);
		}
	}

	// -----------------------------------------private methods
	/**
	 * Initializes the widget.
	 */
	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		clipboard = new Clipboard(Display.getDefault());
		list = new List(this, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * Copy the whole selection.
					 */
					StringBuilder builder = new StringBuilder();
					for(String selection : list.getSelection()) {
						builder.append(selection);
						builder.append("\n");
					}
					/*
					 * If the builder is empty, give a note that items needs to
					 * be selected.
					 */
					if(builder.length() == 0) {
						builder.append("Please select one or more entries in the list.\n");
					}
					/*
					 * Transfer the selected text (items) to the clipboard.
					 */
					TextTransfer textTransfer = TextTransfer.getInstance();
					Object[] data = new Object[]{builder.toString()};
					Transfer[] dataTypes = new Transfer[]{textTransfer};
					clipboard.setContents(data, dataTypes);
				}
			}
		});
	}

	/**
	 * Set the peak values.
	 */
	private void setPeakValues(IChromatogramPeakMSD peak) {

		list.removeAll();
		/*
		 * Fill the list.
		 */
		IPeakModelMSD peakModel = peak.getPeakModel();
		list.add("PeakType: " + peak.getPeakType());
		list.add("Purity: " + decimalFormat.format(peak.getPurity()));
		LinearEquation equation;
		equation = peakModel.getIncreasingInflectionPointEquation();
		list.add(getEquationString("Increasing Inflection Point Equation: ", equation));
		equation = peakModel.getDecreasingInflectionPointEquation();
		list.add(getEquationString("Decreasing Inflection Point Equation: ", equation));
		equation = peakModel.getPercentageHeightBaselineEquation(0.5f);
		list.add(getEquationString("Percentage Baseline Equation 50%: ", equation));
		list.add("Gradient Angle: " + decimalFormat.format(peakModel.getGradientAngle()));
		list.add("-------------------------");
		addRetentionTimes(peakModel);
	}

	private void addRetentionTimes(IPeakModelMSD peakModel) {

		double minutes;
		float abundance;
		float background;
		list.add("milliseconds  -  abundance  -  background  -  minutes");
		for(int retentionTime : peakModel.getRetentionTimes()) {
			minutes = retentionTime / AbstractChromatogramMSD.MINUTE_CORRELATION_FACTOR;
			abundance = peakModel.getPeakAbundance(retentionTime);
			background = peakModel.getBackgroundAbundance(retentionTime);
			clearStringBuilder();
			stringBuilder.append(decimalFormat.format(retentionTime));
			stringBuilder.append(" - ");
			stringBuilder.append(decimalFormat.format(abundance));
			stringBuilder.append(" - ");
			stringBuilder.append(decimalFormat.format(background));
			stringBuilder.append(" - ");
			stringBuilder.append(decimalFormat.format(minutes));
			list.add(stringBuilder.toString());
		}
	}

	private String getEquationString(String description, LinearEquation equation) {

		clearStringBuilder();
		stringBuilder.append(description);
		double b = equation.getB();
		stringBuilder.append("f(x)=");
		stringBuilder.append(decimalFormat.format(equation.getA()));
		/*
		 * B
		 * Don't display +-
		 */
		if(b < 0) {
			stringBuilder.append("x");
		} else {
			stringBuilder.append("x+");
		}
		stringBuilder.append(decimalFormat.format(equation.getB()));
		return stringBuilder.toString();
	}

	private void clearStringBuilder() {

		stringBuilder.delete(0, stringBuilder.length());
	}
	// -----------------------------------------private methods
}
