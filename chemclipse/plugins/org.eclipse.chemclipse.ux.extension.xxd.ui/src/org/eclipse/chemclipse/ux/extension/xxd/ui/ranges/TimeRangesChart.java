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
 * Lorenz Gerber - Time Range Label Cycling
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.ranges;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.model.ranges.TimeRanges;
import org.eclipse.chemclipse.ux.extension.xxd.ui.custom.ChromatogramPeakChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.BaselineSelectionPaintListener;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swtchart.extensions.core.ICustomSelectionHandler;

public class TimeRangesChart extends ChromatogramPeakChart {

	private Cursor defaultCursor;
	private BaselineSelectionPaintListener baselineSelectionPaintListener = new BaselineSelectionPaintListener();
	//
	private int xStart;
	private int yStart;
	private int xStop;
	private int yStop;
	//
	private TimeRangeModus timeRangeModus = TimeRangeModus.TEMPLATE;
	private TimeRange timeRange = null;
	private TimeRanges timeRanges = null;
	private TimeRangeLabels timeRangeLabels = new TimeRangeLabels();
	private String timeRangeLabelCycling = null;
	//
	private ITimeRangeUpdateListener timeRangeUpdateListener = null;
	private ITimeRangePeakListener timeRangePeakListener = null;
	private ITimeRangePointsListener timeRangePointsListener = null;
	/*
	 * TimeRangeModus.POINTS
	 */
	private boolean pointSelectionActive = false;
	private List<Point> pointSelection = new ArrayList<>();
	private TimeRangePointsMarker timeRangePointsMarker = new TimeRangePointsMarker(getBaseChart());

