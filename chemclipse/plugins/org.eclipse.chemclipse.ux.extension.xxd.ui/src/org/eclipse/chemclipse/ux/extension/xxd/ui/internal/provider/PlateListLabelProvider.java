/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.pcr.model.core.IPlateTableEntry;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class PlateListLabelProvider extends AbstractChemClipseLabelProvider {

	public static String[] TITLES = {//
			"", //
			"1", //
			"2", //
			"3", //
			"4", //
			"5", //
			"6", //
			"7", //
			"8", //
			"9", //
			"10", //
			"11", //
			"12" //
	};
	//
	public static int[] BOUNDS = {//
			60, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110, //
			110 //
	};

	public static String getCellText(IWell well) {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
		String text;
		if(well != null) {
			if(well.isEmptyMeasurement()) {
				text = "Position " + (well.getPosition().getId() + 1);
			} else {
				StringBuilder builder = new StringBuilder();
				builder.append(well.getSampleId());
				if(!"".equals(well.getTargetName())) {
					builder.append("\n");
					builder.append(well.getTargetName());
				}
				builder.append("\n");
				builder.append(decimalFormat.format(well.getCrossingPoint()));
				text = builder.toString();
			}
		} else {
			text = "n.v.";
		}
		return text;
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

		String text = "";
		if(element instanceof IPlateTableEntry) {
			IPlateTableEntry entry = (IPlateTableEntry)element;
			if(columnIndex == 0) {
				text = entry.getRow();
			} else {
				IWell well = entry.getWells().get(columnIndex);
				text = getText(well);
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return null;
	}
}
