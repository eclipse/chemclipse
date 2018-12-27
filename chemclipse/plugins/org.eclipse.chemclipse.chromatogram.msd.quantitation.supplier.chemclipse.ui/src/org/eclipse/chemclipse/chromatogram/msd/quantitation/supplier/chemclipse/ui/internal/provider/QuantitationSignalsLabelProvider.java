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

import org.eclipse.swt.graphics.Image;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

public class QuantitationSignalsLabelProvider extends AbstractChemClipseLabelProvider {

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
		if(element instanceof IQuantitationSignal) {
			IQuantitationSignal entry = (IQuantitationSignal)element;
			//
			switch(columnIndex) {
				case 0: // Ion
					double ion = entry.getSignal();
					if(ion == AbstractIon.TIC_ION) {
						text = AbstractIon.TIC_DESCRIPTION;
					} else {
						text = decimalFormat.format(ion);
					}
					break;
				case 1: // Relative Response
					text = decimalFormat.format(entry.getRelativeResponse());
					break;
				case 2: // Uncertainty
					text = decimalFormat.format(entry.getUncertainty());
					break;
				case 3: // Use
					text = Boolean.toString(entry.isUse());
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