	public TimeRangesChart(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public TimeRangeModus getTimeRangeModus() {

		return timeRangeModus;
	}

	public void setTimeRangeModus(TimeRangeModus timeRangeModus) {

		this.timeRangeModus = timeRangeModus;
	}

	public void setTimeRangeLabels(TimeRangeLabels timeRangeLabels) {

		this.timeRangeLabels = timeRangeLabels;
	}

	public void setUpdateListener(ITimeRangeUpdateListener updateListener) {

		this.timeRangeUpdateListener = updateListener;
	}

	public void setUpdateListener(ITimeRangePeakListener updateListener) {

		this.timeRangePeakListener = updateListener;
	}

	public void setUpdateListener(ITimeRangePointsListener updateListener) {

		this.timeRangePointsListener = updateListener;
	}

	public void setInput(TimeRanges timeRanges) {

		this.timeRanges = timeRanges;
	}

	public void select(TimeRange timeRange) {

		this.timeRange = timeRange;
		fireUpdateTimeRange(timeRange);
	}

	public void setTimeRangeLabelCycling(String cycling) {

		this.timeRangeLabelCycling = cycling;
	}

	@Override
	public void handleKeyDownEvent(Event event) {

		super.handleKeyDownEvent(event);
		if(event.keyCode == SWT.MOD1) {
			if(isPointModus()) {
				setCursor(SWT.CURSOR_CROSS);
				pointSelection.clear();
				timeRangePointsMarker.setPointSelection(pointSelection);
				pointSelectionActive = true;
			} else {
				pointSelectionActive = false;
			}
		}
	}

	@Override
	public void handleKeyUpEvent(Event event) {

		super.handleKeyUpEvent(event);
		if(isControlKeyPressed(event)) {
			if(isPointModus()) {
				fireUpdatePoints(pointSelection);
				clearPointModus();
				setCursorDefault();
				resetSelectedRange();
			}
		} else {
			/*
			 * Clear the point modus status.
			 */
			if(isPointModus()) {
				clearPointModus();
			}
			/*
			 * Select the previous or next time range via key code.
			 */
			if(event.keyCode == SWT.ARROW_LEFT) {
				TimeRange timeRangePrevious = TimeRangeSupport.getTimeRangePrevious(timeRanges, timeRange);
				if(timeRangePrevious != null) {
					select(timeRangePrevious);
					timeRange = timeRangePrevious;
				}
			} else if(event.keyCode == SWT.ARROW_RIGHT) {
				TimeRange timeRangeNext = TimeRangeSupport.getTimeRangeNext(timeRanges, timeRange);
				if(timeRangeNext != null) {
					select(timeRangeNext);
					timeRange = timeRangeNext;
				}
			}
		}
	}

	@Override
	public void handleMouseDownEvent(Event event) {

		super.handleMouseDownEvent(event);
		if(isTimeRangeModusActive()) {
			if(isControlKeyPressed(event)) {
				if(!isPointModus()) {
					startBaselineSelection(event.x, event.y);
					setCursor(SWT.CURSOR_CROSS);
				} else {
					resetSelectedRange();
				}
			}
		}
	}

	@Override
	public void handleMouseMoveEvent(Event event) {

		super.handleMouseMoveEvent(event);
		if(isTimeRangeModusActive()) {
			if(isControlKeyPressed(event)) {
				if(!isPointModus()) {
					if(xStart > 0 && yStart > 0) {
						trackBaselineSelection(event.x, event.y);
					}
				}
			}
		}
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		super.handleMouseUpEvent(event);
		if(isTimeRangeModusActive()) {
			if(isControlKeyPressed(event)) {
				if(isPointModus()) {
					if(pointSelectionActive) {
						pointSelection.add(new Point(event.x, event.y));
						timeRangePointsMarker.setPointSelection(pointSelection);
					}
				} else {
					stopBaselineSelection(event.x, event.y);
					if(TimeRangeModus.BASELINE.equals(timeRangeModus)) {
						fireUpdatePeakRange(xStart, yStart, xStop, yStop);
					} else {
						adjustTimeRange(event);
					}
					setCursorDefault();
					resetSelectedRange();
				}
			}
		}
	}

	protected void updateTimeRangeMarker(TimeRange timeRange) {

		this.timeRange = timeRange;
	}

	private void clearPointModus() {

		pointSelectionActive = false;
		pointSelection.clear();
		timeRangePointsMarker.setPointSelection(pointSelection);
	}

	private boolean isPointModus() {

		return TimeRangeModus.POINTS.equals(timeRangeModus);
	}

	private boolean isTimeRangeModusActive() {

		return !TimeRangeModus.NONE.equals(timeRangeModus);
	}

	private void createControl() {

		defaultCursor = getBaseChart().getCursor();
		/*
		 * Add the paint listeners to draw the selected peak range.
		 */
		IPlotArea plotArea = getBaseChart().getPlotArea();
		plotArea.addCustomPaintListener(baselineSelectionPaintListener);
		plotArea.addCustomPaintListener(timeRangePointsMarker);
		//
		getBaseChart().addCustomRangeSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				assignCurrentRangeSelection();
			}
		});
	}

	private void adjustTimeRange(Event event) {

		/*
		 * Prevent an accidental selection.
		 */
		if(Math.abs(xStop - xStart) > 0) {
			/*
			 * Update the existing time range or create a new one.
			 */
			TimeRange timeRange = TimeRangeSelector.selectRange(getBaseChart(), event, xStart, xStop, timeRanges);
			if(timeRange != null) {
				/*
				 * Update
				 */
				updateTimeRange(timeRange);
			} else {
				/*
				 * Add a new TimeRange
				 */
				if(timeRanges != null && !getBaseChart().getSeriesIds().isEmpty()) {
					if(timeRangeLabelCycling != null) {
						int testCounter = 0;
						boolean exists = true;
						while(exists == true) {
							testCounter += 1;
							String testLabel = new StringBuilder("Range (" + timeRangeLabelCycling + ") " + testCounter).toString();
							exists = timeRanges.keySet().stream().anyMatch(x -> x.equals(testLabel));
						}
						timeRangeLabels = new TimeRangeLabels(timeRangeLabelCycling, new StringBuilder("Range (" + timeRangeLabelCycling + ") " + testCounter).toString());
					}
					TimeRangeDialog timeRangeDialog = new TimeRangeDialog(event.display.getActiveShell(), timeRangeLabels, new IInputValidator() {

						@Override
						public String isValid(String newText) {

							if(newText == null || newText.isEmpty() || newText.isBlank()) {
								return timeRangeLabels.getAddError();
							} else if(newText.contains(TimeRangeLabels.DELIMITER)) {
								return timeRangeLabels.getErrorDelimiter();
							} else {
								for(TimeRange timeRangeX : timeRanges.values()) {
									if(timeRangeX.getIdentifier().equals(newText)) {
										return timeRangeLabels.getAddExists();
									}
								}
							}
							return null;
						}
					});
					/*
					 * Add a new time range.
					 */
					if(timeRangeDialog.open() == Window.OK) {
						String identifier = timeRangeDialog.getIdentifier();
						TimeRange timeRangeAdd = new TimeRange(identifier, 0, 0);
						timeRanges.add(timeRangeAdd);
						updateTimeRange(timeRangeAdd);
					}
				}
			}
		}
	}

	private void updateTimeRange(TimeRange timeRange) {

		BaseChart baseChart = getBaseChart();
		IAxisSet axisSet = baseChart.getAxisSet();
		Point rectangle = baseChart.getPlotArea().getSize();
		int width = rectangle.x;
		//
		if(width != 0) {
			/*
			 * Selected Width
			 */
			double factorWidth = 100.0d / width;
			double percentageStartWidth = (factorWidth * xStart) / 100.0d;
			double percentageStopWidth = (factorWidth * xStop) / 100.0d;
			/*
			 * Retention Time
			 */
			IAxis retentionTime = axisSet.getXAxis(BaseChart.ID_PRIMARY_X_AXIS);
			Range millisecondsRange = retentionTime.getRange();
			double millisecondsWidth = millisecondsRange.upper - millisecondsRange.lower;
			int startRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStartWidth);
			int stopRetentionTime = (int)(millisecondsRange.lower + millisecondsWidth * percentageStopWidth);
			/*
			 * Adjust the start and stop time.
			 */
			if(startRetentionTime < 0) {
				startRetentionTime = 0;
			}
			//
			if(stopRetentionTime < startRetentionTime) {
				stopRetentionTime = 0;
			}
			/*
			 * Update the range.
			 */
			timeRange.update(startRetentionTime, stopRetentionTime);
		}
		//
		fireUpdateTimeRange(timeRange);
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

	private void fireUpdateTimeRange(TimeRange timeRange) {

		if(timeRangeUpdateListener != null) {
			timeRangeUpdateListener.update(timeRange);
		}
	}

	private void fireUpdatePeakRange(int xStart, int yStart, int xStop, int yStop) {

		if(timeRangePeakListener != null) {
			timeRangePeakListener.update(xStart, yStart, xStop, yStop);
		}
	}

	private void fireUpdatePoints(List<Point> points) {

		if(timeRangePointsListener != null) {
			timeRangePointsListener.update(points);
		}
	}
}