/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakTableTargetComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IPeak && e2 instanceof IPeak) {
			/*
			 * Peak 1
			 */
			IPeak peak1 = (IPeak)e1;
			IPeakModel peakModel1 = peak1.getPeakModel();
			IScan peakMaximum1 = peakModel1.getPeakMaximum();
			//
			List<IIdentificationTarget> peakTargets1 = new ArrayList<>();
			if(peak1 instanceof ITargetSupplier) {
				ITargetSupplier targetSupplier = peak1;
				peakTargets1.addAll(targetSupplier.getTargets());
			}
			//
			String peakTarget1 = "";
			if(peakTargets1 != null && !peakTargets1.isEmpty()) {
				float retentionIndex = peakMaximum1.getRetentionIndex();
				IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
				Collections.sort(peakTargets1, identificationTargetComparator);
				peakTarget1 = peakTargets1.get(0).getLibraryInformation().getName();
			}
			/*
			 * Peak 2
			 */
			IPeak peak2 = (IPeak)e2;
			IPeakModel peakModel2 = peak2.getPeakModel();
			IScan peakMaximum2 = peakModel2.getPeakMaximum();
			//
			List<IIdentificationTarget> peakTargets2 = new ArrayList<>();
			if(peak2 instanceof ITargetSupplier) {
				ITargetSupplier targetSupplier = peak2;
				peakTargets2.addAll(targetSupplier.getTargets());
			}
			//
			String peakTarget2 = "";
			if(peakTargets2 != null && !peakTargets2.isEmpty()) {
				float retentionIndex = peakMaximum2.getRetentionIndex();
				IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(SortOrder.DESC, retentionIndex);
				Collections.sort(peakTargets2, identificationTargetComparator);
				peakTarget2 = peakTargets2.get(0).getLibraryInformation().getName();
			}
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(peakMaximum2.getRetentionTime(), peakMaximum1.getRetentionTime());
					break;
				case 1:
					sortOrder = peakTarget2.compareTo(peakTarget1);
					break;
				case 2:
					if(peak1 instanceof IChromatogramPeakMSD chromatogramPeakMSD1 && peak2 instanceof IChromatogramPeakMSD chromatogramPeakMSD2) {
						sortOrder = Float.compare(chromatogramPeakMSD2.getSignalToNoiseRatio(), chromatogramPeakMSD1.getSignalToNoiseRatio());
					} else if(peak1 instanceof IChromatogramPeakCSD chromatogramPeakCSD1 && peak2 instanceof IChromatogramPeakCSD chromatogramPeakCSD2) {
						sortOrder = Float.compare(chromatogramPeakCSD2.getSignalToNoiseRatio(), chromatogramPeakCSD1.getSignalToNoiseRatio());
					}
					break;
				case 3:
					sortOrder = Double.compare(peak2.getIntegratedArea(), peak1.getIntegratedArea());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
