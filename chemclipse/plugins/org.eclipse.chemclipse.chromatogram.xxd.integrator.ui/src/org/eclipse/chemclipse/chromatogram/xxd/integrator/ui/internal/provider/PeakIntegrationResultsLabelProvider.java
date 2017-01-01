/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

public class PeakIntegrationResultsLabelProvider extends AbstractChemClipseLabelProvider {

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
		 * SYNCHRONIZE: IntegrationResultLabelProvider
		 * IntegrationResultTabelComparator IntegrationResultListUI
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IPeakIntegrationResult) {
			IPeakIntegrationResult peakIntegrationResult = (IPeakIntegrationResult)element;
			switch(columnIndex) {
				case 0: // Start RT
					text = decimalFormat.format(peakIntegrationResult.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 1: // Stop RT
					text = decimalFormat.format(peakIntegrationResult.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 2: // Integrated Area
					text = decimalFormat.format(peakIntegrationResult.getIntegratedArea());
					break;
				case 3: // ion
					text = getIntegratedIons(peakIntegrationResult);
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

	private String getIntegratedIons(IPeakIntegrationResult peakIntegrationResult) {

		Set<Integer> ions = peakIntegrationResult.getIntegratedIons();
		StringBuilder builder = new StringBuilder();
		ions.size();
		for(Integer ion : ions) {
			if(ion == AbstractIon.TIC_ION) {
				builder.append(AbstractIon.TIC_DESCRIPTION);
			} else {
				builder.append(ion);
			}
			builder.append(" ");
		}
		return builder.toString();
	}
}
