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
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.graphics.Image;

public class MassSpectrumListLabelProvider extends AbstractChemClipseLabelProvider {

	private TargetExtendedComparator targetExtendedComparator;

	public MassSpectrumListLabelProvider() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

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
			case 2: // RI
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
			case 3: // Base Peak
				int basePeakNoPrecision = (int)massSpectrum.getBasePeak();
				if(basePeakNoPrecision == massSpectrum.getBasePeak()) {
					text = Integer.toString(basePeakNoPrecision);
				} else {
					text = decimalFormat.format(massSpectrum.getBasePeak());
				}
				break;
			case 4: // Base Peak Abundance
				int basePeakAbundanceNoPrecision = (int)massSpectrum.getBasePeakAbundance();
				if(basePeakAbundanceNoPrecision == massSpectrum.getBasePeakAbundance()) {
					text = Integer.toString(basePeakAbundanceNoPrecision);
				} else {
					text = decimalFormat.format(massSpectrum.getBasePeakAbundance());
				}
				break;
			case 5: // Number of Ions
				text = Integer.toString(massSpectrum.getNumberOfIons());
				break;
			case 6: // CAS
				if(libraryInformation != null) {
					text = libraryInformation.getCasNumber();
				}
				break;
			case 7: // MW
				if(libraryInformation != null) {
					int molWeightNoPrecision = (int)libraryInformation.getMolWeight();
					if(molWeightNoPrecision == libraryInformation.getMolWeight()) {
						text = Integer.toString(molWeightNoPrecision);
					} else {
						text = decimalFormat.format(libraryInformation.getMolWeight());
					}
				}
				break;
			case 8: // Formula
				if(libraryInformation != null) {
					text = libraryInformation.getFormula();
				}
				break;
			case 9:
				if(libraryInformation != null) {
					text = libraryInformation.getSmiles();
				}
				break;
			case 10:
				if(libraryInformation != null) {
					text = libraryInformation.getInChI();
				}
				break;
			case 11: // Reference Identifier
				if(libraryInformation != null) {
					text = libraryInformation.getReferenceIdentifier();
				}
				break;
			case 12:
				if(libraryInformation != null) {
					text = libraryInformation.getComments();
				}
				break;
			default:
				text = "n.v.";
		}
		return text;
	}

	private ILibraryInformation getLibraryInformation(List<IMassSpectrumTarget> targets) {

		ILibraryInformation libraryInformation = null;
		Collections.sort(targets, targetExtendedComparator);
		if(targets.size() >= 1) {
			libraryInformation = targets.get(0).getLibraryInformation();
		}
		return libraryInformation;
	}
}
