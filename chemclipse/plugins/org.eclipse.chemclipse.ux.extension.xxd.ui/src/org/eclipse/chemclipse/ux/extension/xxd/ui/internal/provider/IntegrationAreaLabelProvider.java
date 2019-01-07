/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class IntegrationAreaLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String[] TITLES = { //
			"Area", //
			"Trace"};
	public static final int[] BOUNDS = { //
			300, //
			300 //
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
		if(element instanceof IIntegrationEntry) {
			IIntegrationEntry integrationEntry = (IIntegrationEntry)element;
			switch(columnIndex) {
				case 0:
					text = decimalFormat.format(integrationEntry.getIntegratedArea());
					break;
				case 1: // TIC ...
					IIntegrationEntry integrationEntryMSD = (IIntegrationEntry)element;
					double signal = integrationEntryMSD.getSignal();
					if(signal != ISignal.TOTAL_INTENSITY) {
						text = decimalFormat.format(signal);
					} else {
						text = ISignal.TOTAL_INTENSITY_DESCRIPTION;
					}
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
