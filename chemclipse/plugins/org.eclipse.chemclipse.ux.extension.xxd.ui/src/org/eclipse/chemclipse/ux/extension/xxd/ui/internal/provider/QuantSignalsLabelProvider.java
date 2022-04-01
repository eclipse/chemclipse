/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.SignalSupport;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class QuantSignalsLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SIGNAL = "Signal";
	public static final String RELATIVE_RESPONSE = "Relative Response";
	public static final String UNCERTAINTY = "Uncertainty";
	public static final String USE = "Use";
	//
	public static final String[] TITLES = { //
			SIGNAL, //
			RELATIVE_RESPONSE, //
			UNCERTAINTY, //
			USE //
	};
	//
	public static final int[] BOUNDS = { //
			100, //
			100, //
			100, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else if(columnIndex == 3) {
			if(element instanceof IQuantitationSignal) {
				IQuantitationSignal signal = (IQuantitationSignal)element;
				String fileName = (signal.isUse()) ? IApplicationImage.IMAGE_SELECTED : IApplicationImage.IMAGE_DESELECTED;
				return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImage.SIZE_16x16);
			}
		}
		//
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IQuantitationSignal) {
			IQuantitationSignal quantitationSignal = (IQuantitationSignal)element;
			//
			switch(columnIndex) {
				case 0:
					text = SignalSupport.asText(quantitationSignal.getSignal(), decimalFormat);
					break;
				case 1:
					text = decimalFormat.format(quantitationSignal.getRelativeResponse());
					break;
				case 2:
					text = decimalFormat.format(quantitationSignal.getUncertainty());
					break;
				case 3:
					text = ""; // Icon
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