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
package org.eclipse.chemclipse.ui.service.swt.internal.charts;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Direction;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;

public class BaseChart extends Chart implements PaintListener, IEventHandler {

	private static final long DELTA_CLICK_TIME = 100;
	/*
	 * Prevent accidental zooming.
	 * At least 30% of the chart width or height needs to be selected.
	 */
	private static final int MIN_SELECTION_PERCENTAGE = 30;
	//
	private UserSelection userSelection;
	private long clickTime;
	//
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	public BaseChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@Override
	public void handleEvent(Event event) {

		super.handleEvent(event);
		switch(event.type) {
			case SWT.KeyDown:
				handleKeyDownEvent(event);
				break;
			case SWT.KeyUp:
				handleKeyUpEvent(event);
				break;
			case SWT.MouseMove:
				handleMouseMoveEvent(event);
				break;
			case SWT.MouseDown:
				handleMouseDownEvent(event);
				break;
			case SWT.MouseUp:
				handleMouseUpEvent(event);
				break;
			case SWT.MouseWheel:
				handleMouseWheel(event);
				break;
			case SWT.MouseDoubleClick:
				handleMouseDoubleClick(event);
				break;
			case SWT.Selection:
				handleSelectionEvent(event);
				break;
			default:
				break;
		}
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(userSelection.isActive()) {
			int xMin = Math.min(userSelection.getStartX(), userSelection.getStopX());
			int xMax = Math.max(userSelection.getStartX(), userSelection.getStopX());
			int yMin = Math.min(userSelection.getStartY(), userSelection.getStopY());
			int yMax = Math.max(userSelection.getStartY(), userSelection.getStopY());
			e.gc.drawRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		}
	}

	/**
	 * SeriesType.LINE or SeriesType.BAR
	 * 
	 * @param seriesType
	 * @param xSeries
	 * @param ySeries
	 * @param id
	 * @return
	 */
	public ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id) {

		ISeriesSet seriesSet = getSeriesSet();
		ISeries series = seriesSet.createSeries(seriesType, id);
		series.setXSeries(xSeries);
		series.setYSeries(ySeries);
		//
		double minX = Arrays.stream(series.getXSeries()).min().getAsDouble();
		double maxX = Arrays.stream(series.getXSeries()).max().getAsDouble();
		double minY = Arrays.stream(series.getYSeries()).min().getAsDouble();
		double maxY = Arrays.stream(series.getXSeries()).max().getAsDouble();
		//
		this.minX = (this.minX < minX) ? this.minX : minX;
		this.maxX = (this.maxX > maxX) ? this.maxX : maxX;
		this.minY = (this.minY < minY) ? this.minY : minY;
		this.maxY = (this.maxY > maxY) ? this.maxY : maxY;
		//
		return series;
	}

	/**
	 * Adjusts the range and redraws the plot.
	 */
	public void adjustRange() {

		getAxisSet().adjustRange();
		adjustMinRange(getAxisSet().getXAxis(0));
		adjustMinRange(getAxisSet().getYAxis(0));
		redraw();
	}

	public double getMinX() {

		return minX;
	}

	public double getMaxX() {

		return maxX;
	}

	public double getMinY() {

		return minY;
	}

	public double getMaxY() {

		return maxY;
	}

	@Override
	public void handleMouseMoveEvent(Event event) {

		userSelection.setStopCoordinate(event.x, event.y);
		redraw();
	}

	@Override
	public void handleMouseDownEvent(Event event) {

		if(event.button == 1) {
			userSelection.setStartCoordinate(event.x, event.y);
			clickTime = System.currentTimeMillis();
		}
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		if(event.button == 1) {
			long deltaTime = System.currentTimeMillis() - clickTime;
			if(deltaTime >= DELTA_CLICK_TIME) {
				//
				int minSelectedWidth;
				int deltaWidth;
				//
				if((getOrientation() == SWT.HORIZONTAL)) {
					minSelectedWidth = getPlotArea().getBounds().width / MIN_SELECTION_PERCENTAGE;
					deltaWidth = Math.abs(userSelection.getStartX() - event.x);
				} else {
					minSelectedWidth = getPlotArea().getBounds().height / MIN_SELECTION_PERCENTAGE;
					deltaWidth = Math.abs(userSelection.getStartY() - event.y);
				}
				//
				if(deltaWidth >= minSelectedWidth) {
					//
					int xStart = userSelection.getStartX();
					int xStop = userSelection.getStopX();
					int yStart = userSelection.getStartY();
					int yStop = userSelection.getStopY();
					//
					if((getOrientation() == SWT.HORIZONTAL)) {
						setRange(xStart, xStop, getAxisSet().getXAxis(0));
						setRange(yStart, yStop, getAxisSet().getYAxis(0));
					} else {
						setRange(xStart, xStop, getAxisSet().getYAxis(0));
						setRange(yStart, yStop, getAxisSet().getXAxis(0));
					}
				}
			}
			userSelection.reset();
			redraw();
		}
	}

	@Override
	public void handleMouseWheel(Event event) {

	}

	@Override
	public void handleMouseDoubleClick(Event event) {

		adjustRange();
	}

	@Override
	public void handleKeyDownEvent(Event event) {

	}

	@Override
	public void handleKeyUpEvent(Event event) {

	}

	@Override
	public void handleSelectionEvent(Event event) {

	}

	private void initialize() {

		userSelection = new UserSelection();
		Composite plotArea = getPlotArea();
		plotArea.addListener(SWT.KeyDown, this);
		plotArea.addListener(SWT.KeyUp, this);
		plotArea.addListener(SWT.MouseMove, this);
		plotArea.addListener(SWT.MouseDown, this);
		plotArea.addListener(SWT.MouseUp, this);
		plotArea.addListener(SWT.MouseWheel, this);
		plotArea.addListener(SWT.MouseDoubleClick, this);
		plotArea.addListener(SWT.Resize, this);
		plotArea.addPaintListener(this);
	}

	private void setRange(int start, int stop, IAxis axis) {

		if(axis != null && Math.abs(stop - start) > 0) {
			double min = axis.getDataCoordinate((start < stop) ? start : stop);
			double max = axis.getDataCoordinate((stop > start) ? stop : start);
			axis.setRange(new Range(min, max));
			adjustMinRange(axis);
		}
	}

	private void adjustMinRange(IAxis axis) {

		if(axis != null) {
			Range range = axis.getRange();
			if(axis.getDirection().equals(Direction.X)) {
				range.lower = (range.lower < minX) ? minX : range.lower;
				range.upper = (range.upper > maxX) ? maxX : range.upper;
			} else {
				range.lower = (range.lower < minY) ? minY : range.lower;
				range.upper = (range.upper > maxY) ? maxY : range.upper;
			}
			axis.setRange(range);
		}
	}
}
