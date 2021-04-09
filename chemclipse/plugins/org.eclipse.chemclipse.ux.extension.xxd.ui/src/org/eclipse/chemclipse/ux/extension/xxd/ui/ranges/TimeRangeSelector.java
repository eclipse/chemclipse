/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
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

import java.util.concurrent.ExecutionException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IExtendedChart;

public class TimeRangeSelector {

	private static final Logger logger = Logger.getLogger(TimeRangeSelector.class);

	public static TimeRange adjustRange(BaseChart baseChart, Event event, TimeRanges timeRanges) {

		/*
		 * Try to get the time range and adjust the start | stop value.
		 */
		TimeRange timeRange = selectRange(baseChart, event, timeRanges);
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
	public static TimeRange selectRange(BaseChart baseChart, Event event, TimeRanges timeRanges) {

		/*
		 * Try to get the closest identifier of the user x selection.
		 */
		if(timeRanges != null) {
			String identifier = "";
			int minDelta = Integer.MAX_VALUE;
			double x = baseChart.getSelectedPrimaryAxisValue(event.x, IExtendedChart.X_AXIS);
			int selection = (int)x;
			//
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
			 * This could be also null if no time range was matched.
			 */
			return timeRanges.get(identifier);
		}
		//
		return null;
	}

	public static void updateTimeRangeUI(TimeRangesUI timeRangesUI, TimeRange timeRange, BaseChart baseChart) {

		if(timeRangesUI != null && timeRange != null) {
			try {
				DisplayUtils.executeInUserInterfaceThread(new Runnable() {

					@Override
					public void run() {

						String[] items = timeRangesUI.getItems();
						exitloop:
						for(int i = 0; i < items.length; i++) {
							if(items[i].equals(timeRange.getIdentifier())) {
								timeRangesUI.select(i);
								baseChart.redraw();
								break exitloop;
							}
						}
					}
				});
			} catch(InterruptedException | ExecutionException e) {
				logger.warn(e);
			}
		}
	}
}
