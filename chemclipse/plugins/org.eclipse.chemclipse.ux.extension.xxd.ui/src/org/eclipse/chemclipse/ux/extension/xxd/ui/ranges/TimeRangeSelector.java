/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IExtendedChart;

public class TimeRangeSelector {

	public static TimeRange adjustRange(BaseChart baseChart, Event event, TimeRanges timeRanges) {

		/*
		 * Try to get the time range and adjust the start | stop value.
		 */
		TimeRange timeRange = selectRange(baseChart, event.x, -1, -1, timeRanges);
		if(timeRange != null) {
			double x = baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
			int selection = (int)x;
			//
			int deltaStart = Math.abs(selection - timeRange.getStart());
			int deltaStop = Math.abs(selection - timeRange.getStop());
			//
			if(deltaStart < deltaStop) {
				timeRange.updateStart(selection);
			} else {
				timeRange.updateStop(selection);
			}
		}
		//
		return timeRange;
	}

	/**
	 * This method may return null.
	 * 
	 * @param baseChart
	 * @param event
	 * @param timeRanges
	 * @return TimeRange
	 */
	public static TimeRange selectRange(BaseChart baseChart, int xEvent, int xStart, int xStop, TimeRanges timeRanges) {

		/*
		 * Try to get the closest identifier of the user x selection.
		 */
		if(timeRanges != null) {
			TimeRange timeRange = null;
			int x = (int)baseChart.getSelectedPrimaryAxisValue(xEvent, IExtendedChart.X_AXIS);
			TimeRange timeRangeLocked = TimeRangeSupport.getTimeRangeLocked(timeRanges.values());
			if(timeRangeLocked != null) {
				/*
				 * Locked Element
				 */
				timeRange = timeRangeLocked;
			} else {
				/*
				 * Normal Search
				 */
				String identifier = "";
				int minDelta = Integer.MAX_VALUE;
				//
				for(TimeRange timeRangeX : timeRanges.values()) {
					int deltaStart = Math.abs(x - timeRangeX.getStart());
					int deltaStop = Math.abs(x - timeRangeX.getStop());
					int delta = Math.min(deltaStart, deltaStop);
					//
					if(delta < minDelta) {
						minDelta = delta;
						identifier = timeRangeX.getIdentifier();
					}
				}
				/*
				 * Try to get the time range and adjust the start | stop value.
				 * This could be also null if no time range was matched.
				 */
				timeRange = timeRanges.get(identifier);
				if(timeRange != null) {
					if(xStart != -1 && xStop != -1) {
						double positionStart = baseChart.getSelectedPrimaryAxisValue(xStart, IExtendedChart.X_AXIS);
						double positionStop = baseChart.getSelectedPrimaryAxisValue(xStop, IExtendedChart.X_AXIS);
						//
						if(positionStart < timeRange.getStart() && positionStop < timeRange.getStart()) {
							timeRange = null; // The selection is left of the time range area.
						} else if(positionStart > timeRange.getStop() && positionStop > timeRange.getStop()) {
							timeRange = null; // The selection is right of the time range area.
						}
					}
				}
			}
			/*
			 * Only select the time range if it is inside of a certain offset.
			 */
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			int xOffset = preferenceStore.getInt(PreferenceSupplier.P_TIME_RANGE_SELECTION_OFFSET);
			if(timeRange != null && xOffset >= 0) {
				if(x < (timeRange.getStart() - xOffset) || x > (timeRange.getStop() + xOffset)) {
					timeRange = null;
				}
			}
			//
			return timeRange;
		}
		//
		return null;
	}
}