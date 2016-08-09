/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.internal.provider;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.swt.graphics.Image;

public class MarkedWavelengthsChooserLabelProvider extends AbstractChemClipseLabelProvider {

	public MarkedWavelengthsChooserLabelProvider() {
		super("0.0####");
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

		String text = "";
		if(element instanceof IMarkedWavelength) {
			IMarkedWavelength markedWavelength = (IMarkedWavelength)element;
			switch(columnIndex) {
				case 0: // Wavelength
					text = Integer.valueOf(markedWavelength.getWavelength()).toString();
					break;
				case 1: // Magnification
					text = Integer.valueOf(markedWavelength.getMagnification()).toString();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAKS, IApplicationImage.SIZE_16x16);
	}
}
