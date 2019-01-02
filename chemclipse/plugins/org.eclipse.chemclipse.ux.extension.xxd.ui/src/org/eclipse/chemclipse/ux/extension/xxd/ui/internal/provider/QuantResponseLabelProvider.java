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

import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class QuantResponseLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SIGNAL = "Signal";
	public static final String CONCENTRATION = "Concentration";
	public static final String RESPONSE = "Response";
	//
	public static final String[] TITLES = { //
			SIGNAL, //
			CONCENTRATION, //
			RESPONSE//
	};
	//
	public static final int[] BOUNDS = { //
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
		if(element instanceof IResponseSignal) {
			IResponseSignal entry = (IResponseSignal)element;
			//
			switch(columnIndex) {
				case 0:
					double signal = entry.getSignal();
					if(signal == IQuantitationSignal.TIC_SIGNAL) {
						text = "TIC";
					} else {
						text = decimalFormat.format(signal);
					}
					break;
				case 1:
					text = decimalFormat.format(entry.getConcentration());
					break;
				case 2:
					text = decimalFormat.format(entry.getResponse());
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
