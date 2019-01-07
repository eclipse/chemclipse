/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class PeakIntegrationEntriesLabelProvider extends AbstractChemClipseLabelProvider {

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
		 * SYNCHRONIZE: PeakIntegrationEntriesLabelProvider PeakIntegrationEntriesLabelComparator PeakIntegrationEntriesView
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IIntegrationEntry) {
			IIntegrationEntry integrationEntryMSD = (IIntegrationEntry)element;
			switch(columnIndex) {
				case 0: // Ion
					double signal = integrationEntryMSD.getSignal();
					if(signal == ISignal.TOTAL_INTENSITY) {
						text = ISignal.TOTAL_INTENSITY_DESCRIPTION;
					} else {
						text = decimalFormat.format(signal);
					}
					break;
				case 1: // Area
					text = decimalFormat.format(integrationEntryMSD.getIntegratedArea());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INTEGRATION_RESULTS, IApplicationImage.SIZE_16x16);
	}
}
