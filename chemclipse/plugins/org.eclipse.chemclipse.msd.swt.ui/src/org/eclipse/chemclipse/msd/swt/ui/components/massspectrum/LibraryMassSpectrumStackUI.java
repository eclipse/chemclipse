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
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class LibraryMassSpectrumStackUI extends Composite {

	private static final Logger logger = Logger.getLogger(LibraryMassSpectrumStackUI.class);
	//
	private StackedMassSpectrumUI stackedMassSpectrumUnknown;
	private StackedMassSpectrumUI stackedMassSpectrumLibrary;
	//
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;

	public LibraryMassSpectrumStackUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision) {
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
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		//
		stackedMassSpectrumUnknown = new StackedMassSpectrumUI(composite, SWT.FILL, massValueDisplayPrecision);
		stackedMassSpectrumLibrary = new StackedMassSpectrumUI(composite, SWT.FILL, massValueDisplayPrecision);
		stackedMassSpectrumUnknown.setOtherStackedMassSpectrumUI(stackedMassSpectrumLibrary);
		stackedMassSpectrumLibrary.setOtherStackedMassSpectrumUI(stackedMassSpectrumUnknown);
	}

	public void update(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

		if(unknownMassSpectrum != null && libraryMassSpectrum != null) {
			try {
				IScanMSD unknownMassSpectrumCopy = unknownMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD libraryMassSpectrumCopy = libraryMassSpectrum.makeDeepCopy().normalize(1000.0f);
				//
				IExtractedIonSignal unknownMS = unknownMassSpectrumCopy.getExtractedIonSignal();
				IExtractedIonSignal libraryMS = libraryMassSpectrumCopy.getExtractedIonSignal();
				int startMZ = ((unknownMS.getStartIon() < libraryMS.getStartIon()) ? unknownMS.getStartIon() : libraryMS.getStartIon()) - 1;
				int stopMZ = ((unknownMS.getStopIon() > libraryMS.getStopIon()) ? unknownMS.getStopIon() : libraryMS.getStopIon()) + 1;
				stackedMassSpectrumUnknown.setFixedAxisRangeX(startMZ, stopMZ);
				stackedMassSpectrumLibrary.setFixedAxisRangeX(startMZ, stopMZ);
				//
				setMassSpectrumLabel(unknownMassSpectrumCopy, libraryMassSpectrumCopy);
				stackedMassSpectrumUnknown.update(unknownMassSpectrumCopy, forceReload);
				stackedMassSpectrumLibrary.update(libraryMassSpectrumCopy, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			stackedMassSpectrumUnknown.update(null, true);
			stackedMassSpectrumLibrary.update(null, true);
		}
	}

	private void setMassSpectrumLabel(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum) {

		setMassSpectrumLabel(unknownMassSpectrum, "UNKNOWN MS = ", stackedMassSpectrumUnknown);
		setMassSpectrumLabel(libraryMassSpectrum, "LIBRARY MS = ", stackedMassSpectrumLibrary);
	}

	private void setMassSpectrumLabel(IScanMSD massSpectrum, String title, StackedMassSpectrumUI stackedMassSpectrumUI) {

		StringBuilder builder = new StringBuilder();
		builder.append("m/z [");
		builder.append(title);
		if(massSpectrum instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			builder.append("NAME: ");
			builder.append(libraryInformation.getName());
		} else {
			builder.append("RT: ");
			builder.append(decimalFormat.format(massSpectrum.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
		}
		builder.append("]");
		//
		stackedMassSpectrumUI.setAxisTitle(SWT.BOTTOM, builder.toString());
	}
}