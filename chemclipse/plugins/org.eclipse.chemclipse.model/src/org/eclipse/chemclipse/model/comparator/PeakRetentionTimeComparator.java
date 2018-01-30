/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.comparator;

import java.util.Comparator;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.support.comparator.SortOrder;

public class PeakRetentionTimeComparator implements Comparator<IPeak> {

	private SortOrder sortOrder;

	public PeakRetentionTimeComparator() {
		sortOrder = SortOrder.ASC;
	}

	public PeakRetentionTimeComparator(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(IPeak peak1, IPeak peak2) {

		int retentionTime1 = peak1.getPeakModel().getRetentionTimeAtPeakMaximum();
		int retentionTime2 = peak2.getPeakModel().getRetentionTimeAtPeakMaximum();
		//
		int returnValue;
		switch(sortOrder) {
			case ASC:
				returnValue = Integer.compare(retentionTime1, retentionTime2);
				break;
			case DESC:
				returnValue = Integer.compare(retentionTime2, retentionTime1);
				break;
			default:
				returnValue = Integer.compare(retentionTime1, retentionTime2);
				break;
		}
		return returnValue;
	}
}
