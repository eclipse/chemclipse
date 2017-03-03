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
package org.eclipse.chemclipse.ui.service.swt.charts.line;

import java.util.List;

import org.eclipse.chemclipse.ui.service.swt.charts.BaseChart;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.ScrollableChart;
import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

public class LineChart extends ScrollableChart {

	public LineChart(Composite parent, int style) {
		super(parent, style);
	}

	public void addSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(lineSeriesDataList != null && lineSeriesDataList.size() > 0) {
			BaseChart baseChart = getBaseChart();
			baseChart.suspendUpdate(true);
			for(ILineSeriesData lineSeriesData : lineSeriesDataList) {
				/*
				 * Get the series data and apply the settings.
				 */
				try {
					ISeriesData seriesData = lineSeriesData.getSeriesData();
					ILineSeries lineSeries = (ILineSeries)createSeries(SeriesType.LINE, seriesData.getXSeries(), seriesData.getYSeries(), seriesData.getId());
					//
					ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
					lineSeries.setDescription(lineSeriesSettings.getDescription());
					lineSeries.setVisible(lineSeriesSettings.isVisible());
					lineSeries.setVisibleInLegend(lineSeriesSettings.isVisibleInLegend());
					lineSeries.setAntialias(lineSeriesSettings.getAntialias());
					lineSeries.enableArea(lineSeriesSettings.isEnableArea());
					lineSeries.setSymbolType(lineSeriesSettings.getSymbolType());
					lineSeries.setSymbolSize(lineSeriesSettings.getSymbolSize());
					lineSeries.setSymbolColor(lineSeriesSettings.getSymbolColor());
					lineSeries.setLineColor(lineSeriesSettings.getLineColor());
					lineSeries.setLineWidth(lineSeriesSettings.getLineWidth());
					lineSeries.enableStack(lineSeriesSettings.isEnableStack());
					lineSeries.enableStep(lineSeriesSettings.isEnableStep());
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
