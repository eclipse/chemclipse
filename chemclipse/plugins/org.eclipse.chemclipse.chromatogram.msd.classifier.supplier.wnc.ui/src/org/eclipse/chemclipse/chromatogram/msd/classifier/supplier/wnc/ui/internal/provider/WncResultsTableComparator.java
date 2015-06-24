/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.swt.ui.viewers.AbstractRecordTableComparator;
import org.eclipse.chemclipse.swt.ui.viewers.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class WncResultsTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		IWncIon wnc1 = (IWncIon)e1;
		IWncIon wnc2 = (IWncIon)e2;
		int sortOrder;
		switch(getPropertyIndex()) {
			case 0: // Name
				sortOrder = wnc2.getName().codePointAt(0) - wnc1.getName().codePointAt(0);
				break;
			case 1: // ion
				sortOrder = wnc2.getIon() - wnc1.getIon();
				break;
			case 2: // Percentage Sum Intensity
				sortOrder = Double.compare(wnc2.getPercentageSumIntensity(), wnc1.getPercentageSumIntensity());
				break;
			case 3: // Percentage Max Intensity
				sortOrder = Double.compare(wnc2.getPercentageMaxIntensity(), wnc1.getPercentageMaxIntensity());
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
