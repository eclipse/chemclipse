/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.isd.filter.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.isd.filter.model.WavenumberSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class WavenumberSignalsLabelProvider extends LabelProvider implements ITableLabelProvider {

	public static final String WAVENUMBER = "Wavenumber";
	public static final String INTENSITY = "Intensity";
	//
	public static final String[] TITLES = { //
			WAVENUMBER, //
			INTENSITY //
	};
	public static final int[] BOUNDS = { //
			200, //
			100 //
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
		if(element instanceof WavenumberSignal setting) {
			switch(columnIndex) {
				case 0:
					text = Double.toString(setting.getWavenumber());
					break;
				case 1:
					text = Double.toString(setting.getIntensity());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SCAN_XIR, IApplicationImageProvider.SIZE_16x16);
	}
}