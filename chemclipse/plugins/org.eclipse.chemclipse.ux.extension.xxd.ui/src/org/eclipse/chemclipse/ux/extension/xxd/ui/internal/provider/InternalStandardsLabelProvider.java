/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.quantitation.IInternalStandard;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class InternalStandardsLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.NAME);
	public static final String CONCENTRATION = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CONCENTRATION);
	public static final String UNIT = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.UNIT);
	public static final String RESPONSE_FACTOR = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RESPONSE_FACTOR);
	public static final String CHEMICAL_CLASS = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CHEMICAL_CLASS);
	//
	public static final String[] TITLES = {//
			NAME, //
			CONCENTRATION, //
			UNIT, //
			RESPONSE_FACTOR, //
			CHEMICAL_CLASS //
	};
	//
	public static final int BOUNDS[] = {//
			170, //
			170, //
			150, //
			170, //
			150 //
	};

	public InternalStandardsLabelProvider() {

		super("0.0##");
	}

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
		if(element instanceof IInternalStandard) {
			IInternalStandard internalStandard = (IInternalStandard)element;
			switch(columnIndex) {
				case 0:
					text = internalStandard.getName();
					break;
				case 1:
					text = decimalFormat.format(internalStandard.getConcentration());
					break;
				case 2:
					text = internalStandard.getConcentrationUnit();
					break;
				case 3:
					text = decimalFormat.format(internalStandard.getResponseFactor());
					break;
				case 4:
					text = internalStandard.getChemicalClass();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
	}
}
