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

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.ColorCode;
import org.eclipse.swt.graphics.Image;

public class ColorCodeLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = ExtensionMessages.name;
	public static final String COLOR = ExtensionMessages.color;
	//
	public static final int INDEX_COLOR = 1;
	//
	public static final String[] TITLES = { //
			NAME, //
			COLOR//
	};
	public static final int[] BOUNDS = { //
			150, //
			100 //
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

		String text = "";
		if(element instanceof ColorCode colorCode) {
			switch(columnIndex) {
				case 0:
					text = colorCode.getName();
					break;
				case 1:
					text = colorCode.getColor().toString();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PLATE_PCR, IApplicationImage.SIZE_16x16);
	}
}
