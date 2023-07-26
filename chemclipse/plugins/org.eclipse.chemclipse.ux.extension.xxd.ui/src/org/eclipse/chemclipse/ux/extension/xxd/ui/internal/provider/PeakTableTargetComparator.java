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

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakTableTargetComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IPeak peak1 && e2 instanceof IPeak peak2) {
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(peak2.getPeakModel().getPeakMaximum().getRetentionTime(), peak1.getPeakModel().getPeakMaximum().getRetentionTime());
					break;
				case 1:
					ILibraryInformation libraryInformation1 = IIdentificationTarget.getLibraryInformation(peak1);
					ILibraryInformation libraryInformation2 = IIdentificationTarget.getLibraryInformation(peak2);
					if(libraryInformation1 != null && libraryInformation2 != null) {
						sortOrder = libraryInformation2.getName().compareTo(libraryInformation1.getName());
					}
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
		//
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		//
		return sortOrder;
	}
}