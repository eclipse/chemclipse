/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class MassSpectrumPeakLabelProvider extends AbstractChemClipseLabelProvider {

	public MassSpectrumPeakLabelProvider() {

		super("0.0####");
	}

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

		DecimalFormat ionDecimalFormat = getDecimalFormat();
		DecimalFormat abundanceFormat = getIntegerDecimalFormatInstance();
		DecimalFormat signalToNoiseDecimalFormat = ValueFormat.getDecimalFormatEnglish("0.0");
		String text = "";
		if(element instanceof IMassSpectrumPeak peak) {
			switch(columnIndex) {
				case 0: // m/z
					text = ionDecimalFormat.format(peak.getIon());
					break;
				case 1: // intensity
					text = abundanceFormat.format(peak.getAbundance());
					break;
				case 2: // s/n
					text = signalToNoiseDecimalFormat.format(peak.getSignalToNoise());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ION, IApplicationImageProvider.SIZE_16x16);
	}
}
