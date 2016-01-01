/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

public class PeakListLabelProvider extends AbstractChemClipseLabelProvider {

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

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IPeakMSD) {
			IPeakMSD peak = (IPeakMSD)element;
			IPeakModelMSD peakModel = peak.getPeakModel();
			switch(columnIndex) {
				case 0: // RT
					text = decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 1: // RI
					text = decimalFormat.format(peakModel.getPeakMassSpectrum().getRetentionIndex());
					break;
				case 2: // Area
					text = decimalFormat.format(peak.getIntegratedArea());
					break;
				case 3: // Start RT
					text = decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 4: // Stop RT
					text = decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 5: // Width
					text = decimalFormat.format(peakModel.getWidthByInflectionPoints() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 6:
				case 7:
					if(element instanceof IChromatogramPeakMSD) {
						IChromatogramPeakMSD chromatogramPeak = (IChromatogramPeakMSD)element;
						switch(columnIndex) {
							case 6: // Scan# at Peak Maximum
								text = Integer.toString(chromatogramPeak.getScanMax());
								break;
							case 7: // S/N
								text = decimalFormat.format(chromatogramPeak.getSignalToNoiseRatio());
								break;
						}
					}
					break;
				case 8: // Leading
					text = decimalFormat.format(peakModel.getLeading());
					break;
				case 9: // Tailing
					text = decimalFormat.format(peakModel.getTailing());
					break;
				case 10: // Model Description
					text = peak.getModelDescription();
					break;
				case 11: // Suggested Components
					text = Integer.toString(peak.getSuggestedNumberOfComponents());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
	}
}
