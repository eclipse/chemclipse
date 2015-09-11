/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		int sortOrder = 0;
		if(e1 instanceof IPeakMSD && e2 instanceof IPeakMSD) {
			IPeakMSD peak1 = (IPeakMSD)e1;
			IPeakModelMSD peakModel1 = peak1.getPeakModel();
			IPeakMSD peak2 = (IPeakMSD)e2;
			IPeakModelMSD peakModel2 = peak2.getPeakModel();
			switch(getPropertyIndex()) {
				case 0: // RT
					sortOrder = peakModel2.getRetentionTimeAtPeakMaximum() - peakModel1.getRetentionTimeAtPeakMaximum();
					break;
				case 1: // RI
					sortOrder = Float.compare(peakModel2.getPeakMassSpectrum().getRetentionIndex(), peakModel1.getPeakMassSpectrum().getRetentionIndex());
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
					if(e1 instanceof IChromatogramPeakMSD && e2 instanceof IChromatogramPeakMSD) {
						IChromatogramPeakMSD chromatogramPeak1 = (IChromatogramPeakMSD)e1;
						IChromatogramPeakMSD chromatogramPeak2 = (IChromatogramPeakMSD)e2;
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
