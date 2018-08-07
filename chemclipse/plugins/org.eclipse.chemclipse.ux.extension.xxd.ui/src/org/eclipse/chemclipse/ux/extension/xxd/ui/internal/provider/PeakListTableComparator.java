/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.comparator.TargetExtendedComparator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	private TargetExtendedComparator targetExtendedComparator;
	private double chromatogramPeakArea = 0.0d;

	public PeakListTableComparator() {
		targetExtendedComparator = new TargetExtendedComparator(SortOrder.DESC);
	}

	public void setChromatogramPeakArea(double chromatogramPeakArea) {

		this.chromatogramPeakArea = chromatogramPeakArea;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IPeak && e2 instanceof IPeak) {
			//
			IPeak peak1 = (IPeak)e1;
			IPeakModel peakModel1 = peak1.getPeakModel();
			ILibraryInformation libraryInformation1 = getLibraryInformation(new ArrayList<IPeakTarget>(peak1.getTargets()));
			//
			IPeak peak2 = (IPeak)e2;
			IPeakModel peakModel2 = peak2.getPeakModel();
			ILibraryInformation libraryInformation2 = getLibraryInformation(new ArrayList<IPeakTarget>(peak2.getTargets()));
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Boolean.compare(peak2.isActiveForAnalysis(), peak1.isActiveForAnalysis());
					break;
				case 1:
					sortOrder = peakModel2.getRetentionTimeAtPeakMaximum() - peakModel1.getRetentionTimeAtPeakMaximum();
					break;
				case 2:
					sortOrder = Float.compare(peakModel2.getPeakMaximum().getRetentionIndex(), peakModel1.getPeakMaximum().getRetentionIndex());
					break;
				case 3:
					sortOrder = Double.compare(peak2.getIntegratedArea(), peak1.getIntegratedArea());
					break;
				case 4:
					sortOrder = peakModel2.getStartRetentionTime() - peakModel1.getStartRetentionTime();
					break;
				case 5:
					sortOrder = peakModel2.getStopRetentionTime() - peakModel1.getStopRetentionTime();
					break;
				case 6:
					sortOrder = peakModel2.getWidthByInflectionPoints() - peakModel1.getWidthByInflectionPoints();
					break;
				case 7:
				case 8:
					if(peak1 instanceof IChromatogramPeakMSD && peak2 instanceof IChromatogramPeakMSD) {
						IChromatogramPeakMSD chromatogramPeak1 = (IChromatogramPeakMSD)peak1;
						IChromatogramPeakMSD chromatogramPeak2 = (IChromatogramPeakMSD)peak2;
						switch(getPropertyIndex()) {
							case 7:
								sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
								break;
							case 8:
								sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
								break;
						}
					} else if(peak1 instanceof IChromatogramPeakCSD && peak2 instanceof IChromatogramPeakCSD) {
						IChromatogramPeakCSD chromatogramPeak1 = (IChromatogramPeakCSD)peak1;
						IChromatogramPeakCSD chromatogramPeak2 = (IChromatogramPeakCSD)peak2;
						switch(getPropertyIndex()) {
							case 7:
								sortOrder = chromatogramPeak2.getScanMax() - chromatogramPeak1.getScanMax();
								break;
							case 8:
								sortOrder = Float.compare(chromatogramPeak2.getSignalToNoiseRatio(), chromatogramPeak1.getSignalToNoiseRatio());
								break;
						}
					}
					break;
				case 9:
					sortOrder = Float.compare(peakModel2.getLeading(), peakModel1.getLeading());
					break;
				case 10:
					sortOrder = Float.compare(peakModel2.getTailing(), peakModel1.getTailing());
					break;
				case 11:
					sortOrder = peak2.getModelDescription().compareTo(peak1.getModelDescription());
					break;
				case 12:
					sortOrder = peak2.getSuggestedNumberOfComponents() - peak1.getSuggestedNumberOfComponents();
					break;
				case 13:
					if(libraryInformation1 != null && libraryInformation2 != null) {
						sortOrder = libraryInformation2.getName().compareTo(libraryInformation1.getName());
					}
					break;
				case 14:
					if(chromatogramPeakArea > 0) {
						double factor = 100.0d / chromatogramPeakArea;
						double peakAreaPercent1 = factor * peak1.getIntegratedArea();
						double peakAreaPercent2 = factor * peak2.getIntegratedArea();
						return Double.compare(peakAreaPercent2, peakAreaPercent1);
					} else {
						sortOrder = 0;
					}
					break;
				case 15:
					sortOrder = Integer.compare(peak2.getInternalStandards().size(), peak1.getInternalStandards().size());
					break;
				case 16:
					sortOrder = peak2.getClassifier().compareTo(peak1.getClassifier());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}

	private ILibraryInformation getLibraryInformation(List<IPeakTarget> targets) {

		ILibraryInformation libraryInformation = null;
		targets = new ArrayList<IPeakTarget>(targets);
		Collections.sort(targets, targetExtendedComparator);
		if(targets.size() >= 1) {
			libraryInformation = targets.get(0).getLibraryInformation();
		}
		return libraryInformation;
	}
}
