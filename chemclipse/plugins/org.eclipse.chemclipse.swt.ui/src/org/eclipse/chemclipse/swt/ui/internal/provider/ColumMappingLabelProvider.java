/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ColumMappingLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String KEYWORD = "Keyword";
	public static final String SEPRATION_COLUMN = "Separation Column";
	//
	public static final String[] TITLES = { //
			KEYWORD, //
			SEPRATION_COLUMN//
	};
	public static final int[] BOUNDS = { //
			200, //
			200 //
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
		if(element instanceof Map.Entry setting) {
			switch(columnIndex) {
				case 0:
					text = setting.getKey().toString();
					break;
				case 1:
					Object object = setting.getValue();
					if(object instanceof SeparationColumnType separationColumnType) {
						text = separationColumnType.label();
					} else {
						text = object.toString();
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16);
	}
}