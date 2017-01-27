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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.IAxis;

public class BaseChart extends AbstractCoordinatedChart {

	/*
	 * Prevent accidental zooming.
	 * At least 30% of the chart width or height needs to be selected.
	 */
	private static final int MIN_SELECTION_PERCENTAGE = 30;
	private static final long DELTA_CLICK_TIME = 100;
	//
	private UserSelection userSelection;
	private List<ICustomSelectionHandler> customSelectionHandlers;
	private long clickStartTime;

	public BaseChart(Composite parent, int style) {
		super(parent, style);
		userSelection = new UserSelection();
		customSelectionHandlers = new ArrayList<ICustomSelectionHandler>();
	}

	public boolean addCustomSelectionHandler(ICustomSelectionHandler customSelectionHandler) {

		return customSelectionHandlers.add(customSelectionHandler);
	}

	public boolean removeCustomSelectionHandler(ICustomSelectionHandler customSelectionHandler) {

		return customSelectionHandlers.remove(customSelectionHandler);
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

		if(event.stateMask == SWT.BUTTON1) {
			userSelection.setStopCoordinate(event.x, event.y);
			redraw();
		}
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
				handleUserSelection(event);
			}
		}
	}

	@Override
	public void handleMouseDoubleClick(Event event) {

		adjustRange(true);
		fireUpdateCustomSelectionHandlers(event);
		redraw();
	}

	private void handleUserSelection(Event event) {

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
		/*
		 * Prevent accidential zooming.
		 */
		if(deltaWidth >= minSelectedWidth) {
			//
			int xStart = userSelection.getStartX();
			int xStop = userSelection.getStopX();
			int yStart = userSelection.getStartY();
			int yStop = userSelection.getStopY();
			IAxis xAxis = getAxisSet().getXAxis(0);
			IAxis yAxis = getAxisSet().getYAxis(0);
			//
			if((getOrientation() == SWT.HORIZONTAL)) {
				setRange(xAxis, xStart, xStop, true);
				setRange(yAxis, yStart, yStop, true);
			} else {
				setRange(xAxis, yStart, yStop, true);
				setRange(yAxis, xStart, xStop, true);
			}
			//
			fireUpdateCustomSelectionHandlers(event);
			/*
			 * Reset the current selection and redraw the chart.
			 */
			userSelection.reset();
			redraw();
		}
	}

	private void fireUpdateCustomSelectionHandlers(Event event) {

		/*
		 * Handle the custom user selection handlers.
		 */
		for(ICustomSelectionHandler customSelectionHandler : customSelectionHandlers) {
			try {
				customSelectionHandler.handleUserSelection(event);
			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}
}
