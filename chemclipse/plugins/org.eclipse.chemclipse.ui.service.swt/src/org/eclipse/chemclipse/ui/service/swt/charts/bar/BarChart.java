/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.charts.bar;

import java.util.List;

import org.eclipse.chemclipse.ui.service.swt.charts.BaseChart;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.ScrollableChart;
import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries.SeriesType;

public class BarChart extends ScrollableChart {

	public BarChart(Composite parent, int style) {
		super(parent, style);
	}

	public void addSeriesData(List<IBarSeriesData> barSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(barSeriesDataList != null && barSeriesDataList.size() > 0) {
			BaseChart baseChart = getBaseChart();
			baseChart.suspendUpdate(true);
			for(IBarSeriesData barSeriesData : barSeriesDataList) {
				/*
				 * Get the series data and apply the settings.
				 */
				try {
					ISeriesData seriesData = barSeriesData.getSeriesData();
					IBarSeries barSeries = (IBarSeries)createSeries(SeriesType.BAR, seriesData.getXSeries(), seriesData.getYSeries(), seriesData.getId());
					//
					IBarSeriesSettings barSeriesSettings = barSeriesData.getBarSeriesSettings();
					barSeries.setDescription(barSeriesSettings.getDescription());
					barSeries.setVisible(barSeriesSettings.isVisible());
					barSeries.setVisibleInLegend(barSeriesSettings.isVisibleInLegend());
					barSeries.setBarColor(barSeriesSettings.getBarColor());
					barSeries.setBarPadding(barSeriesSettings.getBarPadding());
					barSeries.setBarWidth(barSeriesSettings.getBarWidth());
					barSeries.setBarWidthStyle(barSeriesSettings.getBarWidthStyle());
				} catch(SeriesException e) {
					//
				}
			}
			baseChart.suspendUpdate(false);
			adjustRange(true);
			baseChart.redraw();
		}
	}
}
