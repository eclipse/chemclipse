/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import java.util.Map;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class HeaderDataLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String NAME = "Name";
	public static final String VALUE = "Value";
	//
	public static final int INDEX_COLUMN_NAME = 0;
	//
	public static final String[] TITLES = { //
			NAME, //
			VALUE//
	};
	public static final int[] BOUNDS = { //
			200, //
			300 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof Map.Entry) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>)element;
			switch(columnIndex) {
				case 0:
					text = entry.getKey();
					break;
				case 1:
					text = entry.getValue();
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16);
	}
}
