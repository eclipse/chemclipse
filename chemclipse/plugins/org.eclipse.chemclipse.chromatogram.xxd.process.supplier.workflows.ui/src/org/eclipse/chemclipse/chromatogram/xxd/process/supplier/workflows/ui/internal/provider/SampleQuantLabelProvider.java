/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.swt.SampleQuantTableViewerUI;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SampleQuantLabelProvider extends LabelProvider implements ITableLabelProvider {

	private DecimalFormat decimalFormat;

	public SampleQuantLabelProvider() {

		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImageProvider.SIZE_16x16);
		} else if(columnIndex == SampleQuantTableViewerUI.INDEX_TYPE) {
			if(element instanceof ISampleQuantSubstance sampleQuantSubstance) {
				if(sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_ISTD)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAMPLE_ISTD, IApplicationImageProvider.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAMPLE, IApplicationImageProvider.SIZE_16x16);
				}
			}
		} else if(columnIndex == SampleQuantTableViewerUI.INDEX_OK) {
			if(element instanceof ISampleQuantSubstance sampleQuantSubstance) {
				if(!sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_ISTD)) {
					if(sampleQuantSubstance.isValidated()) {
						return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImageProvider.SIZE_16x16);
					} else {
						return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImageProvider.SIZE_16x16);
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ISampleQuantSubstance sampleQuantSubstance) {
			switch(columnIndex) {
				case 0:
					text = Integer.toString(sampleQuantSubstance.getId());
					break;
				case 1:
					text = sampleQuantSubstance.getCasNumber();
					break;
				case 2:
					text = sampleQuantSubstance.getName();
					break;
				case 3:
					text = Integer.toString(sampleQuantSubstance.getMaxScan());
					break;
				case 4:
					text = decimalFormat.format(sampleQuantSubstance.getConcentration());
					break;
				case 5:
					text = sampleQuantSubstance.getUnit();
					break;
				case 6:
					text = sampleQuantSubstance.getMisc();
					break;
				case 7:
					text = ""; // ISTD/SAMPLE
					break;
				case 8:
					if(!sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_ISTD)) {
						text = decimalFormat.format(sampleQuantSubstance.getMinMatchQuality());
					}
					break;
				case 9:
					if(!sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_ISTD)) {
						text = decimalFormat.format(sampleQuantSubstance.getMatchQuality());
					}
					break;
				case 10:
					text = ""; // Checked/Unchecked
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}
}
