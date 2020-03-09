/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.custom;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.BaselineSelectionPaintListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.Range;
import org.eclipse.swtchart.extensions.core.BaseChart;

public class ManualPeakDetectorChart extends ChromatogramPeakChart {

	private Cursor defaultCursor;
	//
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;
	//
	private IChromatogram<? extends IPeak> chromatogram;
	private BaselineSelectionPaintListener baselineSelectionPaintListener;
	private IPeakDetectorListener peakDetectorListener = null;
	private boolean manualDetectionEnabled = false;

	public ManualPeakDetectorChart() {
		super();
		init();
	}

	public ManualPeakDetectorChart(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public void addPeakDetectorListener(IPeakDetectorListener peakDetectorListener) {

		this.peakDetectorListener = peakDetectorListener;
	}

	public void removePeakDetectorListener() {

		this.peakDetectorListener = null;
	}

	public boolean isManualDetectionEnabled() {

		return manualDetectionEnabled;
	}

	public void setManualDetectionEnabled(boolean manualDetectionEnabled) {

		this.manualDetectionEnabled = manualDetectionEnabled;
	}

	public void setChromatogram(IChromatogram<? extends IPeak> chromatogram) {

		this.chromatogram = chromatogram;
	}

	@Override
	public void handleMouseDownEvent(Event event) {

		super.handleMouseDownEvent(event);
		if(manualDetectionEnabled) {
			if(isControlKeyPressed(event)) {
				startBaselineSelection(event.x, event.y);
				setCursor(SWT.CURSOR_CROSS);
			}
		}
	}

	@Override
	public void handleMouseMoveEvent(Event event) {

		super.handleMouseMoveEvent(event);
		if(manualDetectionEnabled) {
			if(isControlKeyPressed(event)) {
				if(xStart > 0 && yStart > 0) {
					trackBaselineSelection(event.x, event.y);
				}
			}
		}
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		super.handleMouseUpEvent(event);
		if(manualDetectionEnabled) {
			if(isControlKeyPressed(event)) {
				stopBaselineSelection(event.x, event.y);
				extractPeak();
				setCursorDefault();
				resetSelectedRange();
			}
		}
	}

	private void init() {

		defaultCursor = getBaseChart().getCursor();
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		IPlotArea plotArea = getBaseChart().getPlotArea();
		baselineSelectionPaintListener = new BaselineSelectionPaintListener();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
	}

	private boolean isControlKeyPressed(Event event) {

		return (event.stateMask & SWT.CTRL) == SWT.CTRL;
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

	private void extractPeak() {

		if(chromatogram != null) {
			IPeak peak = extractPeakFromUserSelection(xStart, yStart, xStop, yStop);
			if(peak != null) {
				if(peakDetectorListener != null) {
					peakDetectorListener.update(chromatogram, peak);
				}
			}
		}
	}

	/**
	 * Extracts the selected peak.
	 * 
	 * @param xStop
	 * @param yStop
	 */
	private IPeak extractPeakFromUserSelection(int xStart, int yStart, int xStop, int yStop) {

		/*
		 * Calculate the rectangle factors.
		 * Enable to set the peak as selected with retention time and abundance range.
		 */
		IPeak peak = null;
		if(chromatogram != null) {
			Point rectangle = getBaseChart().getPlotArea().getSize();
			int width = rectangle.x;
			double factorWidth = 0.0d;
			if(width != 0) {
				factorWidth = 100.0d / width;
				double percentageStartWidth = (factorWidth * xStart) / 100.0d;
				double percentageStopWidth = (factorWidth * xStop) / 100.0d;
				/*
				 * Calculate the start and end points.
				 */
				BaseChart baseChart = getBaseChart();
				IAxis retentionTime = baseChart.getAxisSet().getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
				Range millisecondsRange = retentionTime.getRange();
				/*
				 * With abundance and retention time.
				 */
				double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
				/*
				 * Peak start and stop abundances and retention times.
				 */
				int startRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStartWidth);
				int stopRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStopWidth);
				//
				Set<Integer> traces = new HashSet<>();
				boolean includeBackground = true;
				boolean optimizeRange = true;
				peak = PeakDetectorSupport.extractPeakByRetentionTime(chromatogram, startRetentionTime, stopRetentionTime, includeBackground, optimizeRange, traces);
			}
		}
		return peak;
	}
}
