/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

public class PeakQuantitationEntriesLabelProvider extends AbstractChemClipseLabelProvider {

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

		/*
		 * SYNCHRONIZE: PeakQuantitationEntriesLabelProvider PeakQuantitationEntriesLabelComparator PeakQuantitationEntriesView
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IQuantitationEntryMSD) {
			IQuantitationEntryMSD quantitationEntryMSD = (IQuantitationEntryMSD)element;
			switch(columnIndex) {
				case 0: // Name
					text = quantitationEntryMSD.getName();
					break;
				case 1: // Chemical Class
					text = quantitationEntryMSD.getChemicalClass();
					break;
				case 2: // Concentration
					text = decimalFormat.format(quantitationEntryMSD.getConcentration());
					break;
				case 3: // Concentration Unit
					text = quantitationEntryMSD.getConcentrationUnit();
					break;
				case 4: // Area
					text = decimalFormat.format(quantitationEntryMSD.getArea());
					break;
				case 5: // Ion
					double ion = quantitationEntryMSD.getIon();
					if(ion == AbstractIon.TIC_ION) {
						text = AbstractIon.TIC_DESCRIPTION;
					} else {
						text = decimalFormat.format(ion);
					}
					break;
				case 6: // Calibration Method
					text = quantitationEntryMSD.getCalibrationMethod();
					break;
				case 7: // Used Cross Zero
					text = Boolean.toString(quantitationEntryMSD.getUsedCrossZero());
					break;
				case 8: // Description
					text = quantitationEntryMSD.getDescription();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTITATION_RESULTS, IApplicationImage.SIZE_16x16);
	}
}
