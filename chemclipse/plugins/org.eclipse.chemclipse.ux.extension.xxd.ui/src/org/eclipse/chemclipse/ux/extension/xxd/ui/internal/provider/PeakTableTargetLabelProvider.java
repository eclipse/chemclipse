/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PeakTableTargetLabelProvider extends LabelProvider implements ITableLabelProvider {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

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

		String text = "";
		if(element instanceof IPeak peak) {
			switch(columnIndex) {
				case 0:
					text = decimalFormat.format(peak.getPeakModel().getPeakMaximum().getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 1:
					ILibraryInformation libraryInformation = IIdentificationTarget.getLibraryInformation(peak);
					text = libraryInformation != null ? libraryInformation.getName() : "";
					break;
				case 2:
					if(peak instanceof IChromatogramPeakMSD chromatogramPeakMSD) {
						text = decimalFormat.format(chromatogramPeakMSD.getSignalToNoiseRatio());
					} else if(peak instanceof IChromatogramPeakCSD chromatogramPeakCSD) {
						text = decimalFormat.format(chromatogramPeakCSD.getSignalToNoiseRatio());
					}
					break;
				case 3:
					text = decimalFormat.format(peak.getIntegratedArea());
					break;
				default:
					text = "n.v.";
			}
		}
		//
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
	}
}
