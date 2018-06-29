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

import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SequenceListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SAMPLE_NAME = "Sample Name";
	public static final String DATA_FILE = "Data File";
	public static final String VIAL = "Vial";
	public static final String SUBSTANCE = "Substance";
	public static final String DESCRIPTION = "Description";
	public static final String METHOD = "Method";
	public static final String MULTIPLIER = "Multiplier";
	//
	public static String[] TITLES = {//
			SAMPLE_NAME, //
			DATA_FILE, //
			VIAL, //
			SUBSTANCE, //
			DESCRIPTION, //
			METHOD, //
			MULTIPLIER //
	};
	//
	public static int[] BOUNDS = {//
			200, //
			150, //
			60, //
			150, //
			150, //
			150, //
			60 //
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

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof ISequenceRecord) {
			ISequenceRecord sequenceRecord = (ISequenceRecord)element;
			//
			switch(columnIndex) {
				case 0:
					text = sequenceRecord.getSampleName();
					break;
				case 1:
					text = sequenceRecord.getDataFile();
					break;
				case 2:
					text = Integer.toString(sequenceRecord.getVial());
					break;
				case 3:
					text = sequenceRecord.getSubstance();
					break;
				case 4:
					text = sequenceRecord.getDescription();
					break;
				case 5:
					text = sequenceRecord.getMethod();
					break;
				case 6:
					text = decimalFormat.format(sequenceRecord.getMultiplier());
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
		return image;
	}
}
