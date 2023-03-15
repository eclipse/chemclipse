/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class WncIonLabelProvider extends AbstractChemClipseLabelProvider {

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
		if(element instanceof IWncIon wncIon) {
			switch(columnIndex) {
				case 0: // Name
					text = wncIon.getName();
					break;
				case 1: // ion
					int ion = wncIon.getIon();
					if(ion == IIon.TIC_ION) {
						text = IIon.TIC_DESCRIPTION;
					} else {
						text = Integer.toString(ion);
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ION, IApplicationImageProvider.SIZE_16x16);
	}
}
