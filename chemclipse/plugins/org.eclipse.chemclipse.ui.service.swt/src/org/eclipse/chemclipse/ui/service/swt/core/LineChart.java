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
package org.eclipse.chemclipse.ui.service.swt.core;

import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.eclipse.chemclipse.ui.service.swt.internal.charts.BaseChart;
import org.eclipse.chemclipse.ui.service.swt.internal.charts.ScrollableChart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Position;
import org.swtchart.IAxisSet;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

public class LineChart extends ScrollableChart {

	private IAxis xAxisPrimary;
	private IAxis yAxisPrimary;
	//
	// private IAxis xAxis1;
	// private IAxis xAxis2;
	// private IAxis yAxis1;

	public LineChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void addSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Suspend the update when adding new data to improve the performance.
		 */
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
				lineSeries.enableArea(lineSeriesSettings.isEnableArea());
				lineSeries.setSymbolType(lineSeriesSettings.getSymbolType());
				lineSeries.setSymbolSize(lineSeriesSettings.getSymbolSize());
				lineSeries.setLineColor(lineSeriesSettings.getLineColor());
				lineSeries.setLineWidth(lineSeriesSettings.getLineWidth());
				lineSeries.enableStack(lineSeriesSettings.isEnableStack());
				lineSeries.enableStep(lineSeriesSettings.isEnableStep());
			} catch(SeriesException e) {
				//
			}
		}
		baseChart.suspendUpdate(false);
		adjustRange();
		baseChart.redraw();
	}

	private void initialize() {

		BaseChart baseChart = getBaseChart();
		baseChart.suspendUpdate(true);
		IAxisSet axisSet = baseChart.getAxisSet();
		//
		xAxisPrimary = axisSet.getXAxis(0);
		xAxisPrimary.getTitle().setText("Retention Time (milliseconds)");
		xAxisPrimary.setPosition(Position.Primary);
		xAxisPrimary.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0##"));
		//
		yAxisPrimary = axisSet.getYAxis(0);
		yAxisPrimary.getTitle().setText("Intensity");
		yAxisPrimary.setPosition(Position.Primary);
		yAxisPrimary.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0#E0"));
		yAxisPrimary.enableLogScale(false); // TODO
		yAxisPrimary.enableCategory(false);
		// TODO
		//
		// int idxAxis1 = axisSet.createXAxis();
		// xAxis1 = axisSet.getXAxis(idxAxis1);
		// xAxis1.getTitle().setText("TOP");
		// xAxis1.setPosition(Position.Secondary);
		//
		// int idxAxis2 = axisSet.createXAxis();
		// xAxis2 = axisSet.getXAxis(idxAxis2);
		// xAxis2.getTitle().setText("BOTTOM2");
		// xAxis2.setPosition(Position.Secondary);
		//
		// int idyAxis1 = axisSet.createYAxis();
		// yAxis1 = axisSet.getYAxis(idyAxis1);
		// yAxis1.getTitle().setText("RIGHT");
		// yAxis1.setPosition(Position.Secondary);
		// yAxis1.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0##"));
		//
		setColors();
		setVisibility();
		baseChart.suspendUpdate(false);
	}

	private void setColors() {

		setAxisColor(xAxisPrimary, Colors.BLACK);
		setAxisColor(yAxisPrimary, Colors.BLACK);
		// setAxisColor(xAxis1, Colors.BLACK);
		// setAxisColor(xAxis2, Colors.BLACK);
		// setAxisColor(yAxis1, Colors.BLACK);
	}

	private void setVisibility() {

		BaseChart baseChart = getBaseChart();
		baseChart.getTitle().setForeground(getBackground());
		baseChart.getTitle().setText("");
		baseChart.getTitle().setVisible(false);
		//
		setAxisVisibility(xAxisPrimary, true);
		setAxisVisibility(yAxisPrimary, true);
		// setAxisVisibility(xAxis1, true);
		// setAxisVisibility(xAxis2, true);
		// setAxisVisibility(yAxis1, true);
	}

	private void setAxisColor(IAxis axis, Color color) {

		if(axis != null && color != null) {
			axis.getTitle().setForeground(color);
			axis.getTick().setForeground(color);
		}
	}

	private void setAxisVisibility(IAxis axis, boolean visible) {

		if(axis != null) {
			axis.getTitle().setVisible(visible);
			axis.getTick().setVisible(visible);
		}
	}
}
