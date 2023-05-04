/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.charts;

import org.eclipse.chemclipse.ux.extension.xxd.ui.support.BaselineSelectionPaintListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IAxisSet;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;

public class ChromatogramRulerChart extends ChromatogramChart {

	private Cursor defaultCursor;
	private BaselineSelectionPaintListener baselineSelectionPaintListener;
	private IRulerUpdateNotifier rulerUpdateNotifier = null;
	//
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;

	public ChromatogramRulerChart(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setRulerUpdateNotifier(IRulerUpdateNotifier rulerUpdateNotifier) {

		this.rulerUpdateNotifier = rulerUpdateNotifier;
	}

	private void createControl() {

		defaultCursor = getBaseChart().getCursor();
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		IPlotArea plotArea = getBaseChart().getPlotArea();
		baselineSelectionPaintListener = new BaselineSelectionPaintListener();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
		setData("org.eclipse.e4.ui.css.CssClassName", "ChromatogramRulerChart");
	}

	@Override
	public void handleMouseDownEvent(Event event) {

		super.handleMouseDownEvent(event);
		if(isControlKeyPressed(event) && event.count == 1) {
			startBaselineSelection(event.x, event.y);
			setCursor(SWT.CURSOR_CROSS);
		}
	}

	@Override
	public void handleMouseMoveEvent(Event event) {

		super.handleMouseMoveEvent(event);
		if(isControlKeyPressed(event)) {
			if(xStart > 0 && yStart > 0) {
				trackBaselineSelection(event.x, event.y);
			}
		}
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		super.handleMouseUpEvent(event);
		if(isControlKeyPressed(event)) {
			stopBaselineSelection(event.x, event.y);
			calculateDimension(event);
			setCursorDefault();
			resetSelectedRange();
		}
	}

	private void calculateDimension(Event event) {

		BaseChart baseChart = getBaseChart();
		IAxisSet axisSet = baseChart.getAxisSet();
		Point rectangle = baseChart.getPlotArea().getSize();
		int width = rectangle.x;
		int height = rectangle.y;
		//
		if(rulerUpdateNotifier != null) {
			if(width != 0) {
				/*
				 * X (Retention Time)
				 */
				double factorWidth = 100.0d / width;
				double percentageStartWidth = (factorWidth * xStart) / 100.0d;
				double percentageStopWidth = (factorWidth * xStop) / 100.0d;
				//
				IAxis retentionTime = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
				Range millisecondsRange = retentionTime.getRange();
				double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
				double startX = millisecondsRange.lower + millisecondsWidth * percentageStartWidth;
				double stopX = millisecondsRange.lower + millisecondsWidth * percentageStopWidth;
				/*
				 * Y (Intensity)
				 */
				double startY = 0.0d;
				double stopY = 0.0d;
				if(height != 0) {
					IAxis intensity = baseChart.getAxisSet().getYAxis(BaseChart.ID_PRIMARY_Y_AXIS);
					Range intensityRange = intensity.getRange();
					double abundanceHeight = intensityRange.upper - intensityRange.lower;
					double factorHeight = 100.0d / height;
					double percentageStartHeight = (100.0d - (factorHeight * yStart)) / 100.0d;
					double percentageStopHeight = (100.0d - (factorHeight * yStop)) / 100.0d;
					//
					startY = (float)(intensityRange.lower + abundanceHeight * percentageStartHeight);
					stopY = (float)(intensityRange.lower + abundanceHeight * percentageStopHeight);
				}
				/*
				 * Fire an update.
				 */
				rulerUpdateNotifier.update(new RulerEvent(startX, stopX, startY, stopY));
			}
		}
	}

	private boolean isControlKeyPressed(Event event) {

		return (event.stateMask & SWT.MOD1) == SWT.MOD1;
	}

	private void startBaselineSelection(int x, int y) {

		xStart = x;
		yStart = y;
		/*
		 * Set the start point.
		 */
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
	}

	private void trackBaselineSelection(int x, int y) {

		xStop = x;
		yStop = y;
		//
		baselineSelectionPaintListener.setX1(xStart);
		baselineSelectionPaintListener.setY1(yStart);
		baselineSelectionPaintListener.setX2(xStop);
		baselineSelectionPaintListener.setY2(yStop);
		//
		redrawChart();
	}

	private void stopBaselineSelection(int x, int y) {

		xStop = x;
		yStop = y;
	}

	private void resetSelectedRange() {

		baselineSelectionPaintListener.reset();
		//
		xStart = 0;
		yStart = 0;
		xStop = 0;
		yStop = 0;
		//
		redrawChart();
	}

	private void setCursor(int cursorId) {

		getBaseChart().setCursor(getBaseChart().getDisplay().getSystemCursor(cursorId));
	}

	private void setCursorDefault() {

		getBaseChart().setCursor(defaultCursor);
	}

	private void redrawChart() {

		getBaseChart().redraw();
	}
}
