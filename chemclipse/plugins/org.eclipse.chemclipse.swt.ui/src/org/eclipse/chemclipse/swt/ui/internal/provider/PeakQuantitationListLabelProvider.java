/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.support.PeakQuantitation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.peaks.PeakQuantitationListUI;
import org.eclipse.swt.graphics.Image;

public class PeakQuantitationListLabelProvider extends AbstractChemClipseLabelProvider {

	public PeakQuantitationListLabelProvider() {

		super("0.0##");
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

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof PeakQuantitation peakQuantitationEntry) {
			switch(columnIndex) {
				case 0:
					text = decimalFormat.format(peakQuantitationEntry.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 1:
					text = peakQuantitationEntry.getName();
					break;
				case 2:
					text = peakQuantitationEntry.getCasNumber();
					break;
				case 3:
					text = peakQuantitationEntry.getReferenceIdentifier();
					break;
				case 4:
					text = decimalFormat.format(peakQuantitationEntry.getIntegratedArea());
					break;
				case 5:
					text = peakQuantitationEntry.getClassifier();
					break;
				case 6:
					text = peakQuantitationEntry.getQuantifier();
					break;
				default:
					int index = columnIndex - PeakQuantitationListUI.INDEX_QUANTITATIONS;
					if(index < peakQuantitationEntry.getConcentrations().size()) {
						Double concentration = peakQuantitationEntry.getConcentrations().get(index);
						if(!Double.isNaN(concentration)) {
							text = decimalFormat.format(concentration);
						}
					}
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImageProvider.SIZE_16x16);
	}
}
