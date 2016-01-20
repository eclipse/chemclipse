/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LibraryMassSpectrumComparisonUI extends Composite {

	private SimpleMirroredMassSpectrumUI mirroredMassSpectrumUI;
	private Label infoLabel;
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public LibraryMassSpectrumComparisonUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = new DecimalFormat("0.0####");
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
		labelbar.setLayout(new GridLayout(2, false));
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		labelbar.setLayoutData(gridData);
		/*
		 * The label with scan, retention time and retention index.
		 */
		infoLabel = new Label(labelbar, SWT.NONE);
		infoLabel.setText("");
		infoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// -------------------------------------------MassSpectrum
		mirroredMassSpectrumUI = new SimpleMirroredMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		mirroredMassSpectrumUI.setLayoutData(gridData);
	}

	public void update(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

		if(unknownMassSpectrum != null && libraryMassSpectrum != null) {
			setMassSpectrumLabel(unknownMassSpectrum, libraryMassSpectrum);
			mirroredMassSpectrumUI.update(unknownMassSpectrum, libraryMassSpectrum, forceReload);
		}
	}

	private void setMassSpectrumLabel(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Check if the mass spectrum is a scan.
		 */
		addMassSpectrumLabelData(unknownMassSpectrum, builder);
		builder.append(" vs. ");
		addMassSpectrumLabelData(libraryMassSpectrum, builder);
		/*
		 * Set the label text.
		 */
		infoLabel.setText(builder.toString());
	}

	private void addMassSpectrumLabelData(IScanMSD massSpectrum, StringBuilder builder) {

		builder.append("RT: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" | ");
		builder.append("RI: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
	}
}
