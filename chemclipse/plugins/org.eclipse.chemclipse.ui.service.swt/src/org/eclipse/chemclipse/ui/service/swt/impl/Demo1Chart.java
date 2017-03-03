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

import org.eclipse.chemclipse.ui.service.swt.charts.ColorFormatSupport;
import org.eclipse.chemclipse.ui.service.swt.charts.IChartSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.IPrimaryAxisSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.ISecondaryAxisSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.SecondaryAxisSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.converter.MillisecondsToMinuteConverter;
import org.eclipse.chemclipse.ui.service.swt.charts.converter.MillisecondsToScanNumberConverter;
import org.eclipse.chemclipse.ui.service.swt.charts.converter.RelativeIntensityConverter;
import org.eclipse.chemclipse.ui.service.swt.charts.line.ILineSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.line.ILineSeriesSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.line.LineChart;
import org.eclipse.chemclipse.ui.service.swt.charts.line.LineSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis.Position;

public class Demo1Chart extends LineChart implements IChart {

	public Demo1Chart(Composite parent, int style) {
		super(parent, style);
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = getChartSettings();
		chartSettings.setOrientation(SWT.HORIZONTAL);
		chartSettings.setHorizontalSliderVisible(true);
		chartSettings.setVerticalSliderVisible(true);
		chartSettings.setUseZeroX(true);
		chartSettings.setUseZeroY(true);
		//
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle("Retention Time (milliseconds)");
		primaryAxisSettingsX.setDecimalFormat(ColorFormatSupport.decimalFormatVariable);
		primaryAxisSettingsX.setColor(ColorFormatSupport.COLOR_BLACK);
		primaryAxisSettingsX.setPosition(Position.Secondary);
		primaryAxisSettingsX.setVisible(false);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle("Intensity");
		primaryAxisSettingsY.setDecimalFormat(ColorFormatSupport.decimalFormatScientific);
		primaryAxisSettingsY.setColor(ColorFormatSupport.COLOR_BLACK);
		//
		try {
			ISecondaryAxisSettings secondaryAxisSettingsX1 = new SecondaryAxisSettings("Scan Number", new MillisecondsToScanNumberConverter(50, 50));
			secondaryAxisSettingsX1.setPosition(Position.Primary);
			secondaryAxisSettingsX1.setDecimalFormat(ColorFormatSupport.decimalFormatInteger);
			secondaryAxisSettingsX1.setColor(ColorFormatSupport.COLOR_BLACK);
			chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX1);
		} catch(Exception e) {
			System.out.println(e);
		}
		//
		ISecondaryAxisSettings secondaryAxisSettingsX2 = new SecondaryAxisSettings("Minutes", new MillisecondsToMinuteConverter());
		secondaryAxisSettingsX2.setPosition(Position.Primary);
		secondaryAxisSettingsX2.setDecimalFormat(ColorFormatSupport.decimalFormatFixed);
		secondaryAxisSettingsX2.setColor(ColorFormatSupport.COLOR_BLACK);
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX2);
		//
		ISecondaryAxisSettings secondaryAxisSettingsY1 = new SecondaryAxisSettings("Relative Intensity [%]", new RelativeIntensityConverter());
		secondaryAxisSettingsY1.setPosition(Position.Secondary);
		secondaryAxisSettingsY1.setDecimalFormat(ColorFormatSupport.decimalFormatFixed);
		secondaryAxisSettingsY1.setColor(ColorFormatSupport.COLOR_BLACK);
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY1);
		//
		applySettings(chartSettings);
		/*
		 * Create series.
		 */
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		ISeriesData seriesData = SeriesConverter.getSeriesXY(SeriesConverter.LINE_SERIES_1);
		//
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setEnableArea(true);
		lineSeriesDataList.add(lineSeriesData);
		/*
		 * Set series.
		 */
		addSeriesData(lineSeriesDataList);
	}
}
