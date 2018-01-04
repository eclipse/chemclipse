/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import org.eclipse.chemclipse.model.support.PeakQuantitation;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class PeakQuantitationListTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof PeakQuantitation && e2 instanceof PeakQuantitation) {
			PeakQuantitation peakQuantitationEntry1 = (PeakQuantitation)e1;
			PeakQuantitation peakQuantitationEntry2 = (PeakQuantitation)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = Integer.compare(peakQuantitationEntry2.getRetentionTime(), peakQuantitationEntry1.getRetentionTime());
					break;
				case 1:
					sortOrder = Double.compare(peakQuantitationEntry2.getIntegratedArea(), peakQuantitationEntry1.getIntegratedArea());
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
