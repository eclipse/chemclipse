/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class QuantCompoundLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.NAME);
	public static final String CHEMICAL_CLASS = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CHEMICAL_CLASS);
	public static final String CONCENTRATION_UNIT = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CONCENTRATION_UNIT);
	public static final String CALIBRATION_METHOD = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CALIBRATION_METHOD);
	public static final String CROSS_ZERO = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.CROSS_ZERO);
	public static final String USE_TIC = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.USE_TIC);
	public static final String RETENTION_TIME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_TIME);
	public static final String RETENTION_TIME_LOWER = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_TIME_LOWER);
	public static final String RETENTION_TIME_UPPER = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_TIME_UPPER);
	public static final String RETENTION_INDEX = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_INDEX);
	public static final String RETENTION_INDEX_LOWER = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_INDEX_LOWER);
	public static final String RETENTION_INDEX_UPPER = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_INDEX_UPPER);
	//
	public static final String[] TITLES = {//
			NAME, //
			CHEMICAL_CLASS, //
			CONCENTRATION_UNIT, //
			CALIBRATION_METHOD, //
			CROSS_ZERO, //
			USE_TIC, //
			RETENTION_TIME, //
			RETENTION_TIME_LOWER, //
			RETENTION_TIME_UPPER, //
			RETENTION_INDEX, //
			RETENTION_INDEX_LOWER, //
			RETENTION_INDEX_UPPER //
	};
	//
	public static final int BOUNDS[] = { //
			200, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100, //
			100 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		if(columnIndex == 4) {
			if(element instanceof IQuantitationCompound) {
				IQuantitationCompound quantitationCompound = (IQuantitationCompound)element;
				String fileName = (quantitationCompound.isCrossZero()) ? IApplicationImage.IMAGE_SELECTED : IApplicationImage.IMAGE_DESELECTED;
				return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImage.SIZE_16x16);
			}
		} else if(columnIndex == 5) {
			if(element instanceof IQuantitationCompound) {
				IQuantitationCompound quantitationCompound = (IQuantitationCompound)element;
				String fileName = (quantitationCompound.isUseTIC()) ? IApplicationImage.IMAGE_SELECTED : IApplicationImage.IMAGE_DESELECTED;
				return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImage.SIZE_16x16);
			}
		}
		//
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IQuantitationCompound) {
			IQuantitationCompound compound = (IQuantitationCompound)element;
			IRetentionTimeWindow retentionTimeWindow = compound.getRetentionTimeWindow();
			IRetentionIndexWindow retentionIndexWindow = compound.getRetentionIndexWindow();
			//
			switch(columnIndex) {
				case 0:
					text = compound.getName();
					break;
				case 1:
					text = compound.getChemicalClass();
					break;
				case 2:
					text = compound.getConcentrationUnit();
					break;
				case 3:
					text = compound.getCalibrationMethod().toString();
					break;
				case 4:
					text = ""; // Icon
					break;
				case 5:
					text = ""; // Icon
					break;
				case 6:
					text = decimalFormat.format(retentionTimeWindow.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 7:
					text = decimalFormat.format(retentionTimeWindow.getAllowedNegativeDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 8:
					text = decimalFormat.format(retentionTimeWindow.getAllowedPositiveDeviation() / IChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 9:
					text = decimalFormat.format(retentionIndexWindow.getRetentionIndex());
					break;
				case 10:
					text = decimalFormat.format(retentionIndexWindow.getAllowedNegativeDeviation());
					break;
				case 11:
					text = decimalFormat.format(retentionIndexWindow.getAllowedPositiveDeviation());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTIFY_SELECTED_PEAK, IApplicationImage.SIZE_16x16);
	}
}
