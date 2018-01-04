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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResult;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakIntegrationResultsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: IntegrationResultLabelProvider
		 * IntegrationResultTabelComparator IntegrationResultListUI
		 */
		IPeakIntegrationResult peakIntegrationResult1 = (IPeakIntegrationResult)e1;
		IPeakIntegrationResult peakIntegrationResult2 = (IPeakIntegrationResult)e2;
		int sortOrder;
		switch(getPropertyIndex()) {
			case 0: // Start RT
				sortOrder = peakIntegrationResult2.getStartRetentionTime() - peakIntegrationResult1.getStartRetentionTime();
				break;
			case 1: // Stop RT
				sortOrder = peakIntegrationResult2.getStopRetentionTime() - peakIntegrationResult1.getStopRetentionTime();
				break;
			case 2: // Integrated Area
				sortOrder = Double.compare(peakIntegrationResult2.getIntegratedArea(), peakIntegrationResult1.getIntegratedArea());
				break;
			default:
				sortOrder = 0;
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
