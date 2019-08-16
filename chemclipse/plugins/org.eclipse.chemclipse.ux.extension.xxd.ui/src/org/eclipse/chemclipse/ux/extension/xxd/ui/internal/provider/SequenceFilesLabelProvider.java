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

import java.io.File;
import java.text.DecimalFormat;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SequenceFilesLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = "Name";
	public static final String SIZE = "Size (KB)";
	//
	public static String[] TITLES = {//
			NAME, //
			SIZE //
	};
	//
	public static int[] BOUNDS = {//
			300, //
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
		if(element instanceof File) {
			File file = (File)element;
			//
			switch(columnIndex) {
				case 0:
					text = file.getName();
					break;
				case 1:
					text = decimalFormat.format(file.length() / 1000.0d);
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEQUENCE_LIST, IApplicationImage.SIZE_16x16);
	}
}
