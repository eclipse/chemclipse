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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LibraryMassSpectrumDifferenceUI extends Composite {

	private static final Logger logger = Logger.getLogger(LibraryMassSpectrumDifferenceUI.class);
	//
	private Label infoLabelPositive;
	private SimpleMirroredMassSpectrumUI mirroredMassSpectrumUI;
	private Label infoLabelNegative;
	//
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public LibraryMassSpectrumDifferenceUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
		super(parent, style);
		decimalFormat = new DecimalFormat("0.0####");
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
		infoLabelPositive = new Label(composite, SWT.NONE);
		infoLabelPositive.setText("");
		infoLabelPositive.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		mirroredMassSpectrumUI = new SimpleMirroredMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		mirroredMassSpectrumUI.setLayoutData(gridData);
		//
		infoLabelNegative = new Label(composite, SWT.NONE);
		infoLabelNegative.setText("");
		infoLabelNegative.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void update(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

		if(unknownMassSpectrum != null && libraryMassSpectrum != null) {
			try {
				IScanMSD differenceMassSpectrumPositive = unknownMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD differenceMassSpectrumNegative = libraryMassSpectrum.makeDeepCopy().normalize(1000.0f);
				//
				IExtractedIonSignal extractedIonSignalUnknown = differenceMassSpectrumPositive.getExtractedIonSignal();
				IExtractedIonSignal extractedIonSignalLibrary = differenceMassSpectrumNegative.getExtractedIonSignal();
				int startIon = (extractedIonSignalUnknown.getStartIon() < extractedIonSignalLibrary.getStartIon()) ? extractedIonSignalUnknown.getStartIon() : extractedIonSignalLibrary.getStartIon();
				int stopIon = (extractedIonSignalUnknown.getStopIon() > extractedIonSignalLibrary.getStopIon()) ? extractedIonSignalUnknown.getStopIon() : extractedIonSignalLibrary.getStopIon();
				//
				differenceMassSpectrumPositive.getIons().clear();
				differenceMassSpectrumNegative.getIons().clear();
				//
				for(int ion = startIon; ion <= stopIon; ion++) {
					float abundance = extractedIonSignalUnknown.getAbundance(ion) - extractedIonSignalLibrary.getAbundance(ion);
					if(abundance < 0) {
						abundance *= -1;
						differenceMassSpectrumNegative.addIon(getIon(ion, abundance));
					} else {
						differenceMassSpectrumPositive.addIon(getIon(ion, abundance));
					}
				}
				//
				setMassSpectrumLabel(differenceMassSpectrumPositive, differenceMassSpectrumNegative);
				mirroredMassSpectrumUI.update(differenceMassSpectrumPositive, differenceMassSpectrumNegative, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		}
	}

	private IIon getIon(int mz, float abundance) {

		IIon ion = null;
		try {
			ion = new Ion(mz, abundance);
		} catch(AbundanceLimitExceededException e) {
			logger.warn(e);
		} catch(IonLimitExceededException e) {
			logger.warn(e);
		}
		return ion;
	}

	private void setMassSpectrumLabel(IScanMSD differenceMassSpectrumPositive, IScanMSD differenceMassSpectrumNegative) {

		setMassSpectrumLabel(differenceMassSpectrumPositive, "(+) [U-L] UNKNOWN MS = ", infoLabelPositive);
		setMassSpectrumLabel(differenceMassSpectrumNegative, "(-) [U-L] LIBRARY MS = ", infoLabelNegative);
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
		builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
		label.setText(builder.toString());
	}
}
