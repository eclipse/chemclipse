/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class MassSpectrumListLabelProvider extends AbstractChemClipseLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		if(element instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)element;
			return getText(libraryMassSpectrum, libraryMassSpectrum.getLibraryInformation(), columnIndex);
		} else if(element instanceof IScanMSD) {
			IScanMSD massSpectrum = (IScanMSD)element;
			ILibraryInformation libraryInformation = getLibraryInformation(massSpectrum.getTargets());
			if(massSpectrum.getOptimizedMassSpectrum() != null) {
				massSpectrum = massSpectrum.getOptimizedMassSpectrum();
			}
			return getText(massSpectrum, libraryInformation, columnIndex);
		} else {
			return "n.a.";
		}
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16);
		return image;
	}

	private String getText(IScanMSD massSpectrum, ILibraryInformation libraryInformation, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		switch(columnIndex) {
			case 0: // RT
				text = decimalFormat.format(massSpectrum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				break;
			case 1: // RI
				text = decimalFormat.format(massSpectrum.getRetentionIndex());
				break;
			case 2: // Base Peak
				text = decimalFormat.format(massSpectrum.getBasePeak());
				break;
			case 3: // Base Peak Abundance
				text = decimalFormat.format(massSpectrum.getBasePeakAbundance());
				break;
			case 4: // Number of Ions
				text = Integer.toString(massSpectrum.getNumberOfIons());
				break;
			case 5: // Name
				if(libraryInformation != null) {
					text = libraryInformation.getName();
				}
				break;
			case 6: // CAS
				if(libraryInformation != null) {
					text = libraryInformation.getCasNumber();
				}
				break;
			case 7: // MW
				if(libraryInformation != null) {
					text = decimalFormat.format(libraryInformation.getMolWeight());
				}
				break;
			case 8: // Formula
				if(libraryInformation != null) {
					text = libraryInformation.getFormula();
				}
				break;
			default:
				text = "n.v.";
		}
		return text;
	}

	private ILibraryInformation getLibraryInformation(List<IMassSpectrumTarget> targets) {

		ILibraryInformation libraryInformation = null;
		float matchFactor = Float.MIN_VALUE;
		for(IMassSpectrumTarget target : targets) {
			if(target.getComparisonResult().getMatchFactor() > matchFactor) {
				matchFactor = target.getComparisonResult().getMatchFactor();
				libraryInformation = target.getLibraryInformation();
			}
		}
		return libraryInformation;
	}
}
