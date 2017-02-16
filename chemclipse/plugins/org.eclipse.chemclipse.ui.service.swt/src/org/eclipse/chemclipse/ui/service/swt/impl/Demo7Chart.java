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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ui.service.swt.charts.ChartSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.IChartSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.IPrimaryAxisSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.ISecondaryAxisSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.ISeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.SecondaryAxisSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.converter.PassThroughConverter;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.IScatterSeriesData;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.IScatterSeriesSettings;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.ScatterChart;
import org.eclipse.chemclipse.ui.service.swt.charts.scatter.ScatterSeriesData;
import org.eclipse.chemclipse.ui.service.swt.internal.charts.BaseChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.IAxis.Position;
import org.swtchart.ICustomPaintListener;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.IPlotArea;
import org.swtchart.ISeries;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;

public class Demo7Chart extends ScatterChart implements IChart {

	private Color COLOR_BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
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
		 * Chart Settings
		 */
		IChartSettings chartSettings = new ChartSettings();
		chartSettings //
				.setOrientation(SWT.HORIZONTAL) //
				.setHorizontalSliderVisible(true) //
				.setVerticalSliderVisible(true) //
				.setUseZeroX(false) //
				.setUseZeroY(false);
		//
		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0##");
		String titlePC1 = "PC1";
		String titlePC2 = "PC2";
		//
		IPrimaryAxisSettings primaryAxisSettingsX = chartSettings.getPrimaryAxisSettingsX();
		primaryAxisSettingsX.setTitle(titlePC1);
		primaryAxisSettingsX.setDecimalFormat(decimalFormat);
		primaryAxisSettingsX.setColor(Colors.BLACK);
		//
		IPrimaryAxisSettings primaryAxisSettingsY = chartSettings.getPrimaryAxisSettingsY();
		primaryAxisSettingsY.setTitle(titlePC2);
		primaryAxisSettingsY.setDecimalFormat(decimalFormat);
		primaryAxisSettingsY.setColor(Colors.BLACK);
		//
		ISecondaryAxisSettings secondaryAxisSettingsX1 = new SecondaryAxisSettings(titlePC1, new PassThroughConverter());
		secondaryAxisSettingsX1.setTitle("");
		secondaryAxisSettingsX1.setPosition(Position.Secondary);
		secondaryAxisSettingsX1.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsX1.setColor(Colors.BLACK);
		chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX1);
		//
		ISecondaryAxisSettings secondaryAxisSettingsY1 = new SecondaryAxisSettings(titlePC2, new PassThroughConverter());
		secondaryAxisSettingsY1.setTitle("");
		secondaryAxisSettingsY1.setPosition(Position.Secondary);
		secondaryAxisSettingsY1.setDecimalFormat(decimalFormat);
		secondaryAxisSettingsY1.setColor(Colors.BLACK);
		chartSettings.getSecondaryAxisSettingsListY().add(secondaryAxisSettingsY1);
		//
		applySettings(chartSettings);
		/*
		 * Plot a marker at zero.
		 */
		IPlotArea plotArea = (IPlotArea)getBaseChart().getPlotArea();
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				Range xRange = getBaseChart().getAxisSet().getXAxes()[BaseChart.ID_PRIMARY_X_AXIS].getRange();
				Range yRange = getBaseChart().getAxisSet().getYAxes()[BaseChart.ID_PRIMARY_Y_AXIS].getRange();
				/*
				 * Mark the zero lines if possible.
				 * Otherwise draw the marker in half width.
				 */
				if(xRange.lower < 0 && xRange.upper > 0 && yRange.lower < 0 && yRange.upper > 0) {
					Rectangle rectangle = getBaseChart().getPlotArea().getClientArea();
					int width = rectangle.width;
					int height = rectangle.height;
					int xWidth;
					int yHeight;
					/*
					 * Dependent where the zero values are.
					 * xDelta and yDelta can't be zero -> protect from division by zero.
					 */
					double xDelta = xRange.upper - xRange.lower;
					double yDelta = yRange.upper - yRange.lower;
					double xDiff = xRange.lower * -1; // lower is negative
					double yDiff = yRange.upper;
					double xPart = ((100 / xDelta) * xDiff) / 100; // percent -> 0.0 - 1.0
					double yPart = ((100 / yDelta) * yDiff) / 100; // percent -> 0.0 - 1.0
					xWidth = (int)(width * xPart);
					yHeight = (int)(height * yPart);
					/*
					 * Draw the line.
					 */
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawLine(xWidth, 0, xWidth, height); // Vertical line through zero
					e.gc.drawLine(0, yHeight, width, yHeight); // Horizontal line through zero
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
		/*
		 * Plot the series name above the entry.
		 */
		plotArea.addCustomPaintListener(new ICustomPaintListener() {

			@Override
			public void paintControl(PaintEvent e) {

				ISeriesSet seriesSet = getBaseChart().getSeriesSet();
				ISeries[] series = seriesSet.getSeries();
				for(ISeries serie : series) {
					String label = serie.getId();
					Point point = serie.getPixelCoordinates(0);
					/*
					 * Draw the label
					 */
					Point labelSize = e.gc.textExtent(label);
					e.gc.setForeground(COLOR_BLACK);
					e.gc.drawText(label, (int)(point.x - labelSize.x / 2.0d), (int)(point.y - labelSize.y - SYMBOL_SIZE / 2.0d), true);
				}
			}

			@Override
			public boolean drawBehindSeries() {

				return false;
			}
		});
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
