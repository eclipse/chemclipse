/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.ui.internal.provider;

import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannel;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class VirtualChannelLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String[] TITLES = { //
			"Subset", //
			"Sample", //
			"Source Channels", //
			"Target Channel", //
	};
	//
	public static final int[] BOUNDS = { //
			150, //
			150, //
			150, //
			150, //
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

		String text = "";
		if(element instanceof VirtualChannel) {
			VirtualChannel virtualChannel = (VirtualChannel)element;
			switch(columnIndex) {
				case 0:
					text = virtualChannel.getSubset();
					break;
				case 1:
					text = virtualChannel.getSample();
					break;
				case 2:
					text = virtualChannel.getSourceChannelString();
					break;
				case 3:
					text = String.valueOf(virtualChannel.getTargetChannel());
					break;
				default:
					text = "n.a.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY, IApplicationImageProvider.SIZE_16x16);
	}
}