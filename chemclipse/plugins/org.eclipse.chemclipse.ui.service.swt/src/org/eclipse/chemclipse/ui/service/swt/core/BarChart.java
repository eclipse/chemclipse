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

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ui.service.swt.internal.charts.BaseChart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Position;
import org.swtchart.IAxisSet;

public class BarChart extends BaseChart {

	private IAxis xAxisPrimary;
	private IAxis yAxisPrimary;
	//
	private IAxis xAxis1;
	private IAxis xAxis2;
	private IAxis yAxis1;

	public BarChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {

		enableCompress(true);
		suspendUpdate(true);
		IAxisSet axisSet = getAxisSet();
		getLegend().setVisible(false);
		getTitle().setVisible(false);
		//
		xAxisPrimary = axisSet.getXAxis(0);
		xAxisPrimary.getTitle().setText("BOTTOM");
		xAxisPrimary.setPosition(Position.Primary);
		xAxisPrimary.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0##"));
		//
		yAxisPrimary = axisSet.getYAxis(0);
		yAxisPrimary.getTitle().setText("LEFT");
		yAxisPrimary.setPosition(Position.Primary);
		yAxisPrimary.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0#E0"));
		//
		int idxAxis1 = axisSet.createXAxis();
		xAxis1 = axisSet.getXAxis(idxAxis1);
		xAxis1.getTitle().setText("TOP");
		xAxis1.setPosition(Position.Secondary);
		//
		int idxAxis2 = axisSet.createXAxis();
		xAxis2 = axisSet.getXAxis(idxAxis2);
		xAxis2.getTitle().setText("BOTTOM 2");
		xAxis2.setPosition(Position.Primary);
		//
		int idyAxis1 = axisSet.createYAxis();
		yAxis1 = axisSet.getYAxis(idyAxis1);
		yAxis1.getTitle().setText("RIGHT");
		yAxis1.setPosition(Position.Secondary);
		yAxis1.getTick().setFormat(ValueFormat.getDecimalFormatEnglish("0.0##"));
		//
		setColors();
		setVisibility();
		suspendUpdate(false);
	}

	private void setColors() {

		setBackground(Colors.WHITE);
		setBackgroundInPlotArea(Colors.WHITE);
		//
		setAxisColor(xAxisPrimary, Colors.BLACK);
		setAxisColor(yAxisPrimary, Colors.BLACK);
		setAxisColor(xAxis1, Colors.BLACK);
		setAxisColor(xAxis2, Colors.BLACK);
		setAxisColor(yAxis1, Colors.BLACK);
	}

	private void setVisibility() {

		getTitle().setForeground(getBackground());
		getTitle().setText("");
		getTitle().setVisible(false);
		//
		setAxisVisibility(xAxisPrimary, true);
		setAxisVisibility(yAxisPrimary, true);
		setAxisVisibility(xAxis1, true);
		setAxisVisibility(xAxis2, true);
		setAxisVisibility(yAxis1, true);
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
