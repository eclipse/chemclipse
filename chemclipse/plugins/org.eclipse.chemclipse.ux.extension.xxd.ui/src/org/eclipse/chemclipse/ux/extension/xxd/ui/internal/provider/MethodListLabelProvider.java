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

import java.util.Arrays;

import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class MethodListLabelProvider extends AbstractChemClipseLabelProvider {

	public static String[] TITLES = {//
			"Name", //
			"Description", //
			"Type", //
			"Settings", //
			"ID" //
	};
	//
	public static int[] BOUNDS = {//
			250, //
			250, //
			160, //
			300, //
			110 //
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
		if(element instanceof IProcessMethod) {
			IProcessMethod entry = (IProcessMethod)element;
			switch(columnIndex) {
				case 0:
					text = entry.getName();
					break;
				case 1:
					text = entry.getDescription();
					break;
				case 2:
					text = Arrays.toString(entry.getSupportedDataTypes().toArray());
					break;
				case 3:
					text = entry.getJsonSettings();
					break;
				case 4:
					text = entry.getId();
					break;
				default:
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PROCESS_CONTROL, IApplicationImage.SIZE_16x16);
	}
}
