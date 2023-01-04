/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.core.SignalSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class IntegrationAreaLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String AREA = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.AREA);
	public static final String TRACE = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.TRACE);
	//
	public static final String[] TITLES = { //
			AREA, //
			TRACE //
	};
	public static final int[] BOUNDS = { //
			300, //
			300 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
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
					text = SignalSupport.asText(integrationEntry.getSignal(), decimalFormat);
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INTEGRATION_RESULTS, IApplicationImage.SIZE_16x16);
	}
}
