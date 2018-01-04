/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.Map;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class BasePeakResultsLabelProvider extends AbstractChemClipseLabelProvider {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof Map.Entry<?, ?>) {
			Map.Entry<String, Double> ligninEntry = (Map.Entry<String, Double>)element;
			switch(columnIndex) {
				case 0:
					text = ligninEntry.getKey();
					break;
				case 1:
					text = decimalFormat.format(ligninEntry.getValue());
					break;
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ION, IApplicationImage.SIZE_16x16);
	}
}
