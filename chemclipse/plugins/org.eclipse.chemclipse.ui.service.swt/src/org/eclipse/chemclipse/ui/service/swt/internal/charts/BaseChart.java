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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.IAxis;
import org.swtchart.Range;

public class BaseChart extends AbstractCoordinatedChart {

	/*
	 * Prevent accidental zooming.
	 * At least 30% of the chart width or height needs to be selected.
	 */
	private static final int MIN_SELECTION_PERCENTAGE = 30;
	private static final long DELTA_CLICK_TIME = 100;
	//
	private UserSelection userSelection;
	private long clickStartTime;

	public BaseChart(Composite parent, int style) {
		super(parent, style);
		userSelection = new UserSelection();
	}

	@Override
	public void paintControl(PaintEvent e) {

		if(userSelection.isActive()) {
			/*
			 * Draw the rectangle of the user selection.
			 */
			int xMin = Math.min(userSelection.getStartX(), userSelection.getStopX());
			int xMax = Math.max(userSelection.getStartX(), userSelection.getStopX());
			int yMin = Math.min(userSelection.getStartY(), userSelection.getStopY());
			int yMax = Math.max(userSelection.getStartY(), userSelection.getStopY());
			e.gc.drawRectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		}
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
			clickStartTime = System.currentTimeMillis();
		}
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		if(event.button == 1) {
			long deltaTime = System.currentTimeMillis() - clickStartTime;
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
	public void handleMouseDoubleClick(Event event) {

		adjustRange();
		redraw();
	}

	/**
	 * Adjusts the range of all axes and validates the min/max ranges.
	 */
	public void adjustRange() {

		getAxisSet().adjustRange();
		adjustMinMaxRange(getAxisSet().getXAxis(0));
		adjustMinMaxRange(getAxisSet().getYAxis(0));
	}

	/**
	 * Sets and adjusts the range.
	 * 
	 * @param start
	 * @param stop
	 * @param axis
	 */
	private void setRange(int start, int stop, IAxis axis) {

		if(axis != null && Math.abs(stop - start) > 0) {
			double min = axis.getDataCoordinate(Math.min(start, stop));
			double max = axis.getDataCoordinate(Math.max(start, stop));
			axis.setRange(new Range(min, max));
			adjustMinMaxRange(axis);
		}
	}
}
