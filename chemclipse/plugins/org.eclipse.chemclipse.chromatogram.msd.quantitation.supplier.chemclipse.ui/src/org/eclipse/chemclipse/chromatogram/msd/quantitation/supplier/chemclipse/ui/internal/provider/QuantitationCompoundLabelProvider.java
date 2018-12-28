/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class QuantitationCompoundLabelProvider extends AbstractChemClipseLabelProvider {

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
		if(element instanceof IQuantitationCompound) {
			IQuantitationCompound compound = (IQuantitationCompound)element;
			IRetentionTimeWindow retentionTimeWindow = compound.getRetentionTimeWindow();
			IRetentionIndexWindow retentionIndexWindow = compound.getRetentionIndexWindow();
			//
			switch(columnIndex) {
				case 0: // Name
					text = compound.getName();
					break;
				case 1: // Chemical Class
					text = compound.getChemicalClass();
					break;
				case 2: // Concentration Unit
					text = compound.getConcentrationUnit();
					break;
				case 3: // Calibration Method
					text = compound.getCalibrationMethod().toString();
					break;
				case 4: // Cross Zero
					text = Boolean.toString(compound.isCrossZero());
					break;
				case 5: // Use Tic
					text = Boolean.toString(compound.isUseTIC());
					break;
				case 6: // Retention Time
					text = decimalFormat.format(retentionTimeWindow.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 7: // Allowed Negative Deviation
					text = decimalFormat.format(retentionTimeWindow.getAllowedNegativeDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 8: // Allowed Positive Deviation
					text = decimalFormat.format(retentionTimeWindow.getAllowedPositiveDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 9: // Retention Index
					text = decimalFormat.format(retentionIndexWindow.getRetentionIndex());
					break;
				case 10: // Allowed Negative Deviation
					text = decimalFormat.format(retentionIndexWindow.getAllowedNegativeDeviation());
					break;
				case 11: // Allowed Positive Deviation
					text = decimalFormat.format(retentionIndexWindow.getAllowedPositiveDeviation());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTIFY_SELECTED_PEAK, IApplicationImage.SIZE_16x16);
	}
}
