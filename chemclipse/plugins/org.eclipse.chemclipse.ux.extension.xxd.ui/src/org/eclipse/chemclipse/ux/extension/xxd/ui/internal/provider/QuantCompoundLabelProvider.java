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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class QuantCompoundLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String[] TITLES = {//
			"Name", //
			"Chemical Class", //
			"Concentration Unit", //
			"Calibration Method", //
			"Cross Zero", //
			"Use TIC", //
			"Retention Time (RT)", //
			"RT (-)", //
			"RT (+)", //
			"Retention Index (RI)", //
			"RI (-)", //
			"RI (+)" //
	};
	//
	public static final int BOUNDS[] = { //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
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
				case 0:
					text = compound.getName();
					break;
				case 1:
					text = compound.getChemicalClass();
					break;
				case 2:
					text = compound.getConcentrationUnit();
					break;
				case 3:
					text = compound.getCalibrationMethod().toString();
					break;
				case 4:
					text = Boolean.toString(compound.isCrossZero());
					break;
				case 5:
					text = Boolean.toString(compound.isUseTIC());
					break;
				case 6:
					text = decimalFormat.format(retentionTimeWindow.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 7:
					text = decimalFormat.format(retentionTimeWindow.getAllowedNegativeDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 8:
					text = decimalFormat.format(retentionTimeWindow.getAllowedPositiveDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 9:
					text = decimalFormat.format(retentionIndexWindow.getRetentionIndex());
					break;
				case 10:
					text = decimalFormat.format(retentionIndexWindow.getAllowedNegativeDeviation());
					break;
				case 11:
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
