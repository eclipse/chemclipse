/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.instruments.Instrument;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class InstrumentLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String IDENTIFIER = ExtensionMessages.identifier;
	public static final String NAME = ExtensionMessages.name;
	public static final String DESCRIPTION = ExtensionMessages.description;
	//
	public static final String[] TITLES = { //
			IDENTIFIER, //
			NAME, //
			DESCRIPTION //
	};
	public static final int[] BOUNDS = { //
			100, //
			200, //
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
		if(element instanceof Instrument) {
			Instrument instrument = (Instrument)element;
			switch(columnIndex) {
				case 0:
					text = instrument.getIdentifier();
					break;
				case 1:
					text = instrument.getName();
					break;
				case 2:
					text = instrument.getDescription();
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INSTRUMENT, IApplicationImage.SIZE_16x16);
	}
}
