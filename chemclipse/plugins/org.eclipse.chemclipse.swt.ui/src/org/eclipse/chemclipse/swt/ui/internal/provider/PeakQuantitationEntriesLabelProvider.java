/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.text.DecimalFormat;

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
				case 5: // Calibration Method
					text = quantitationEntry.getCalibrationMethod();
					break;
				case 6: // Used Cross Zero
					text = Boolean.toString(quantitationEntry.getUsedCrossZero());
					break;
				case 7: // Description
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
