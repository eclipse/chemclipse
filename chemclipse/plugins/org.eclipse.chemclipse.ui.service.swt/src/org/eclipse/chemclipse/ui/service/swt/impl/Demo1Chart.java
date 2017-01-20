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

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ui.service.swt.core.ChartSettings;
import org.eclipse.chemclipse.ui.service.swt.core.ILineSeriesData;
import org.eclipse.chemclipse.ui.service.swt.core.LineChart;
import org.eclipse.chemclipse.ui.service.swt.core.LineSeriesData;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries.PlotSymbolType;

public class Demo1Chart extends LineChart {

	public Demo1Chart(Composite parent, int style) {
		super(parent, style);
		applySettings(new ChartSettings());
		ILineSeriesData lineSeriesData = new LineSeriesData();
		lineSeriesData.setXSeries(SeriesConverter.getXSeries());
		lineSeriesData.setYSeries(SeriesConverter.getYSeries());
		lineSeriesData.setId("Demo");
		lineSeriesData.setEnableArea(true);
		lineSeriesData.setSymbolType(PlotSymbolType.NONE);
		lineSeriesData.setSymbolSize(8);
		lineSeriesData.setLineColor(Colors.RED);
		lineSeriesData.setLineWidth(1);
		lineSeriesData.setEnableStack(false);
		lineSeriesData.setEnableStep(false);
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		lineSeriesDataList.add(lineSeriesData);
		addSeriesData(lineSeriesDataList);
	}
}
