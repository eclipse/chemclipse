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
package org.eclipse.chemclipse.ui.service.swt.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Position;
import org.swtchart.IAxisSet;

public class BaseChart extends AbstractExtendedChart implements IChartDataCoordinates, IRangeSupport, IExtendedChart {

	public static final int ID_PRIMARY_X_AXIS = 0;
	public static final int ID_PRIMARY_Y_AXIS = 0;
	public static final String DEFAULT_TITLE_X_AXIS = "X-Axis";
	public static final String DEFAULT_TITLE_Y_AXIS = "Y-Axis";
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
		/*
		 * Create the default x and y axis.
		 */
		IAxisSet axisSet = getAxisSet();
		//
		IAxis xAxisPrimary = axisSet.getXAxis(ID_PRIMARY_X_AXIS);
		xAxisPrimary.getTitle().setText(DEFAULT_TITLE_X_AXIS);
		xAxisPrimary.setPosition(Position.Primary);
		xAxisPrimary.getTick().setFormat(new DecimalFormat());
		xAxisPrimary.enableLogScale(false);
		xAxisPrimary.enableCategory(false);
		xAxisPrimary.enableCategory(false);
		xAxisPrimary.setCategorySeries(new String[]{});
		//
		IAxis yAxisPrimary = axisSet.getYAxis(ID_PRIMARY_Y_AXIS);
		yAxisPrimary.getTitle().setText(DEFAULT_TITLE_Y_AXIS);
		yAxisPrimary.setPosition(Position.Primary);
		yAxisPrimary.getTick().setFormat(new DecimalFormat());
		yAxisPrimary.enableLogScale(false);
		yAxisPrimary.enableCategory(false);
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
		 * Prevent accidental zooming.
		 */
		if(deltaWidth >= minSelectedWidth) {
			//
			int xStart = userSelection.getStartX();
			int xStop = userSelection.getStopX();
			int yStart = userSelection.getStartY();
			int yStop = userSelection.getStopY();
			IAxis xAxis = getAxisSet().getXAxis(ID_PRIMARY_X_AXIS);
			IAxis yAxis = getAxisSet().getYAxis(ID_PRIMARY_Y_AXIS);
			//
			if((getOrientation() == SWT.HORIZONTAL)) {
				setRange(xAxis, xStart, xStop, true);
				setRange(yAxis, yStart, yStop, true);
			} else {
				setRange(xAxis, yStart, yStop, true);
				setRange(yAxis, xStart, xStop, true);
			}
			/*
			 * Inform all registered handlers.
			 * Reset the current selection and redraw the chart.
			 */
			fireUpdateCustomSelectionHandlers(event);
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
