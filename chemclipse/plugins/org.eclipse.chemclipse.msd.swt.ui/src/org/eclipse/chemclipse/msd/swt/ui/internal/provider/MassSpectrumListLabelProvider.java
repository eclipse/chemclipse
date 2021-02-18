/*******************************************************************************
 * Copyright (c) 2013, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
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
			/*
			 * Library Entry
			 */
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)element;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			return getText(libraryMassSpectrum, libraryInformation, columnIndex);
		} else if(element instanceof IScanMSD) {
			/*
			 * Scan
			 */
			IScanMSD massSpectrum = (IScanMSD)element;
			float retentionIndex = massSpectrum.getRetentionIndex();
			IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
			ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(massSpectrum.getTargets(), identificationTargetComparator);
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
			case 0: // Name
				if(libraryInformation != null) {
					text = libraryInformation.getName();
				}
				break;
			case 1: // RT
				if(massSpectrum.getRetentionTime() == 0) {
					text = "0";
				} else {
					text = decimalFormat.format(massSpectrum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				}
				break;
			case 2: // RRT
				if(massSpectrum.getRelativeRetentionTime() == 0) {
					text = "0";
				} else {
					text = decimalFormat.format(massSpectrum.getRelativeRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
				}
				break;
			case 3: // RI
				int retentionIndexNoPrecision = (int)massSpectrum.getRetentionIndex();
				if(retentionIndexNoPrecision == massSpectrum.getRetentionIndex()) {
					text = Integer.toString(retentionIndexNoPrecision);
				} else {
					if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
						text = Integer.toString(retentionIndexNoPrecision);
					} else {
						text = decimalFormat.format(massSpectrum.getRetentionIndex());
					}
				}
				break;
			case 4: // Base Peak
				int basePeakNoPrecision = (int)massSpectrum.getBasePeak();
				if(basePeakNoPrecision == massSpectrum.getBasePeak()) {
					text = Integer.toString(basePeakNoPrecision);
				} else {
					text = decimalFormat.format(massSpectrum.getBasePeak());
				}
				break;
			case 5: // Base Peak Abundance
				int basePeakAbundanceNoPrecision = (int)massSpectrum.getBasePeakAbundance();
				if(basePeakAbundanceNoPrecision == massSpectrum.getBasePeakAbundance()) {
					text = Integer.toString(basePeakAbundanceNoPrecision);
				} else {
					text = decimalFormat.format(massSpectrum.getBasePeakAbundance());
				}
				break;
			case 6: // Number of Ions
				text = Integer.toString(massSpectrum.getNumberOfIons());
				break;
			case 7: // CAS
				if(libraryInformation != null) {
					text = libraryInformation.getCasNumber();
				}
				break;
			case 8: // MW
				if(libraryInformation != null) {
					int molWeightNoPrecision = (int)libraryInformation.getMolWeight();
					if(molWeightNoPrecision == libraryInformation.getMolWeight()) {
						text = Integer.toString(molWeightNoPrecision);
					} else {
						text = decimalFormat.format(libraryInformation.getMolWeight());
					}
				}
				break;
			case 9: // Formula
				if(libraryInformation != null) {
					text = libraryInformation.getFormula();
				}
				break;
			case 10:
				if(libraryInformation != null) {
					text = libraryInformation.getSmiles();
				}
				break;
			case 11:
				if(libraryInformation != null) {
					text = libraryInformation.getInChI();
				}
				break;
			case 12: // Reference Identifier
				if(libraryInformation != null) {
					text = libraryInformation.getReferenceIdentifier();
				}
				break;
			case 13:
				if(libraryInformation != null) {
					text = libraryInformation.getComments();
				}
				break;
			default:
				text = "n.v.";
		}
		return text;
	}
}
