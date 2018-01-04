/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumStackUI extends Composite {

	private static final Logger logger = Logger.getLogger(MassSpectrumStackUI.class);
	//
	private StackedMassSpectrumUI stackedMassSpectrumReference;
	private StackedMassSpectrumUI stackedMassSpectrumComparison;
	//
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	//
	private String labelReference = "";
	private String labelComparison = "";
	//
	public static final int MAX_LENGTH_NAME = 20;

	public MassSpectrumStackUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision, String labelReference, String labelComparison) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		/*
		 * Mass spectrum type, nominal or accurate
		 */
		this.massValueDisplayPrecision = massValueDisplayPrecision;
		this.labelReference = labelReference;
		this.labelComparison = labelComparison;
		//
		initialize(parent);
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		//
		stackedMassSpectrumReference = new StackedMassSpectrumUI(composite, SWT.FILL, massValueDisplayPrecision);
		stackedMassSpectrumComparison = new StackedMassSpectrumUI(composite, SWT.FILL, massValueDisplayPrecision);
		stackedMassSpectrumReference.setOtherStackedMassSpectrumUI(stackedMassSpectrumComparison);
		stackedMassSpectrumComparison.setOtherStackedMassSpectrumUI(stackedMassSpectrumReference);
	}

	public void update(IScanMSD referenceMassSpectrum, IScanMSD comparisonMassSpectrum, boolean forceReload) {

		if(referenceMassSpectrum != null && comparisonMassSpectrum != null) {
			try {
				IScanMSD referenceMassSpectrumCopy = referenceMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD comparisonMassSpectrumCopy = comparisonMassSpectrum.makeDeepCopy().normalize(1000.0f);
				//
				IExtractedIonSignal unknownMS = referenceMassSpectrumCopy.getExtractedIonSignal();
				IExtractedIonSignal libraryMS = comparisonMassSpectrumCopy.getExtractedIonSignal();
				int startMZ = ((unknownMS.getStartIon() < libraryMS.getStartIon()) ? unknownMS.getStartIon() : libraryMS.getStartIon()) - 1;
				int stopMZ = ((unknownMS.getStopIon() > libraryMS.getStopIon()) ? unknownMS.getStopIon() : libraryMS.getStopIon()) + 1;
				stackedMassSpectrumReference.setFixedAxisRangeX(startMZ, stopMZ);
				stackedMassSpectrumComparison.setFixedAxisRangeX(startMZ, stopMZ);
				//
				setMassSpectrumLabel(referenceMassSpectrumCopy, comparisonMassSpectrumCopy);
				stackedMassSpectrumReference.update(referenceMassSpectrumCopy, forceReload);
				stackedMassSpectrumComparison.update(comparisonMassSpectrumCopy, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			stackedMassSpectrumReference.update(null, true);
			stackedMassSpectrumComparison.update(null, true);
		}
	}

	private void setMassSpectrumLabel(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum) {

		setMassSpectrumLabel(unknownMassSpectrum, labelReference + " MS = ", stackedMassSpectrumReference);
		setMassSpectrumLabel(libraryMassSpectrum, labelComparison + " MS = ", stackedMassSpectrumComparison);
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum, String title, StackedMassSpectrumUI stackedMassSpectrumUI) {

		StringBuilder builder = new StringBuilder();
		builder.append("m/z [");
		builder.append(title);
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			builder.append("NAME: ");
			String name = libraryInformation.getName();
			if(name.length() > MAX_LENGTH_NAME) {
				builder.append(name.substring(0, MAX_LENGTH_NAME));
				builder.append("...");
			} else {
				builder.append(name);
			}
		} else {
			builder.append("RT: ");
			builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		}
		builder.append(" RI:");
		builder.append(Integer.toString((int)massSpectrum.getRetentionIndex()));
		builder.append("]");
		//
		stackedMassSpectrumUI.setAxisTitle(SWT.BOTTOM, builder.toString());
	}
}