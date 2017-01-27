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
import org.eclipse.chemclipse.ui.service.swt.charts.bar.BarChart;
import org.eclipse.chemclipse.ui.service.swt.charts.bar.BarSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.bar.IBarSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.bar.IBarSeriesSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class Demo5Chart extends BarChart {

	public Demo5Chart(Composite parent, int style) {
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
		List<IBarSeriesData> barSeriesDataList = new ArrayList<IBarSeriesData>();
		ISeriesData seriesData = SeriesConverter.getSeries(SeriesConverter.BAR_SERIES_1);
		seriesData.setId("Demo");
		//
		IBarSeriesData barSeriesData = new BarSeriesData(seriesData);
		IBarSeriesSettings barSeriesSettings = barSeriesData.getBarSeriesSettings();
		barSeriesSettings.setDescription("");
		barSeriesDataList.add(barSeriesData);
		/*
		 * Set series.
		 */
		addSeriesData(barSeriesDataList);
	}
}
