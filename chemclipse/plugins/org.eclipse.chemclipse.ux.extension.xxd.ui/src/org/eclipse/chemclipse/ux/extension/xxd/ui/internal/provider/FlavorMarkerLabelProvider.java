/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class FlavorMarkerLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String VERIFIED_MANUALLY = "Verified (manually)";
	public static final String ODOR = "Odor";
	public static final String MATRIX = "Matrix";
	public static final String SOLVENT = "Solvent";
	//
	public static final String[] TITLES = { //
			VERIFIED_MANUALLY, //
			ODOR, //
			MATRIX, //
			SOLVENT //
	};
	//
	public static final int[] BOUNDS = { //
			30, //
			250, //
			250, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			/*
			 * CheckBox
			 */
			if(element instanceof IFlavorMarker flavorMarker) {
				if(flavorMarker.isManuallyVerified()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IFlavorMarker flavorMarker) {
			switch(columnIndex) {
				case 0:
					text = ""; //
					break;
				case 1:
					text = flavorMarker.getOdor();
					break;
				case 2:
					text = flavorMarker.getMatrix();
					break;
				case 3:
					text = flavorMarker.getSolvent();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16);
	}
}