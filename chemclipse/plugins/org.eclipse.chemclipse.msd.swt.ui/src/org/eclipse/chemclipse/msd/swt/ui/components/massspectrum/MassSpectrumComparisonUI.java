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
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MassSpectrumComparisonUI extends Composite {

	private static final Logger logger = Logger.getLogger(MassSpectrumComparisonUI.class);
	//
	private Label infoLabelReference;
	private SimpleMirroredMassSpectrumUI mirroredMassSpectrumUI;
	private Label infoLabelComparison;
	//
	private DecimalFormat decimalFormat;
	private MassValueDisplayPrecision massValueDisplayPrecision;
	//
	private String labelReference = "";
	private String labelComparison = "";

	public MassSpectrumComparisonUI(Composite parent, int style, MassValueDisplayPrecision massValueDisplayPrecision, String labelReference, String labelComparison) {
		super(parent, style);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0####");
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
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		//
		infoLabelReference = new Label(composite, SWT.NONE);
		infoLabelReference.setText("");
		infoLabelReference.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		mirroredMassSpectrumUI = new SimpleMirroredMassSpectrumUI(composite, SWT.FILL | SWT.BORDER, massValueDisplayPrecision);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		mirroredMassSpectrumUI.setLayoutData(gridData);
		//
		infoLabelComparison = new Label(composite, SWT.NONE);
		infoLabelComparison.setText("");
		infoLabelComparison.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void update(IScanMSD referenceMassSpectrum, IScanMSD comparisonMassSpectrum, boolean forceReload) {

		if(referenceMassSpectrum != null && comparisonMassSpectrum != null) {
			try {
				IScanMSD referenceMassSpectrumCopy = referenceMassSpectrum.makeDeepCopy().normalize(1000.0f);
				IScanMSD comparisonMassSpectrumCopy = comparisonMassSpectrum.makeDeepCopy().normalize(1000.0f);
				//
				setMassSpectrumLabel(referenceMassSpectrumCopy, comparisonMassSpectrumCopy);
				mirroredMassSpectrumUI.update(referenceMassSpectrumCopy, comparisonMassSpectrumCopy, forceReload);
			} catch(CloneNotSupportedException e) {
				logger.warn(e);
			}
		} else {
			mirroredMassSpectrumUI.update(null, null, true);
		}
	}

	private void setMassSpectrumLabel(IScanMSD referenceMassSpectrum, IScanMSD comparisonMassSpectrum) {

		setMassSpectrumLabel(referenceMassSpectrum, labelReference + " MS = ", infoLabelReference);
		setMassSpectrumLabel(comparisonMassSpectrum, labelComparison + " MS = ", infoLabelComparison);
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
