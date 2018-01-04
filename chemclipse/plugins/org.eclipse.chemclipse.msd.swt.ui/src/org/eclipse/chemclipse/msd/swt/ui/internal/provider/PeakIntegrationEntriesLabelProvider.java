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

import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

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
		if(element instanceof IIntegrationEntryMSD) {
			IIntegrationEntryMSD integrationEntryMSD = (IIntegrationEntryMSD)element;
			switch(columnIndex) {
				case 0: // Ion
					double ion = integrationEntryMSD.getIon();
					if(ion == AbstractIon.TIC_ION) {
						text = AbstractIon.TIC_DESCRIPTION;
					} else {
						text = decimalFormat.format(ion);
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
