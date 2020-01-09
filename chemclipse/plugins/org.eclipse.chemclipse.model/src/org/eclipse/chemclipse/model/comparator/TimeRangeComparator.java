/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.ranges.TimeRange;

public class TimeRangeComparator implements Comparator<TimeRange> {

	@Override
	public int compare(TimeRange timeRange1, TimeRange timeRange2) {

		int result = Integer.compare(timeRange1.getStart(), timeRange2.getStart());
		if(result == 0) {
			result = Integer.compare(timeRange1.getStop(), timeRange2.getStop());
			if(result == 0) {
				result = timeRange1.getIdentifier().compareTo(timeRange2.getIdentifier());
			}
		}
		return result;
	}
}
