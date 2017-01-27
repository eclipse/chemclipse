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

import org.eclipse.chemclipse.ui.service.swt.core.ChartSettings;
import org.eclipse.chemclipse.ui.service.swt.core.IChartSettings;
import org.eclipse.chemclipse.ui.service.swt.core.ILineSeriesData;
import org.eclipse.chemclipse.ui.service.swt.core.ILineSeriesSettings;
import org.eclipse.chemclipse.ui.service.swt.core.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.core.LineChart;
import org.eclipse.chemclipse.ui.service.swt.core.LineSeriesData;
import org.eclipse.chemclipse.ui.service.swt.core.SeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class Demo4Chart extends LineChart {

	public Demo4Chart(Composite parent, int style) {
		super(parent, style);
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = new ChartSettings();
		chartSettings //
				.setOrientation(SWT.VERTICAL) //
				.setHorizontalSliderVisible(true) //
				.setVerticalSliderVisible(true) //
				.setUseZeroX(false) //
				.setUseZeroY(false);
		applySettings(chartSettings);
		/*
		 * Create series.
		 */
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		//
		ISeriesData seriesData = new SeriesData();
		seriesData.setXSeries(SeriesConverter.getXSeries());
		seriesData.setYSeries(SeriesConverter.getYSeries());
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
