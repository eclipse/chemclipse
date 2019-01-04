/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class QuantitationListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String[] TITLES = { //
			"Name", //
			"Chemical Class", //
			"Concentration", //
			"Concentration Unit", //
			"Area", //
			"Trace", //
			"Calibration Method", //
			"Cross Zero", //
			"Description" //
	};
	public static final int[] BOUNDS = { //
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

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IQuantitationEntry) {
			IQuantitationEntry quantitationEntry = (IQuantitationEntry)element;
			switch(columnIndex) {
				case 0:
					text = quantitationEntry.getName();
					break;
				case 1:
					text = quantitationEntry.getChemicalClass();
					break;
				case 2:
					text = decimalFormat.format(quantitationEntry.getConcentration());
					break;
				case 3:
					text = quantitationEntry.getConcentrationUnit();
					break;
				case 4:
					text = decimalFormat.format(quantitationEntry.getArea());
					break;
				case 5: // TIC ...
					text = IQuantitationSignal.TIC_DESCRIPTION;
					double signal = quantitationEntry.getSignal();
					if(signal != IQuantitationSignal.TIC_SIGNAL) {
						text = decimalFormat.format(signal);
					}
					break;
				case 6:
					text = quantitationEntry.getCalibrationMethod();
					break;
				case 7:
					text = Boolean.toString(quantitationEntry.getUsedCrossZero());
					break;
				case 8:
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
