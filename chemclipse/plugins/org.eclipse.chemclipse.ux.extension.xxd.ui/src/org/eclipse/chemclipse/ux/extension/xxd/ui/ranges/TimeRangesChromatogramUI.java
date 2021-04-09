/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.ChromatogramPeakChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.ScrollableChart;

public class TimeRangesChromatogramUI extends Composite {

	private TimeRangesUI timeRangesUI;
	private ChromatogramPeakChart chromatogramChart;
	private TimeRangeMarker timeRangeMarker;
	private TimeRangeAdjustmentHandler timeRangeAdjustmentHandler = new TimeRangeAdjustmentHandler();
	private TimeRangeSelectionHandler timeRangeSelectionHandler = new TimeRangeSelectionHandler();
	//
	private TimeRanges timeRanges = null;

	public TimeRangesChromatogramUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setTimeRanges(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
		this.timeRangesUI.setInput(timeRanges);
		timeRangeAdjustmentHandler.update(this.timeRangesUI, this.timeRanges);
		timeRangeSelectionHandler.update(this.timeRangesUI, this.timeRanges);
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
		//
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.addHandledEventProcessor(timeRangeAdjustmentHandler);
		chartSettings.addHandledEventProcessor(timeRangeSelectionHandler);
		chromatogramChart.applySettings(chartSettings);
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

	private void updateTimeRangeMarker() {

		timeRangeMarker.getTimeRanges().clear();
		if(timeRanges != null) {
			/*
			 * Show the time range composite.
			 */
			setTimeRangesVisible(true);
			timeRangeMarker.getTimeRanges().addAll(timeRanges.values());
		} else {
			/*
			 * Hide the time range composite.
			 */
			setTimeRangesVisible(false);
		}
		chromatogramChart.getBaseChart().redraw();
	}

	private void setTimeRangesVisible(boolean visible) {

		timeRangesUI.setVisible(visible);
		Object layoutData = timeRangesUI.getLayoutData();
		if(layoutData instanceof GridData) {
			GridData gridData = (GridData)layoutData;
			gridData.exclude = !visible;
		}
		Composite parent = timeRangesUI.getParent();
		parent.layout(true);
		parent.redraw();
	}
}