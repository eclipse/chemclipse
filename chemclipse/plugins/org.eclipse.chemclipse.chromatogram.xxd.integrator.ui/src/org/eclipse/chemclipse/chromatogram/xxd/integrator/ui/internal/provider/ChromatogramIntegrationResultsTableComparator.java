/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResult;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ChromatogramIntegrationResultsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	public ChromatogramIntegrationResultsTableComparator() {
		super();
		setPropertyIndex(2);// ion
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		/*
		 * SYNCHRONIZE: IntegrationResultLabelProvider
		 * IntegrationResultTabelComparator IntegrationResultListUI
		 */
		IChromatogramIntegrationResult chromatogramIntegrationResult1 = (IChromatogramIntegrationResult)e1;
		IChromatogramIntegrationResult chromatogramIntegrationResult2 = (IChromatogramIntegrationResult)e2;
		int sortOrder;
		switch(getPropertyIndex()) {
			case 0: // Chromatogram Area
				sortOrder = Double.compare(chromatogramIntegrationResult2.getChromatogramArea(), chromatogramIntegrationResult1.getChromatogramArea());
				break;
			case 1: // Background Area
				sortOrder = Double.compare(chromatogramIntegrationResult2.getBackgroundArea(), chromatogramIntegrationResult1.getBackgroundArea());
				break;
			case 2: // ion
				sortOrder = Double.compare(chromatogramIntegrationResult2.getIon(), chromatogramIntegrationResult1.getIon());
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
