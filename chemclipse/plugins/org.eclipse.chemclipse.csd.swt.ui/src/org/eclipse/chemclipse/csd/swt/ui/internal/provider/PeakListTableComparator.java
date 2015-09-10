/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.swt.ui.internal.provider;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IPeakModelCSD;
import org.eclipse.chemclipse.support.ui.swt.viewers.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.viewers.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		int sortOrder = 0;
		if(e1 instanceof IPeakCSD && e2 instanceof IPeakCSD) {
			IPeakCSD peak1 = (IPeakCSD)e1;
			IPeakModelCSD peakModel1 = peak1.getPeakModel();
			IPeakCSD peak2 = (IPeakCSD)e2;
			IPeakModelCSD peakModel2 = peak2.getPeakModel();
			switch(getPropertyIndex()) {
				case 0: // RT
					sortOrder = peakModel2.getRetentionTimeAtPeakMaximum() - peakModel1.getRetentionTimeAtPeakMaximum();
					break;
				case 1: // RI
					sortOrder = Float.compare(peakModel2.getPeakMaximum().getRetentionIndex(), peakModel1.getPeakMaximum().getRetentionIndex());
					break;
				case 2: // Area
					sortOrder = Double.compare(peak2.getIntegratedArea(), peak1.getIntegratedArea());
					break;
				case 3: // Start RT
					sortOrder = peakModel2.getStartRetentionTime() - peakModel1.getStartRetentionTime();
					break;
				case 4: // Stop RT
					sortOrder = peakModel2.getStopRetentionTime() - peakModel1.getStopRetentionTime();
					break;
				case 5: // Width
					sortOrder = peakModel2.getWidthByInflectionPoints() - peakModel1.getWidthByInflectionPoints();
					break;
				case 6:
				case 7:
					if(e1 instanceof IChromatogramPeakCSD && e2 instanceof IChromatogramPeakCSD) {
						IChromatogramPeakCSD chromatogramPeak1 = (IChromatogramPeakCSD)e1;
						IChromatogramPeakCSD chromatogramPeak2 = (IChromatogramPeakCSD)e2;
						switch(getPropertyIndex()) {
							case 6: // Scan# at Peak Maximum
								sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
								break;
							case 7: // S/N
								sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
								break;
						}
					}
					break;
				case 8: // Leading
					sortOrder = Float.compare(peakModel2.getLeading(), peakModel1.getLeading());
					break;
				case 9: // Tailing
					sortOrder = Float.compare(peakModel2.getTailing(), peakModel1.getTailing());
					break;
				case 10: // Model Description
					sortOrder = peak2.getModelDescription().compareTo(peak1.getModelDescription());
					break;
				case 11: // Suggested Components
					sortOrder = peak2.getSuggestedNumberOfComponents() - peak1.getSuggestedNumberOfComponents();
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
