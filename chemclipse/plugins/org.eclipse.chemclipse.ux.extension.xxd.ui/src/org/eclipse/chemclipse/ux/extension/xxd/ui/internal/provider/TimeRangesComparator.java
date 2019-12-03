/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class TimeRangesComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof TimeRange && e2 instanceof TimeRange) {
			//
			TimeRange timeRange1 = (TimeRange)e1;
			TimeRange timeRange2 = (TimeRange)e2;
			//
			switch(getPropertyIndex()) {
				case 0:
					sortOrder = timeRange2.getIdentifier().compareTo(timeRange1.getIdentifier());
					break;
				case 1:
					sortOrder = Integer.compare(timeRange2.getStart(), timeRange1.getStart());
					break;
				case 2:
					sortOrder = Integer.compare(timeRange2.getCenter(), timeRange1.getCenter());
					break;
				case 3:
					sortOrder = Integer.compare(timeRange2.getStop(), timeRange1.getStop());
					break;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
