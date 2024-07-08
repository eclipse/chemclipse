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
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.AbstractBaseChartPaintListener;
import org.eclipse.swtchart.extensions.marker.IBaseChartPaintListener;
import org.eclipse.swtchart.extensions.model.ElementLine;
import org.eclipse.swtchart.extensions.model.ElementRectangle;
import org.eclipse.swtchart.extensions.model.ICustomSeries;
import org.eclipse.swtchart.extensions.model.IElement;
import org.eclipse.swtchart.extensions.model.TextElement;
import org.eclipse.swtchart.extensions.support.ElementSupport;
import org.eclipse.swtchart.extensions.support.PointPrimary;
import org.eclipse.swtchart.extensions.support.RectanglePrimary;

public class RetentionIndexMarker extends AbstractBaseChartPaintListener implements IBaseChartPaintListener {

	private boolean showIdentifier = false;
	private boolean showLabelTop = true;
	private List<PositionMarker> positionMarkers = new ArrayList<>();
	private ICustomSeries customSeries = null;

	public RetentionIndexMarker(BaseChart baseChart) {

		this(baseChart, "Retention Index Marker", "Currently used retention indices.");
	}

	public RetentionIndexMarker(BaseChart baseChart, String label, String description) {

		super(baseChart);
		this.customSeries = baseChart.createCustomSeries(label, description);
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

		customSeries.clear();
		if(isDraw()) {
			BaseChart baseChart = getBaseChart();
			IAxis axisX = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			IAxis axisY = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
			if(axisX != null && axisY != null) {
				ElementSupport elementSupport = new ElementSupport(axisX.getRange(), axisY.getRange(), e.width, e.height);
				for(PositionMarker positionMarker : positionMarkers) {
					if(usePositionMarker(positionMarker)) {
						drawMarker(e, positionMarker, elementSupport, showLabelTop);
					}
				}
			}
		}
	}

	private boolean usePositionMarker(PositionMarker positionMarker) {

		return positionMarker.getRetentionTime() > 0;
	}

	private void drawMarker(PaintEvent e, PositionMarker positionMarker, ElementSupport elementSupport, boolean top) {

		int retentionTime = positionMarker.getRetentionTime();
		int retentionIndex = positionMarker.getRetentionIndex();
		/*
		 * Current range
		 */
		Range rangeX = elementSupport.getRangeX();
		double min = rangeX.lower;
		double max = rangeX.upper;
		//
		if(retentionTime >= min && retentionTime <= max) {
			int width = e.width;
			double widthX = max - min + 1;
			double percent = 1.0d / widthX * (retentionTime - min);
			int offset = (int)(width * percent);
			if(offset > 0) {
				/*
				 * Settings
				 */
				GC gc = e.gc;
				Color colorBackground = gc.getBackground();
				Color colorForeground = gc.getForeground();
				//
				if(retentionIndex > 0) {
					//
					gc.setForeground(Colors.DARK_GRAY);
					gc.setLineStyle(SWT.LINE_DASHDOT);
					gc.drawLine(offset, 0, offset, e.height);
					//
					String label = showIdentifier ? positionMarker.getIdentifier() : Integer.toString(retentionIndex);
					Point labelSize = gc.textExtent(label);
					//
					int offsetX = offset - 20;
					int offsetY;
					int boxWidth = 40;
					int boxHeight = 25;
					int labelX = offset - (int)(labelSize.x / 2.0d);
					int labelY;
					//
					if(top) {
						gc.setBackground(Colors.DARK_GRAY);
						offsetY = 15;
						gc.fillRectangle(offsetX, offsetY, boxWidth, boxHeight);
						gc.setForeground(Colors.WHITE);
						labelY = 19;
						gc.drawText(label, labelX, labelY, SWT.DRAW_TRANSPARENT);
					} else {
						gc.setBackground(Colors.DARK_GRAY);
						offsetY = e.height - 40;
						gc.fillRectangle(offsetX, offsetY, boxWidth, boxHeight);
						gc.setForeground(Colors.WHITE);
						labelY = e.height - 35;
						gc.drawText(label, labelX, labelY, SWT.DRAW_TRANSPARENT);
					}
					/*
					 * Custom Series
					 */
					ElementRectangle elementRectangle = new ElementRectangle();
					RectanglePrimary rectanglePrimary = elementSupport.convertRectangle(offsetX, offsetY, boxWidth, boxHeight);
					elementRectangle.setX(rectanglePrimary.getX());
					elementRectangle.setY(rectanglePrimary.getY());
					elementRectangle.setWidth(rectanglePrimary.getWidth());
					elementRectangle.setHeight(rectanglePrimary.getHeight() / 4); // TODO - fine tune scaling
					elementRectangle.setColor(Colors.DARK_GRAY);
					elementRectangle.setAlpha(255);
					customSeries.getGraphicElements().add(elementRectangle);
					//
					ElementLine elementLine = new ElementLine();
					elementLine.setX(retentionTime);
					elementLine.setY(IElement.POSITION_TOP_Y);
					elementLine.setX2(retentionTime);
					elementLine.setY2(IElement.POSITION_BOTTOM_Y);
					elementLine.setColor(Colors.DARK_GRAY);
					elementLine.setAlpha(255);
					elementLine.setLineStyle(LineStyle.DASHDOT);
					elementLine.setLineWidth(2);
					customSeries.getGraphicElements().add(elementLine);
					//
					TextElement textElement = new TextElement();
					textElement.setLabel(label);
					textElement.setColor(Colors.WHITE);
					PointPrimary pointLabel = elementSupport.convertPoint(labelX, labelY);
					textElement.setX(pointLabel.getX());
					textElement.setY(pointLabel.getY() - rectanglePrimary.getHeight() / 16); // TODO - fine tune scaling
					textElement.setRotation(0);
					customSeries.getTextElements().add(textElement);
				}
				//
				gc.setBackground(colorBackground);
				gc.setForeground(colorForeground);
			}
		}
	}
}