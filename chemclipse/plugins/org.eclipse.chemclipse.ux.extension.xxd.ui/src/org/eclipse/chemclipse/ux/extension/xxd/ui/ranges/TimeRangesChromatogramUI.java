/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.ChromatogramPeakChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.ICustomSelectionHandler;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public class TimeRangesChromatogramUI extends Composite {

	private TimeRangesUI timeRangesUI;
	private ChromatogramPeakChart chromatogramChart;
	private TimeRangeMarker timeRangeMarker;
	//
	private TimeRanges timeRanges = null;
	private TimeRangeSelector timeRangeSelector = new TimeRangeSelector();

	public TimeRangesChromatogramUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setTimeRanges(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		timeRangesUI.setInput(timeRanges);
		updateTimeRangeMarker();
	}

	public ChromatogramPeakChart getChromatogramChart() {

		return chromatogramChart;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		timeRangesUI = createTimeRangesUI(this);
		chromatogramChart = createChromatogram(this);
	}

	private TimeRangesUI createTimeRangesUI(Composite parent) {

		TimeRangesUI timeRangesUI = new TimeRangesUI(parent, SWT.NONE);
		timeRangesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		timeRangesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				if(timeRanges != null) {
					updateTimeRangeMarker();
				}
			}
		});
		//
		return timeRangesUI;
	}

	private ChromatogramPeakChart createChromatogram(Composite parent) {

		ChromatogramPeakChart chromatogramChart = new ChromatogramPeakChart(parent, SWT.NONE);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		timeRangeMarker = addTimeRangeMarker(chromatogramChart);
		enableTimeRangeAdjustments(chromatogramChart);
		//
		return chromatogramChart;
	}

	private TimeRangeMarker addTimeRangeMarker(ScrollableChart scrollableChart) {

		BaseChart baseChart = scrollableChart.getBaseChart();
		IPlotArea plotArea = baseChart.getPlotArea();
		TimeRangeMarker timeRangeMarker = new TimeRangeMarker(baseChart);
		plotArea.addCustomPaintListener(timeRangeMarker);
		return timeRangeMarker;
	}

	private void enableTimeRangeAdjustments(ScrollableChart scrollableChart) {

		BaseChart baseChart = scrollableChart.getBaseChart();
		baseChart.addCustomPointSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				if(timeRanges != null) {
					TimeRange timeRange = timeRangeSelector.adjustRange(baseChart, event, timeRanges);
					if(timeRange != null && timeRanges != null) {
						String[] items = timeRangesUI.getItems();
						exitloop:
						for(int i = 0; i < items.length; i++) {
							if(items[i].equals(timeRange.getIdentifier())) {
								timeRangesUI.select(i);
								break exitloop;
							}
						}
					}
					scrollableChart.redraw();
				}
			}
		});
	}

	private void updateTimeRangeMarker() {

		timeRangeMarker.getTimeRanges().clear();
		if(timeRanges != null) {
			timeRangeMarker.getTimeRanges().addAll(timeRanges.values());
		}
		chromatogramChart.getBaseChart().redraw();
	}
}