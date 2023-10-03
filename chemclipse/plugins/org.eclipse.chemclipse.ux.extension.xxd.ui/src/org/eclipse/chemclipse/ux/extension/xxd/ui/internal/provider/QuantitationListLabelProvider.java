/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.SignalSupport;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class QuantitationListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String[] TITLES = { //
			ExtensionMessages.name, //
			ExtensionMessages.chemicalClass, //
			ExtensionMessages.concentration, //
			ExtensionMessages.concentrationUnit, //
			ExtensionMessages.area, //
			ExtensionMessages.trace, //
			ExtensionMessages.calibrationMethod, //
			ExtensionMessages.crossZero, //
			ExtensionMessages.flag, //
			ExtensionMessages.description, //
			ExtensionMessages.group, //
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
			100, //
			100, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IQuantitationEntry quantitationEntry) {
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
				case 5: // TIC, XIC, ...
					text = SignalSupport.asText(quantitationEntry.getSignals(), decimalFormat);
					break;
				case 6:
					text = quantitationEntry.getCalibrationMethod();
					break;
				case 7:
					text = Boolean.toString(quantitationEntry.getUsedCrossZero());
					break;
				case 8:
					text = quantitationEntry.getQuantitationFlag().label();
					break;
				case 9:
					text = quantitationEntry.getDescription();
					break;
				case 10:
					text = quantitationEntry.getGroup();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTITATION_RESULTS, IApplicationImageProvider.SIZE_16x16);
	}
}