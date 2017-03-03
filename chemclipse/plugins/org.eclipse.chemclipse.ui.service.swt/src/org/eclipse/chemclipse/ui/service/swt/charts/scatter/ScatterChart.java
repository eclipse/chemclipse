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
package org.eclipse.chemclipse.ui.service.swt.charts.scatter;

import java.util.List;

import org.eclipse.chemclipse.ui.service.swt.charts.BaseChart;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.ScrollableChart;
import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.LineStyle;

public class ScatterChart extends ScrollableChart {

	public ScatterChart(Composite parent, int style) {
		super(parent, style);
	}

	public void addSeriesData(List<IScatterSeriesData> scatterSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
		if(scatterSeriesDataList != null && scatterSeriesDataList.size() > 0) {
			/*
			 * Set the data.
			 */
			BaseChart baseChart = getBaseChart();
			baseChart.suspendUpdate(true);
			for(IScatterSeriesData scatterSeriesData : scatterSeriesDataList) {
				/*
				 * Get the series data and apply the settings.
				 */
				try {
					ISeriesData seriesData = scatterSeriesData.getSeriesData();
					ILineSeries scatterSeries = (ILineSeries)createSeries(SeriesType.LINE, seriesData.getXSeries(), seriesData.getYSeries(), seriesData.getId());
					//
					IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
					scatterSeries.setDescription(scatterSeriesSettings.getDescription());
					scatterSeries.setVisible(scatterSeriesSettings.isVisible());
					scatterSeries.setVisibleInLegend(scatterSeriesSettings.isVisibleInLegend());
					scatterSeries.enableArea(false);
					scatterSeries.setSymbolType(scatterSeriesSettings.getSymbolType());
					scatterSeries.setSymbolSize(scatterSeriesSettings.getSymbolSize());
					scatterSeries.setSymbolColor(scatterSeriesSettings.getSymbolColor());
					scatterSeries.setLineStyle(LineStyle.NONE);
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
