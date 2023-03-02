/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;

public class TimeRangeSupport {

	public static TimeRange getTimeRangePrevious(TimeRanges timeRanges, TimeRange timeRange) {

		if(timeRanges != null) {
			List<TimeRange> timeRangeList = new ArrayList<>(timeRanges.values());
			Collections.sort(timeRangeList, (t1, t2) -> Integer.compare(t1.getStart(), t2.getStart()));
			if(!timeRangeList.isEmpty()) {
				if(timeRange == null) {
					/*
					 * No selection yet
					 */
					timeRange = timeRangeList.get(0);
				} else {
					/*
					 * Previous or first
					 */
					int index = timeRangeList.indexOf(timeRange) - 1;
					if(index >= 0 && index < timeRangeList.size()) {
						timeRange = timeRangeList.get(index);
					} else {
						timeRange = timeRangeList.get(0);
					}
				}
				/*
				 * Update
				 */
				return timeRange;
			}
		}
		//
		return null;
	}

	public static TimeRange getTimeRangeNext(TimeRanges timeRanges, TimeRange timeRange) {

		if(timeRanges != null) {
			List<TimeRange> timeRangeList = new ArrayList<>(timeRanges.values());
			Collections.sort(timeRangeList, (t1, t2) -> Integer.compare(t1.getStart(), t2.getStart()));
			if(!timeRangeList.isEmpty()) {
				if(timeRange == null) {
					/*
					 * No selection yet
					 */
					int last = timeRangeList.size() - 1;
					last = (last < 0) ? 0 : last;
					timeRange = timeRangeList.get(last);
				} else {
					/*
					 * Previous or first
					 */
					int index = timeRangeList.indexOf(timeRange) + 1;
					if(index >= 0 && index < timeRangeList.size()) {
						timeRange = timeRangeList.get(index);
					} else {
						int last = timeRangeList.size() - 1;
						last = (last < 0) ? 0 : last;
						timeRange = timeRangeList.get(last);
					}
				}
				/*
				 * Update
				 */
				return timeRange;
			}
		}
		//
		return null;
	}
}