/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LibraryMassSpectrumComparisonUI extends Composite {

	private static final Logger logger = Logger.getLogger(LibraryMassSpectrumComparisonUI.class);
	//
	private Label infoLabelUnknown;
	private SimpleMirroredMassSpectrumUI mirroredMassSpectrumUI;
	private Label infoLabelLibrary;
	//
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public LibraryMassSpectrumComparisonUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0####");
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		initialize(parent);
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		//
		infoLabelUnknown = new Label(composite, SWT.NONE);
		infoLabelUnknown.setText("");
		infoLabelUnknown.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		mirroredMassSpectrumUI = new SimpleMirroredMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		mirroredMassSpectrumUI.setLayoutData(gridData);
		//
		infoLabelLibrary = new Label(composite, SWT.NONE);
		infoLabelLibrary.setText("");
		infoLabelLibrary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void update(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

		if(unknownMassSpectrum != null && libraryMassSpectrum != null) {
			try {
				IScanMSD unknownMassSpectrumCopy = unknownMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD libraryMassSpectrumCopy = libraryMassSpectrum.makeDeepCopy().normalize(1000.0f);
				//
				setMassSpectrumLabel(unknownMassSpectrumCopy, libraryMassSpectrumCopy);
				mirroredMassSpectrumUI.update(unknownMassSpectrumCopy, libraryMassSpectrumCopy, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			mirroredMassSpectrumUI.update(null, null, true);
		}
	}

	private void setMassSpectrumLabel(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum) {

		setMassSpectrumLabel(unknownMassSpectrum, "UNKNOWN MS = ", infoLabelUnknown);
		setMassSpectrumLabel(libraryMassSpectrum, "LIBRARY MS = ", infoLabelLibrary);
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum, String title, Label label) {

		StringBuilder builder = new StringBuilder();
		builder.append(title);
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			builder.append("NAME: ");
			builder.append(libraryInformation.getName());
			builder.append(" | ");
			builder.append("CAS: ");
			builder.append(libraryInformation.getCasNumber());
			builder.append(" | ");
		}
		builder.append("RT: ");
		builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		builder.append(" | ");
		builder.append("RI: ");
		if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
			builder.append(Integer.toString((int)massSpectrum.getRetentionIndex()));
		} else {
			builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
		}
		label.setText(builder.toString());
	}
}
