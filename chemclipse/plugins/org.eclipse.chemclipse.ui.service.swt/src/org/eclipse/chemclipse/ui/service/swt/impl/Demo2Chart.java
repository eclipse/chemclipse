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
package org.eclipse.chemclipse.ui.service.swt.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.ui.service.swt.charts.ChartSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.IChartSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.line.ILineSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.line.ILineSeriesSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.line.LineChart;
import org.eclipse.chemclipse.ui.service.swt.charts.line.LineSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class Demo2Chart extends LineChart implements IChart {

	public Demo2Chart(Composite parent, int style) {
		super(parent, style);
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = new ChartSettings();
		chartSettings //
				.setOrientation(SWT.HORIZONTAL) //
				.setHorizontalSliderVisible(true) //
				.setVerticalSliderVisible(true) //
				.setUseZeroX(false) //
				.setUseZeroY(false);
		applySettings(chartSettings);
		/*
		 * Create series.
		 */
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		ISeriesData seriesData = SeriesConverter.getSeries(SeriesConverter.LINE_SERIES_1);
		seriesData.setId("Demo");
		//
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setEnableArea(false);
		lineSeriesDataList.add(lineSeriesData);
		/*
		 * Set series.
		 */
		addSeriesData(lineSeriesDataList);
	}
}
