/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.wavelengths.NamedWavelength;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class NamedWavelengthsLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String IDENTIFIER = "Identifier";
	public static final String WAVELENGTHS = "Wavelengths";
	//
	public static final String[] TITLES = { //
			IDENTIFIER, //
			WAVELENGTHS //
	};
	public static final int[] BOUNDS = { //
			300, //
			250 //
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
		if(element instanceof NamedWavelength) {
			NamedWavelength namedWavelength = (NamedWavelength)element;
			switch(columnIndex) {
				case 0:
					text = namedWavelength.getIdentifier();
					break;
				case 1:
					text = namedWavelength.getWavelengths();
					break;
			}
		}
		return text;
	}
}
