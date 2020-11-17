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

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SamplesLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SAMPLE_NAME = "Sample Name";
	public static final String USE = "Use";
	public static final String COLOR = "Color";
	public static final String GROUP_NAME = "Group Name";
	public static final String CLASSIFICATION = "Classification";
	public static final String DESCRIPTION = "Description";
	//
	public static final int INDEX_COLOR = 2;
	//
	public static String[] TITLES = {//
			SAMPLE_NAME, //
			USE, //
			COLOR, //
			GROUP_NAME, //
			CLASSIFICATION, //
			DESCRIPTION //
	};
	//
	public static int[] BOUNDS = {//
			300, //
			30, //
			30, //
			100, //
			100, //
			300 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else if(columnIndex == 1) {
			if(element instanceof ISample) {
				ISample sample = (ISample)element;
				if(sample.isSelected()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ISample) {
			ISample sample = (ISample)element;
			//
			switch(columnIndex) {
				case 0:
					text = sample.getName() != null ? sample.getName() : "";
					break;
				case 1:
					text = ""; // Checkbox
					break;
				case 2:
					text = ""; // Color
					break;
				case 3:
					text = sample.getGroupName() != null ? sample.getGroupName() : "";
					break;
				case 4:
					text = sample.getClassification() != null ? sample.getClassification() : "";
					break;
				case 5:
					text = sample.getDescription() != null ? sample.getDescription() : "";
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
