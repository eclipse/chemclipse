/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private TargetExtendedComparator targetExtendedComparator;

	public PeakListTableComparator() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		int sortOrder = 0;
		if(e1 instanceof IPeakMSD && e2 instanceof IPeakMSD) {
			IPeakMSD peak1 = (IPeakMSD)e1;
			IPeakModelMSD peakModel1 = peak1.getPeakModel();
			ILibraryInformation libraryInformation1 = getLibraryInformation(peak1.getTargets());
			IPeakMSD peak2 = (IPeakMSD)e2;
			IPeakModelMSD peakModel2 = peak2.getPeakModel();
			ILibraryInformation libraryInformation2 = getLibraryInformation(peak2.getTargets());
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Boolean.compare(peak2.isActiveForAnalysis(), peak1.isActiveForAnalysis());
					break;
				case 1: // RT
					sortOrder = peakModel2.getRetentionTimeAtPeakMaximum() - peakModel1.getRetentionTimeAtPeakMaximum();
					break;
				case 2: // RI
					sortOrder = Float.compare(peakModel2.getPeakMassSpectrum().getRetentionIndex(), peakModel1.getPeakMassSpectrum().getRetentionIndex());
					break;
				case 3: // Area
					sortOrder = Double.compare(peak2.getIntegratedArea(), peak1.getIntegratedArea());
					break;
				case 4: // Start RT
					sortOrder = peakModel2.getStartRetentionTime() - peakModel1.getStartRetentionTime();
					break;
				case 5: // Stop RT
					sortOrder = peakModel2.getStopRetentionTime() - peakModel1.getStopRetentionTime();
					break;
				case 6: // Width
					sortOrder = peakModel2.getWidthByInflectionPoints() - peakModel1.getWidthByInflectionPoints();
					break;
				case 7:
				case 8:
					if(e1 instanceof IChromatogramPeakMSD && e2 instanceof IChromatogramPeakMSD) {
						IChromatogramPeakMSD chromatogramPeak1 = (IChromatogramPeakMSD)e1;
						IChromatogramPeakMSD chromatogramPeak2 = (IChromatogramPeakMSD)e2;
						switch(getPropertyIndex()) {
							case 7: // Scan# at Peak Maximum
								sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
								break;
							case 8: // S/N
								sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
								break;
						}
					}
					break;
				case 9: // Leading
					sortOrder = Float.compare(peakModel2.getLeading(), peakModel1.getLeading());
					break;
				case 10: // Tailing
					sortOrder = Float.compare(peakModel2.getTailing(), peakModel1.getTailing());
					break;
				case 11: // Model Description
					sortOrder = peak2.getModelDescription().compareTo(peak1.getModelDescription());
					break;
				case 12: // Suggested Components
					sortOrder = peak2.getSuggestedNumberOfComponents() - peak1.getSuggestedNumberOfComponents();
					break;
				case 13: // Name
					if(libraryInformation1 != null && libraryInformation2 != null) {
						sortOrder = libraryInformation2.getName().compareTo(libraryInformation1.getName());
					}
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

	private ILibraryInformation getLibraryInformation(Set<IIdentificationTarget> targets) {

		ILibraryInformation libraryInformation = null;
		List<IIdentificationTarget> targetsList = new ArrayList<>(targets);
		Collections.sort(targetsList, targetExtendedComparator);
		if(targetsList.size() >= 1) {
			libraryInformation = targetsList.get(0).getLibraryInformation();
		}
		return libraryInformation;
	}
}
