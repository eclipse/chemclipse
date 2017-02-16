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

import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.custom.PCAChart;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.IScatterSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.IScatterSeriesSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.ScatterSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.ILineSeries.PlotSymbolType;

public class Demo7Chart extends PCAChart implements IChart {

	private Color COLOR_RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	private Color COLOR_BLUE = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	private Color COLOR_MAGENTA = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	private Color COLOR_CYAN = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	private Color COLOR_GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	//
	private int SYMBOL_SIZE = 8;

	public Demo7Chart(Composite parent, int style) {
		super(parent, style);
		/*
		 * Create series.
		 */
		List<ISeriesData> scatterSeriesList = SeriesConverter.getScatterSeries(SeriesConverter.SCATTER_SERIES_1);
		List<IScatterSeriesData> scatterSeriesDataList = new ArrayList<IScatterSeriesData>();
		//
		for(ISeriesData seriesData : scatterSeriesList) {
			IScatterSeriesData scatterSeriesData = new ScatterSeriesData(seriesData);
			IScatterSeriesSettings scatterSeriesSettings = scatterSeriesData.getScatterSeriesSettings();
			/*
			 * Set the color and symbol type.
			 */
			double x = seriesData.getXSeries()[0];
			double y = seriesData.getYSeries()[0];
			scatterSeriesSettings.setSymbolSize(SYMBOL_SIZE);
			setSymbolSize(SYMBOL_SIZE);
			//
			if(x > 0 && y > 0) {
				scatterSeriesSettings.setSymbolColor(COLOR_RED);
				scatterSeriesSettings.setSymbolType(PlotSymbolType.SQUARE);
			} else if(x > 0 && y < 0) {
				scatterSeriesSettings.setSymbolColor(COLOR_BLUE);
				scatterSeriesSettings.setSymbolType(PlotSymbolType.TRIANGLE);
			} else if(x < 0 && y > 0) {
				scatterSeriesSettings.setSymbolColor(COLOR_MAGENTA);
				scatterSeriesSettings.setSymbolType(PlotSymbolType.DIAMOND);
			} else if(x < 0 && y < 0) {
				scatterSeriesSettings.setSymbolColor(COLOR_CYAN);
				scatterSeriesSettings.setSymbolType(PlotSymbolType.INVERTED_TRIANGLE);
			} else {
				scatterSeriesSettings.setSymbolColor(COLOR_GRAY);
				scatterSeriesSettings.setSymbolType(PlotSymbolType.CIRCLE);
			}
			//
			scatterSeriesDataList.add(scatterSeriesData);
		}
		/*
		 * Set series.
		 */
		addSeriesData(scatterSeriesDataList);
	}
}
