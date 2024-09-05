/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.marker.AbstractBaseChartPaintListener;
import org.eclipse.swtchart.extensions.marker.IBaseChartPaintListener;

public class TimeRangeMarker extends AbstractBaseChartPaintListener implements IBaseChartPaintListener {

	private static final int INVISIBLE = -1;
	//
	private Set<TimeRange> timeRanges = new HashSet<>();
	private TimeRange timeRangeSelection = null;
	private TimeRange timeRangeHover = null;
	private int hoverPositionX = -1;
	private Transform transform = new Transform(Display.getDefault());

	public TimeRangeMarker(BaseChart baseChart) {

		super(baseChart);
		transform.rotate(-90);
	}

	public Set<TimeRange> getTimeRanges() {

		return timeRanges;
	}

	public void setTimeRangeSelection(TimeRange timeRange) {

		this.timeRangeSelection = timeRange;
	}

	public TimeRange getTimeRangeSelection() {

		return timeRangeSelection;
	}

	public void clearTimeRangeHover() {

		setTimeRangeHover(null, -1);
	}

	public void setTimeRangeHover(TimeRange timeRange, int hoverPositionX) {

		this.timeRangeHover = timeRange;
		this.hoverPositionX = hoverPositionX;
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(!getBaseChart().isBufferActive()) {
			GC gc = e.gc;
			//
			TimeRange timeRangeLocked = TimeRangeSupport.getTimeRangeLocked(timeRanges);
			if(timeRangeLocked != null) {
				plotMarker(gc, timeRangeLocked);
			} else {
				for(TimeRange timeRange : timeRanges) {
					plotMarker(gc, timeRange);
				}
			}
			//
			gc.setAlpha(255);
		}
	}

	@Override
	protected void finalize() throws Throwable {

		transform.dispose();
	}

	private void plotMarker(GC gc, TimeRange timeRange) {

		BaseChart baseChart = getBaseChart();
		if(baseChart.getSeriesSet().getSeries().length > 0) {
			IAxis xAxis = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			IAxis yAxis = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			//
			if(xAxis != null && yAxis != null) {
				/*
				 * Settings
				 */
				boolean isSelected = timeRange.equals(timeRangeSelection);
				boolean isHovered = timeRange.equals(timeRangeHover);
				//
				Range rangeX = xAxis.getRange();
				IPlotArea plotArea = baseChart.getPlotArea();
				Point rectangle = plotArea instanceof Scrollable scrollable ? scrollable.getSize() : plotArea.getSize();
				Color colorArea = isSelected ? (timeRange.isLocked() ? Colors.RED : Colors.DARK_RED) : Colors.GRAY;
				Color colorLabel = isSelected ? (timeRange.isLocked() ? Colors.DARK_RED : Colors.BLACK) : Colors.DARK_GRAY;
				/*
				 * Print lines, rectangle and label
				 */
				int xStart = printLine(gc, rectangle, rangeX, timeRange.getStart(), colorArea, 1);
				int xCenter = printLine(gc, rectangle, rangeX, timeRange.getCenter(), colorArea, 1);
				int xStop = printLine(gc, rectangle, rangeX, timeRange.getStop(), colorArea, 1);
				fillRectangle(gc, rectangle, xStart, xStop, colorArea);
				printLabel(gc, rectangle, timeRange, xStop, colorLabel);
				/*
				 * Hover the left or right part of the closest time range.
				 */
				if(isHovered) {
					if(hoverPositionX >= 0) {
						IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
						int offset = preferenceStore.getInt(PreferenceSupplier.P_TIME_RANGE_SELECTION_OFFSET);
						if(hoverPositionX >= (timeRange.getStart() - offset) && hoverPositionX <= (timeRange.getStop() + offset)) {
							int start;
							int stop;
							if(hoverPositionX < timeRangeHover.getCenter()) {
								start = xStart;
								stop = xCenter;
							} else {
								start = xCenter;
								stop = xStop;
							}
							Color color = timeRange.isLocked() ? Colors.DARK_RED : (isSelected ? Colors.DARK_GRAY : Colors.GRAY);
							fillRectangle(gc, rectangle, start, stop, color);
						}
					}
				}
			}
		}
	}

	private int printLine(GC gc, Point rectangle, Range rangeX, int position, Color color, int lineWidth) {

		int x = INVISIBLE;
		if(isMarkerInRange(rangeX, position)) {
			x = calculatePositionX(rangeX, rectangle, position);
			gc.setAlpha(255);
			gc.setForeground(color);
			gc.setLineWidth(lineWidth);
			gc.drawLine(x, 0, x, rectangle.y);
		}
		return x;
	}

	private void fillRectangle(GC gc, Point rectangle, int xStart, int xStop, Color color) {

		if(xStart > INVISIBLE || xStop > INVISIBLE) {
			int start = (xStart == INVISIBLE) ? 0 : xStart;
			int stop = (xStop == INVISIBLE) ? rectangle.x : xStop;
			gc.setBackground(color);
			gc.setAlpha(50);
			gc.fillRectangle(start, 0, stop - start, rectangle.y);
		}
	}

	private void printLabel(GC gc, Point rectangle, TimeRange timeRange, int xStop, Color color) {

		if(xStop > INVISIBLE) {
			//
			String label = timeRange.getIdentifier();
			if(timeRange.isLocked()) {
				label += " (locked)";
			}
			//
			gc.setTransform(transform);
			Point labelSize = gc.textExtent(label);
			int xLabel = -labelSize.x - (rectangle.y - labelSize.x) + (rectangle.y / 20);
			/*
			 * Use this position to set the label before the start line:
			 * int yLabel = xStart - labelSize.y;
			 */
			int yLabel = xStop;
			gc.setAlpha(255);
			gc.setForeground(color);
			gc.setLineWidth(timeRange.isLocked() ? 2 : 1);
			gc.drawText(label, xLabel, yLabel, true);
			gc.setTransform(null);
		}
	}

	private boolean isMarkerInRange(Range range, int x) {

		return x >= range.lower && x <= range.upper;
	}

	private int calculatePositionX(Range range, Point point, int x) {

		int position = 0;
		double deltaRange = range.upper - range.lower + 1;
		if(deltaRange != 0) {
			double partSize = point.x / deltaRange;
			double deltaX = x - range.lower;
			position = (int)(deltaX * partSize);
		}
		return position;
	}
}