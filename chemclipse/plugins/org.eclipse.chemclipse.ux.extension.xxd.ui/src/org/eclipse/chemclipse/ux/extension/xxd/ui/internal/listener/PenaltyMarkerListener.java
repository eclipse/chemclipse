/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.listener;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.AbstractBaseChartPaintListener;
import org.eclipse.swtchart.extensions.marker.IBaseChartPaintListener;

public class PenaltyMarkerListener extends AbstractBaseChartPaintListener implements IBaseChartPaintListener {

	private int retentionTime = 0;
	private String labelTop = "";
	private String labelBottom = "";
	private int level = 0;

	public PenaltyMarkerListener(BaseChart baseChart) {

		this(baseChart, 0);
	}

	public PenaltyMarkerListener(BaseChart baseChart, int level) {

		super(baseChart);
		this.level = level;
	}

	public void setLevel(int level) {

		if(level >= 0) {
			this.level = level;
		}
	}

	public void reset() {

		retentionTime = 0;
		labelTop = "";
		labelBottom = "";
	}

	public void setData(int retentionTime, String labelTop, String labelBottom) {

		this.retentionTime = retentionTime;
		this.labelTop = labelTop;
		this.labelBottom = labelBottom;
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(retentionTime > 0) {
			drawMarker(e);
		}
	}

	private void drawMarker(PaintEvent e) {

		BaseChart baseChart = getBaseChart();
		IAxis axisX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
		if(axisX != null) {
			/*
			 * Current range
			 */
			Range range = axisX.getRange();
			double min = range.lower;
			double max = range.upper;
			//
			if(retentionTime >= min && retentionTime <= max) {
				int width = e.width;
				double rangeX = max - min + 1;
				double percentX = 1.0d / rangeX * (retentionTime - min);
				int offsetX = (int)(width * percentX);
				if(offsetX > 0) {
					GC gc = e.gc;
					Color colorBackground = gc.getBackground();
					Color colorForeground = gc.getForeground();
					//
					gc.setForeground(Colors.DARK_GRAY);
					gc.setLineStyle(SWT.LINE_SOLID);
					gc.drawLine(offsetX, 0, offsetX, e.height);
					drawLabel(e, offsetX, labelTop, true);
					drawLabel(e, offsetX, labelBottom, false);
					//
					gc.setBackground(colorBackground);
					gc.setForeground(colorForeground);
				}
			}
		}
	}

	private void drawLabel(PaintEvent e, int offset, String label, boolean top) {

		if(!label.isEmpty()) {
			/*
			 * Text
			 */
			GC gc = e.gc;
			Point labelSize = gc.textExtent(label);
			int textWidth = labelSize.x;
			int textOffsetX = (int)(textWidth / 2.0d);
			int offsetHeight = level * labelSize.y + level * 10;
			int offsetTextX = offset - textOffsetX - 2;
			int widthX = textWidth + 4;
			//
			if(top) {
				gc.setBackground(Colors.DARK_GRAY);
				gc.fillRectangle(offsetTextX, 15 + offsetHeight, widthX, 25);
				gc.setForeground(Colors.WHITE);
				gc.drawText(label, offset - textOffsetX, 19 + offsetHeight, SWT.DRAW_TRANSPARENT);
			} else {
				gc.setBackground(Colors.DARK_GRAY);
				gc.fillRectangle(offsetTextX, e.height - 40 - offsetHeight, widthX, 25);
				gc.setForeground(Colors.WHITE);
				gc.drawText(label, offset - textOffsetX, e.height - 35 - offsetHeight, SWT.DRAW_TRANSPARENT);
			}
		}
	}
}