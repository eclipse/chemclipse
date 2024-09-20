/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class SampleGroupAssignerLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SAMPLE_NAME = "Sample Name";
	public static final String DESCRIPTION = "Description";
	public static final String SELECT = "Select";
	public static final String GROUP_NAME = "Group Name";
	//
	public static String[] TITLES = {//
			SAMPLE_NAME, //
			DESCRIPTION, //
			SELECT, //
			GROUP_NAME //
	};
	//
	public static int[] BOUNDS = {//
			100, //
			200, //
			30, //
			100, //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0 && columnIndex == 1) {
			return getImage(element);
		} else if(columnIndex == 2) {
			if(element instanceof ISample sample) {
				if(sample.isSelected()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImageProvider.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImageProvider.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ISample sample) {
			//
			switch(columnIndex) {
				case 0:
					text = sample.getSampleName() != null ? sample.getSampleName() : "";
					break;
				case 1:
					text = sample.getDescription() != null ? sample.getDescription() : "";
					break;
				case 2:
					text = ""; // Checkbox
					break;
				case 3:
					text = sample.getGroupName() != null ? sample.getGroupName() : "";
					break;
			}
		}
		return text;
	}
}
