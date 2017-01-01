/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.swt;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.FilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CombinedMassSpectrumUI extends Composite {

	private DecimalFormat decimalFormat;
	private Label label;
	private SimpleMassSpectrumUI simpleMassSpectrumUI;

	public CombinedMassSpectrumUI(Composite parent, int style) {
		super(parent, style);
		initialize(parent);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0##");
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout;
		GridData gridData;
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
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
		// ------------------------------------------------------------------------------------------
		// Mass Spectrum
		simpleMassSpectrumUI = new SimpleMassSpectrumUI(composite, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		simpleMassSpectrumUI.setLayoutData(gridData);
	}

	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		if(chromatogramSelection != null) {
			boolean useNormalize = PreferenceSupplier.isUseNormalize();
			IScanMSD massSpectrum = FilterSupport.getCombinedMassSpectrum(chromatogramSelection, null, useNormalize);
			setMassSpectrumLabel(chromatogramSelection);
			simpleMassSpectrumUI.update(massSpectrum, forceReload);
		}
	}

	private void setMassSpectrumLabel(IChromatogramSelectionMSD chromatogramSelection) {

		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		StringBuilder builder = new StringBuilder();
		builder.append("Scan range: ");
		builder.append(chromatogramSelection.getChromatogram().getScanNumber(startRetentionTime));
		builder.append("–");
		builder.append(chromatogramSelection.getChromatogram().getScanNumber(stopRetentionTime));
		builder.append(" | RT range: ");
		builder.append(decimalFormat.format((double)startRetentionTime / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append("–");
		builder.append(decimalFormat.format((double)stopRetentionTime / IChromatogram.MINUTE_CORRELATION_FACTOR));
		label.setText(builder.toString());
	}
}
