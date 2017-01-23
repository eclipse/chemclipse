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

import org.eclipse.chemclipse.ui.service.swt.core.IChartSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.swtchart.IAxis;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.Range;

public class ScrollableChart extends Composite implements IEventHandler {

	private Slider sliderVertical;
	private Slider sliderHorizontal;
	private BaseChart baseChart;

	public ScrollableChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void applySettings(IChartSettings chartSettings) {

		sliderVertical.setVisible(chartSettings.isVerticalSliderVisible());
		sliderHorizontal.setVisible(chartSettings.isHorizontalSliderVisible());
		//
		baseChart.setOrientation(chartSettings.getOrientation());
		baseChart.getLegend().setVisible(chartSettings.isLegendVisible());
		baseChart.getTitle().setVisible(chartSettings.isTitleVisible());
		baseChart.setBackground(chartSettings.getBackground());
		baseChart.setBackgroundInPlotArea(chartSettings.getBackgroundInPlotArea());
		baseChart.enableCompress(chartSettings.isEnableCompress());
		baseChart.setUseZeroX(chartSettings.isUseZeroX());
		baseChart.setUseZeroY(chartSettings.isUseZeroY());
	}

	public BaseChart getBaseChart() {

		return baseChart;
	}

	public ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id) {

		ISeries series = baseChart.createSeries(seriesType, xSeries, ySeries, id);
		resetSlider();
		return series;
	}

	public void adjustRange() {

		baseChart.adjustRange();
		resetSlider();
	}

	@Override
	public void handleEvent(Event event) {

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
	public void handleMouseMoveEvent(Event event) {

		baseChart.handleMouseMoveEvent(event);
	}

	@Override
	public void handleMouseDownEvent(Event event) {

		baseChart.handleMouseDownEvent(event);
	}

	@Override
	public void handleMouseUpEvent(Event event) {

		baseChart.handleMouseUpEvent(event);
		//
		IAxis xAxis = baseChart.getAxisSet().getXAxis(0);
		if(xAxis != null) {
			int selectionX = (int)(xAxis.getRange().upper - xAxis.getRange().lower);
			if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
				sliderHorizontal.setSelection(selectionX);
				sliderHorizontal.setThumb(selectionX);
			} else {
				sliderVertical.setSelection(selectionX);
				sliderVertical.setSelection(selectionX);
			}
		}
		IAxis yAxis = baseChart.getAxisSet().getYAxis(0);
		if(yAxis != null) {
			int selectionY = (int)(yAxis.getRange().upper - yAxis.getRange().lower);
			if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
				sliderVertical.setSelection(selectionY);
				sliderVertical.setSelection(selectionY);
			} else {
				sliderHorizontal.setSelection(selectionY);
				sliderHorizontal.setThumb(selectionY);
			}
		}
	}

	@Override
	public void handleMouseWheel(Event event) {

		baseChart.handleMouseWheel(event);
	}

	@Override
	public void handleMouseDoubleClick(Event event) {

		baseChart.handleMouseDoubleClick(event);
	}

	@Override
	public void handleKeyDownEvent(Event event) {

		baseChart.handleKeyDownEvent(event);
	}

	@Override
	public void handleKeyUpEvent(Event event) {

		baseChart.handleKeyUpEvent(event);
	}

	@Override
	public void handleSelectionEvent(Event event) {

		baseChart.handleSelectionEvent(event);
	}

	@Override
	public void paintControl(PaintEvent e) {

		baseChart.paintControl(e);
	}

	private void resetSlider() {

		if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
			/*
			 * Horizontal
			 */
			int selectionY = (int)(baseChart.getMaxY() - baseChart.getMinY());
			int incrementY = (int)(selectionY / baseChart.getLengthY());
			incrementY = (incrementY < 1) ? 1 : incrementY;
			sliderVertical.setMinimum((int)baseChart.getMinY());
			sliderVertical.setMaximum((int)baseChart.getMaxY());
			sliderVertical.setIncrement(incrementY);
			sliderVertical.setSelection(selectionY);
			sliderVertical.setThumb(selectionY);
			//
			int selectionX = (int)(baseChart.getMaxX() - baseChart.getMinX());
			int incrementX = (int)(selectionX / baseChart.getLengthX());
			incrementX = (incrementX < 1) ? 1 : incrementX;
			sliderHorizontal.setMinimum((int)baseChart.getMinX());
			sliderHorizontal.setMaximum((int)baseChart.getMaxX());
			sliderHorizontal.setPageIncrement(incrementX);
			sliderHorizontal.setSelection(selectionX);
			sliderHorizontal.setThumb(selectionX);
		} else {
			/*
			 * Vertical
			 */
			int selectionY = (int)(baseChart.getMaxX() - baseChart.getMinX());
			int incrementY = (int)(selectionY / baseChart.getLengthY());
			incrementY = (incrementY < 1) ? 1 : incrementY;
			sliderVertical.setMinimum((int)baseChart.getMinX());
			sliderVertical.setMaximum((int)baseChart.getMaxX());
			sliderVertical.setPageIncrement(incrementY);
			sliderVertical.setSelection(selectionY);
			sliderVertical.setThumb(selectionY);
			//
			int selectionX = (int)(baseChart.getMaxY() - baseChart.getMinY());
			int incrementX = (int)(selectionX / baseChart.getLengthX());
			incrementX = (incrementX < 1) ? 1 : incrementX;
			sliderHorizontal.setMinimum((int)baseChart.getMinY());
			sliderHorizontal.setMaximum((int)baseChart.getMaxY());
			sliderHorizontal.setPageIncrement(incrementX);
			sliderHorizontal.setSelection(selectionX);
			sliderHorizontal.setThumb(selectionX);
		}
	}

	private void initialize() {

		this.setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Slider Vertical
		 */
		sliderVertical = new Slider(composite, SWT.VERTICAL);
		sliderVertical.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		sliderVertical.setVisible(true);
		/*
		 * Chart
		 */
		baseChart = new BaseChart(composite, SWT.NONE);
		baseChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite plotArea = baseChart.getPlotArea();
		plotArea.addListener(SWT.KeyDown, this);
		plotArea.addListener(SWT.KeyUp, this);
		plotArea.addListener(SWT.MouseMove, this);
		plotArea.addListener(SWT.MouseDown, this);
		plotArea.addListener(SWT.MouseUp, this);
		plotArea.addListener(SWT.MouseWheel, this);
		plotArea.addListener(SWT.MouseDoubleClick, this);
		plotArea.addListener(SWT.Resize, this);
		plotArea.addPaintListener(this);
		/*
		 * Slider Horizontal
		 */
		sliderHorizontal = new Slider(composite, SWT.HORIZONTAL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		sliderHorizontal.setLayoutData(gridData);
		sliderHorizontal.setVisible(true);
		sliderHorizontal.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				IAxis xAxis = baseChart.getAxisSet().getXAxis(0);
				IAxis yAxis = baseChart.getAxisSet().getYAxis(0);
				if(xAxis != null) {
					double min = sliderHorizontal.getSelection();
					double max = sliderHorizontal.getSelection() + (xAxis.getRange().upper - xAxis.getRange().lower);
					Range range = new Range(min, max);
					if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
						xAxis.setRange(range);
						baseChart.adjustMinMaxRange(xAxis);
						baseChart.redraw();
					} else {
						yAxis.setRange(range);
						baseChart.adjustMinMaxRange(xAxis);
						baseChart.redraw();
					}
				}
			}
		});
	}
}