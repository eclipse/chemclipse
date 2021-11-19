/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.CalibrationFile;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class CalibrationListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String[] TITLES = { //
			"Name", //
			"Type", //
			"File Name", //
			"File Path" //
	};
	//
	public static final int[] BOUNDS = { //
			100, //
			150, //
			150, //
			250 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof CalibrationFile) {
			//
			CalibrationFile calibrationFile = (CalibrationFile)element;
			//
			switch(columnIndex) {
				case 0:
					text = calibrationFile.getSeparationColumnIndices().getSeparationColumn().getName();
					break;
				case 1:
					text = calibrationFile.getSeparationColumnIndices().getSeparationColumn().getSeparationColumnType().label();
					break;
				case 2:
					text = calibrationFile.getFile().getName();
					break;
				case 3:
					text = calibrationFile.getFile().getAbsolutePath();
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
		return image;
	}
}