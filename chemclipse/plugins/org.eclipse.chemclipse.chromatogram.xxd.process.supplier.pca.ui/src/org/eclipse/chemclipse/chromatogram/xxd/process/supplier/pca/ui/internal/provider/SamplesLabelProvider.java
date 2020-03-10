/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SamplesLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String ACTIVE = "Active";
	public static final String SAMPLE_NAME = "Sample Name";
	public static final String GROUP_NAME = "Group Name";
	//
	public static String[] TITLES = {//
			ACTIVE, //
			SAMPLE_NAME, //
			GROUP_NAME //
	};
	//
	public static int[] BOUNDS = {//
			30, //
			300, //
			300 //
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
		if(element instanceof Sample) {
			Sample sample = (Sample)element;
			//
			switch(columnIndex) {
				case 0:
					text = Boolean.toString(sample.isSelected());
					break;
				case 1:
					text = sample.getName();
					break;
				case 2:
					text = sample.getGroupName();
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAMPLE, IApplicationImage.SIZE_16x16);
	}
}
