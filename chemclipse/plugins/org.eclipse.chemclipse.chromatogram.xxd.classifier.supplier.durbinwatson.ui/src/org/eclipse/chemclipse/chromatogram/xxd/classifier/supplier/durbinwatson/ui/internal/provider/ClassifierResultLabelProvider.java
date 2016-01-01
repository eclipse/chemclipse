/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.ISavitzkyGolayFilterRating;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;

public class ClassifierResultLabelProvider extends AbstractChemClipseLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof ISavitzkyGolayFilterRating) {
			ISavitzkyGolayFilterRating result = (ISavitzkyGolayFilterRating)element;
			switch(columnIndex) {
				case 0: // Rating
					text = decimalFormat.format(result.getRating());
					break;
				case 1: // Derivative
					text = Integer.toString(result.getSupplierFilterSettings().getDerivative());
					break;
				case 2: // Order
					text = Integer.toString(result.getSupplierFilterSettings().getOrder());
					break;
				case 3: // Width
					text = Integer.toString(result.getSupplierFilterSettings().getWidth());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CLASSIFIER_DW, IApplicationImage.SIZE_16x16);
	}
}
