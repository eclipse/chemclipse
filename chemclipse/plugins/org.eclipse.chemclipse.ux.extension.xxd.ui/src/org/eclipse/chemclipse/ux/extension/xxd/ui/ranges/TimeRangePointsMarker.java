/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Lorenz Gerber - add draw line for y selection
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.AbstractBaseChartPaintListener;
import org.eclipse.swtchart.extensions.marker.IBaseChartPaintListener;

public class TimeRangePointsMarker extends AbstractBaseChartPaintListener implements IBaseChartPaintListener {

	private List<Point> pointSelection = new ArrayList<>();
	//
	private static final String START = "Start";
	private static final String MAX = "Max";
	private static final String STOP = "Stop";

	public TimeRangePointsMarker(BaseChart baseChart) {

		super(baseChart);
	}

	public void setPointSelection(List<Point> pointSelection) {

		this.pointSelection = pointSelection;
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(!pointSelection.isEmpty()) {
			BaseChart baseChart = getBaseChart();
			IAxis axisX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			if(axisX != null) {
				for(int i = 0; i < pointSelection.size(); i++) {
					/*
					 * It's a manual selection, hence
					 * x should be always > 0.
					 */
					Point point = pointSelection.get(i);
					int x = point.x;
					int y = point.y;
					if(x > 0) {
						GC gc = e.gc;
						Color colorBackground = gc.getBackground();
						Color colorForeground = gc.getForeground();
						//
						gc.setForeground(Colors.DARK_GRAY);
						gc.setLineStyle(SWT.LINE_DASHDOT);
						gc.drawLine(x, 0, x, e.height);
						gc.drawLine(0, y, e.width, y);
						//
						String label = getLabel(i, pointSelection);
						Point labelSize = gc.textExtent(label);
						gc.setBackground(Colors.DARK_GRAY);
						gc.fillRectangle(x - 20, 15, 40, 25);
						gc.setForeground(Colors.WHITE);
						gc.drawText(label, x - (int)(labelSize.x / 2.0d), 19, SWT.DRAW_TRANSPARENT);
						//
						gc.setBackground(colorBackground);
						gc.setForeground(colorForeground);
					}
				}
			}
		}
	}

	private String getLabel(int i, List<Point> pointSelection) {

		String label = "";
		if(!pointSelection.isEmpty()) {
			int last = pointSelection.size() - 1;
			if(i == 0 || last == 0) {
				label = pointSelection.get(0).x <= pointSelection.get(last).x ? START : STOP;
			} else if(i == last) {
				label = pointSelection.get(0).x <= pointSelection.get(last).x ? STOP : START;
			} else {
				label = MAX;
			}
		}
		//
		return label;
	}
}