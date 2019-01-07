/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

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
		if(element instanceof IQuantitationEntry) {
			IQuantitationEntry quantitationEntry = (IQuantitationEntry)element;
			switch(columnIndex) {
				case 0: // Name
					text = quantitationEntry.getName();
					break;
				case 1: // Chemical Class
					text = quantitationEntry.getChemicalClass();
					break;
				case 2: // Concentration
					text = decimalFormat.format(quantitationEntry.getConcentration());
					break;
				case 3: // Concentration Unit
					text = quantitationEntry.getConcentrationUnit();
					break;
				case 4: // Area
					text = decimalFormat.format(quantitationEntry.getArea());
					break;
				case 5: // Ion
					double ion = quantitationEntry.getSignal();
					if(ion == ISignal.TOTAL_INTENSITY) {
						text = ISignal.TOTAL_INTENSITY_DESCRIPTION;
					} else {
						text = decimalFormat.format(ion);
					}
					break;
				case 6: // Calibration Method
					text = quantitationEntry.getCalibrationMethod();
					break;
				case 7: // Used Cross Zero
					text = Boolean.toString(quantitationEntry.getUsedCrossZero());
					break;
				case 8: // Description
					text = quantitationEntry.getDescription();
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
