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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class QuantCompoundLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = ExtensionMessages.name;
	public static final String CHEMICAL_CLASS = ExtensionMessages.chemicalClass;
	public static final String CONCENTRATION_UNIT = ExtensionMessages.concentrationUnit;
	public static final String CALIBRATION_METHOD = ExtensionMessages.calibrationMethod;
	public static final String CROSS_ZERO = ExtensionMessages.crossZero;
	public static final String USE_TIC = ExtensionMessages.useTic;
	public static final String RETENTION_TIME = ExtensionMessages.retentionTime;
	public static final String RETENTION_TIME_LOWER = ExtensionMessages.retentionTimeLower;
	public static final String RETENTION_TIME_UPPER = ExtensionMessages.retentionTimeUpper;
	public static final String RETENTION_INDEX = ExtensionMessages.retentionIndex;
	public static final String RETENTION_INDEX_LOWER = ExtensionMessages.retentionIndexLower;
	public static final String RETENTION_INDEX_UPPER = ExtensionMessages.retentionIndexUpper;
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
			if(element instanceof IQuantitationCompound quantitationCompound) {
				String fileName = (quantitationCompound.isCrossZero()) ? IApplicationImage.IMAGE_SELECTED : IApplicationImage.IMAGE_DESELECTED;
				return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImageProvider.SIZE_16x16);
			}
		} else if(columnIndex == 5) {
			if(element instanceof IQuantitationCompound quantitationCompound) {
				String fileName = (quantitationCompound.isUseTIC()) ? IApplicationImage.IMAGE_SELECTED : IApplicationImage.IMAGE_DESELECTED;
				return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImageProvider.SIZE_16x16);
			}
		}
		//
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IQuantitationCompound compound) {
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
					text = decimalFormat.format(retentionTimeWindow.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 7:
					text = decimalFormat.format(retentionTimeWindow.getAllowedNegativeDeviation() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 8:
					text = decimalFormat.format(retentionTimeWindow.getAllowedPositiveDeviation() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
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

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_QUANTIFY_SELECTED_PEAK, IApplicationImageProvider.SIZE_16x16);
	}
}
