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
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IExtendedChart;

public class TimeRangeSelector {

	public void adjustRange(BaseChart baseChart, Event event, TimeRanges timeRanges) {

		String identifier = "";
		int minDelta = Integer.MAX_VALUE;
		double x = baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
		int selection = (int)x;
		/*
		 * Try to get the closest identifier of the user x selection.
		 */
		for(TimeRange timeRange : timeRanges.values()) {
			int deltaStart = Math.abs(selection - timeRange.getStart());
			int deltaStop = Math.abs(selection - timeRange.getStop());
			int delta = Math.min(deltaStart, deltaStop);
			//
			if(delta < minDelta) {
				minDelta = delta;
				identifier = timeRange.getIdentifier();
			}
		}
		/*
		 * Try to get the time range and adjust the start | stop value.
		 */
		TimeRange timeRange = timeRanges.get(identifier);
		if(timeRange != null) {
			int deltaStart = Math.abs(selection - timeRange.getStart());
			int deltaStop = Math.abs(selection - timeRange.getStop());
			//
			if(deltaStart < deltaStop) {
				timeRange.updateStart(selection);
			} else {
				timeRange.updateStop(selection);
			}
		}
	}
}
