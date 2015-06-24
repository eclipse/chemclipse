/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

public class ChromatogramIntegrationResultsLabelProvider extends AbstractChemClipseLabelProvider {

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
		if(element instanceof IChromatogramIntegrationResult) {
			IChromatogramIntegrationResult chromatogramIntegrationResult = (IChromatogramIntegrationResult)element;
			switch(columnIndex) {
				case 0: // Chromatogram Area
					text = decimalFormat.format(chromatogramIntegrationResult.getChromatogramArea());
					break;
				case 1: // Background Area
					text = decimalFormat.format(chromatogramIntegrationResult.getBackgroundArea());
					break;
				case 2: // ion
					double ion = chromatogramIntegrationResult.getIon();
					if(ion == AbstractIon.TIC_ION) {
						text = AbstractIon.TIC_DESCRIPTION;
					} else {
						text = decimalFormat.format(ion);
					}
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
