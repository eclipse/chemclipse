/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PeakTableTargetLabelProvider extends LabelProvider implements ITableLabelProvider {

	private DecimalFormat decimalFormat;
	private TargetExtendedComparator targetExtendedComparator;

	public PeakTableTargetLabelProvider() {
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

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

		String text = "";
		if(element instanceof IPeak) {
			IPeak peak = (IPeak)element;
			IPeakModel peakModel = peak.getPeakModel();
			IScan peakMaximum = peakModel.getPeakMaximum();
			//
			List<IIdentificationTarget> peakTargets = new ArrayList<>();
			if(peak instanceof ITargetSupplier) {
				ITargetSupplier targetSupplier = (ITargetSupplier)peak;
				peakTargets.addAll(targetSupplier.getTargets());
			}
			//
			String peakTarget = "";
			if(peakTargets != null && peakTargets.size() > 0) {
				Collections.sort(peakTargets, targetExtendedComparator);
				peakTarget = peakTargets.get(0).getLibraryInformation().getName();
			}
			//
			switch(columnIndex) {
				case 0:
					text = decimalFormat.format(peakMaximum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 1:
					text = peakTarget;
					break;
				case 2:
					if(peak instanceof IChromatogramPeakMSD) {
						text = decimalFormat.format(((IChromatogramPeakMSD)peak).getSignalToNoiseRatio());
					} else if(peak instanceof IChromatogramPeakCSD) {
						text = decimalFormat.format(((IChromatogramPeakCSD)peak).getSignalToNoiseRatio());
					}
					break;
				case 3:
					text = decimalFormat.format(peak.getIntegratedArea());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImage.SIZE_16x16);
	}
}
