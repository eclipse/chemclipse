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
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

import java.util.ArrayList;
import java.util.List;

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

public class RetentionIndexMarker extends AbstractBaseChartPaintListener implements IBaseChartPaintListener {

	private boolean showIdentifier = false;
	private boolean showLabelTop = true;
	private List<PositionMarker> positionMarkers = new ArrayList<>();

	public RetentionIndexMarker(BaseChart baseChart) {

		super(baseChart);
	}

	public boolean isShowIdentifier() {

		return showIdentifier;
	}

	public void setShowIdentifier(boolean showIdentifier) {

		this.showIdentifier = showIdentifier;
	}

	public boolean isShowLabelTop() {

		return showLabelTop;
	}

	public void setShowLabelTop(boolean showLabelTop) {

		this.showLabelTop = showLabelTop;
	}

	public void clear() {

		positionMarkers.clear();
		super.setDraw(false);
	}

	public List<PositionMarker> getPositionMarkers() {

		return positionMarkers;
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(isDraw()) {
			for(PositionMarker positionMarker : positionMarkers) {
				if(usePositionMarker(positionMarker)) {
					drawMarker(e, positionMarker, showLabelTop);
				}
			}
		}
	}

	private boolean usePositionMarker(PositionMarker positionMarker) {

		return positionMarker.getRetentionTime() > 0;
	}

	private void drawMarker(PaintEvent e, PositionMarker positionMarker, boolean top) {

		int retentionTime = positionMarker.getRetentionTime();
		int retentionIndex = positionMarker.getRetentionIndex();
		//
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
				double percent = 1.0d / rangeX * (retentionTime - min);
				int offset = (int)(width * percent);
				if(offset > 0) {
					/*
					 * Settings
					 */
					GC gc = e.gc;
					Color colorBackground = gc.getBackground();
					Color colorForeground = gc.getForeground();
					//
					gc.setForeground(Colors.DARK_GRAY);
					gc.setLineStyle(SWT.LINE_DASHDOT);
					gc.drawLine(offset, 0, offset, e.height);
					//
					if(retentionIndex > 0) {
						String label = showIdentifier ? positionMarker.getIdentifier() : Integer.toString(retentionIndex);
						Point labelSize = gc.textExtent(label);
						if(top) {
							gc.setBackground(Colors.DARK_GRAY);
							gc.fillRectangle(offset - 20, 15, 40, 25);
							gc.setForeground(Colors.WHITE);
							gc.drawText(label, offset - (int)(labelSize.x / 2.0d), 19, SWT.DRAW_TRANSPARENT);
						} else {
							gc.setBackground(Colors.DARK_GRAY);
							gc.fillRectangle(offset - 20, e.height - 40, 40, 25);
							gc.setForeground(Colors.WHITE);
							gc.drawText(label, offset - (int)(labelSize.x / 2.0d), e.height - 35, SWT.DRAW_TRANSPARENT);
						}
					}
					//
					gc.setBackground(colorBackground);
					gc.setForeground(colorForeground);
				}
			}
		}
	}
}