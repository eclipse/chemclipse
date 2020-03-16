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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Feature;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class FeatureLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String VARIABLE = "Variable"; // e.g. Retention Time
	public static final String USE = "Use";
	public static final String CLASSIFICATION = "Classification";
	//
	public static final int BOUND_SAMPLE = 100;
	//
	public static String[] TITLES = {//
			VARIABLE, //
			USE, //
			CLASSIFICATION //
	};
	//
	public static int[] BOUNDS = {//
			100, //
			30, //
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
		if(element instanceof Feature) {
			Feature feature = (Feature)element;
			IVariable variable = feature.getVariable();
			//
			switch(columnIndex) {
				case 0:
					text = variable.getValue();
					break;
				case 1:
					text = Boolean.toString(variable.isSelected());
					break;
				case 2:
					text = variable.getClassification();
					break;
				default:
					int index = columnIndex - 3;
					List<ISampleData<?>> sampleData = feature.getSampleData();
					if(sampleData.size() > index) {
						text = Double.toString(sampleData.get(index).getData());
					} else {
						text = "--";
					}
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
