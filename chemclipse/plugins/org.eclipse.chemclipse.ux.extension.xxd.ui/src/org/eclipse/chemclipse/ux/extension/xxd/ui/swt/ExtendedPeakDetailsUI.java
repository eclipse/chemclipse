/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.msd.model.core.AbstractChromatogramMSD;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class ExtendedPeakDetailsUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	//
	private List list;
	private Clipboard clipboard;
	private DecimalFormat decimalFormat;
	private StringBuilder stringBuilder;
	//
	private IPeak peak;
	//
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	public ExtendedPeakDetailsUI(Composite parent, int style) {

		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
		stringBuilder = new StringBuilder();
		createControl();
	}

	public boolean setFocus() {

		updatePeak();
		return true;
	}

	public void update(IPeak peak) {

		this.peak = peak;
		toolbarInfo.get().setText(peakDataSupport.getPeakLabel(peak));
		updatePeak();
	}

	private void updatePeak() {

		if(peak != null) {
			setPeakValues(peak);
		} else {
			list.removeAll();
		}
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createPeakList(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createPeakList(Composite parent) {

		clipboard = new Clipboard(DisplayUtils.getDisplay());
		list = new List(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
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
	private void setPeakValues(IPeak peak) {

		list.removeAll();
		/*
		 * Fill the list.
		 */
		IPeakModel peakModel = peak.getPeakModel();
		list.add("PeakType: " + peak.getPeakType());
		// list.add("Purity: " + decimalFormat.format(peak.getPurity()));
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

	private void addRetentionTimes(IPeakModel peakModel) {

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
}
