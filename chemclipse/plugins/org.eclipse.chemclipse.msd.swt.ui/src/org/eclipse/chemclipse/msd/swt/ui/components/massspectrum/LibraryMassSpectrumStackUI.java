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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.swtchart.Range;

public class LibraryMassSpectrumStackUI extends Composite {

	private static final Logger logger = Logger.getLogger(LibraryMassSpectrumStackUI.class);
	//
	private Label infoLabelUnknown;
	private SimpleMassSpectrumUI simpleMassSpectrumUnknown;
	private SimpleMassSpectrumUI simpleMassSpectrumLibrary;
	private Label infoLabelLibrary;
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
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		//
		infoLabelUnknown = new Label(composite, SWT.NONE);
		infoLabelUnknown.setText("");
		infoLabelUnknown.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Composite massSpectraComposite = new Composite(composite, SWT.NONE);
		massSpectraComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		massSpectraComposite.setLayout(new FillLayout(SWT.VERTICAL));
		//
		simpleMassSpectrumUnknown = new SimpleMassSpectrumUI(massSpectraComposite, SWT.FILL, massValueDisplayPrecision);
		simpleMassSpectrumUnknown.setShowAxis(SWT.TOP, false);
		simpleMassSpectrumUnknown.setShowAxis(SWT.RIGHT, false);
		simpleMassSpectrumUnknown.getPlotArea().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				super.mouseUp(e);
				Range range = simpleMassSpectrumUnknown.getRange();
				simpleMassSpectrumLibrary.setRange(range);
			}
		});
		simpleMassSpectrumUnknown.getPlotArea().addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {

				Range range = simpleMassSpectrumUnknown.getRange();
				simpleMassSpectrumLibrary.setRange(range);
			}
		});
		//
		simpleMassSpectrumLibrary = new SimpleMassSpectrumUI(massSpectraComposite, SWT.FILL, massValueDisplayPrecision);
		simpleMassSpectrumLibrary.setShowAxis(SWT.TOP, false);
		simpleMassSpectrumLibrary.setShowAxis(SWT.RIGHT, false);
		simpleMassSpectrumLibrary.getPlotArea().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				super.mouseUp(e);
				Range range = simpleMassSpectrumLibrary.getRange();
				simpleMassSpectrumUnknown.setRange(range);
			}
		});
		simpleMassSpectrumLibrary.getPlotArea().addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {

				Range range = simpleMassSpectrumLibrary.getRange();
				simpleMassSpectrumUnknown.setRange(range);
			}
		});
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
				IExtractedIonSignal unknownMS = unknownMassSpectrumCopy.getExtractedIonSignal();
				IExtractedIonSignal libraryMS = libraryMassSpectrumCopy.getExtractedIonSignal();
				int startMZ = ((unknownMS.getStartIon() < libraryMS.getStartIon()) ? unknownMS.getStartIon() : libraryMS.getStartIon()) - 1;
				int stopMZ = ((unknownMS.getStopIon() > libraryMS.getStopIon()) ? unknownMS.getStopIon() : libraryMS.getStopIon()) + 1;
				simpleMassSpectrumUnknown.setFixedAxisRangeX(startMZ, stopMZ);
				simpleMassSpectrumLibrary.setFixedAxisRangeX(startMZ, stopMZ);
				//
				setMassSpectrumLabel(unknownMassSpectrumCopy, libraryMassSpectrumCopy);
				simpleMassSpectrumUnknown.update(unknownMassSpectrumCopy, forceReload);
				simpleMassSpectrumLibrary.update(libraryMassSpectrumCopy, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			simpleMassSpectrumUnknown.update(null, true);
			simpleMassSpectrumLibrary.update(null, true);
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
		builder.append(decimalFormat.format(massSpectrum.getRetentionIndex()));
		label.setText(builder.toString());
	}
}
