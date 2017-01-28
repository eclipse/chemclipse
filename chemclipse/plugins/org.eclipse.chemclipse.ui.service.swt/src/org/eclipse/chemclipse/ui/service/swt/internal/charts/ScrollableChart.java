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

import org.eclipse.chemclipse.ui.service.swt.charts.IChartSettings;
import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
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

public class ScrollableChart extends Composite implements IScrollableChart, IEventHandler, IExtendedChart {

	private Slider sliderVertical;
	private Slider sliderHorizontal;
	private BaseChart baseChart;

	public ScrollableChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	@Override
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

	@Override
	public BaseChart getBaseChart() {

		return baseChart;
	}

	@Override
	public ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id) throws SeriesException {

		ISeries series = baseChart.createSeries(seriesType, xSeries, ySeries, id);
		resetSlider();
		return series;
	}

	@Override
	public void deleteSeries(String id) {

		baseChart.deleteSeries(id);
		resetSlider();
	}

	@Override
	public void setRange(String axis, double start, double stop) {

		baseChart.setRange(axis, start, stop);
		setSliderSelection(false);
	}

	@Override
	public void adjustRange(boolean adjustMinMax) {

		baseChart.adjustRange(adjustMinMax);
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

	private void initialize() {

		this.setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Slider Vertical
		 */
		sliderVertical = new Slider(composite, SWT.VERTICAL);
		sliderVertical.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		sliderVertical.setOrientation(SWT.RIGHT_TO_LEFT); // See Bug #511257
		sliderVertical.setVisible(true);
		sliderVertical.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				IAxis xAxis = baseChart.getAxisSet().getXAxis(0);
				IAxis yAxis = baseChart.getAxisSet().getYAxis(0);
				//
				if(xAxis != null && yAxis != null) {
					Range range = calculateShiftedRange(yAxis.getRange(), sliderVertical);
					if(isOrientationHorizontal()) {
						yAxis.setRange(range);
						baseChart.adjustMinMaxRange(yAxis);
					} else {
						xAxis.setRange(range);
						baseChart.adjustMinMaxRange(xAxis);
					}
					baseChart.redraw();
				}
			}
		});
		/*
		 * Chart
		 */
		baseChart = new BaseChart(composite, SWT.NONE);
		baseChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		baseChart.addCustomSelectionHandler(new ICustomSelectionHandler() {

			@Override
			public void handleUserSelection(Event event) {

				setSliderSelection(false);
			}
		});
		//
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
		sliderHorizontal.setOrientation(SWT.LEFT_TO_RIGHT);
		sliderHorizontal.setVisible(true);
		sliderHorizontal.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				IAxis xAxis = baseChart.getAxisSet().getXAxis(0);
				IAxis yAxis = baseChart.getAxisSet().getYAxis(0);
				//
				if(xAxis != null && yAxis != null) {
					Range range = calculateShiftedRange(xAxis.getRange(), sliderHorizontal);
					if(isOrientationHorizontal()) {
						xAxis.setRange(range);
						baseChart.adjustMinMaxRange(xAxis);
					} else {
						yAxis.setRange(range);
						baseChart.adjustMinMaxRange(yAxis);
					}
					baseChart.redraw();
				}
			}
		});
	}

	private void resetSlider() {

		setSliderSelection(true);
	}

	private void setSliderSelection(boolean calculateIncrement) {

		IAxis xAxis = baseChart.getAxisSet().getXAxis(0);
		IAxis yAxis = baseChart.getAxisSet().getYAxis(0);
		//
		if(xAxis != null && yAxis != null) {
			/*
			 * Take care of Horizontal or Vertical orientation.
			 */
			int minX = (int)baseChart.getMinX();
			int maxX = (int)baseChart.getMaxX();
			int minY = (int)baseChart.getMinY();
			int maxY = (int)baseChart.getMaxY();
			//
			int minSelectionX = (int)xAxis.getRange().lower;
			int maxSelectionX = (int)xAxis.getRange().upper;
			int thumbSelectionX = (int)(maxSelectionX - minSelectionX);
			//
			int minSelectionY = (int)yAxis.getRange().lower;
			int maxSelectionY = (int)yAxis.getRange().upper;
			int thumbSelectionY = (int)(maxSelectionY - minSelectionY);
			//
			boolean isHorizontal = isOrientationHorizontal();
			//
			sliderVertical.setMinimum((isHorizontal) ? minY : minX);
			sliderVertical.setMaximum((isHorizontal) ? maxY : maxX);
			sliderVertical.setThumb((isHorizontal) ? thumbSelectionY : thumbSelectionX);
			sliderVertical.setSelection((isHorizontal) ? minSelectionY : minSelectionX);
			//
			sliderHorizontal.setMinimum((isHorizontal) ? minX : minY);
			sliderHorizontal.setMaximum((isHorizontal) ? maxX : maxY);
			sliderHorizontal.setThumb((isHorizontal) ? thumbSelectionX : thumbSelectionY);
			sliderHorizontal.setSelection((isHorizontal) ? minSelectionX : minSelectionY);
			/*
			 * Calculate the increment.
			 */
			if(calculateIncrement) {
				int thumbX = maxX - minX;
				int thumbY = maxY - minY;
				int incrementX = calculateIncrement(thumbX, baseChart.getLength());
				int incrementY = calculateIncrement(thumbY, baseChart.getLength());
				sliderVertical.setIncrement((isHorizontal) ? incrementY : incrementX);
				sliderHorizontal.setPageIncrement((isHorizontal) ? incrementX : incrementY);
			}
		}
	}

	private boolean isOrientationHorizontal() {

		return (baseChart.getOrientation() == SWT.HORIZONTAL) ? true : false;
	}

	private int calculateIncrement(double selection, double length) {

		if(length == 0) {
			return 0;
		} else {
			int increment = (int)(selection / length);
			return (increment < 1) ? 1 : increment;
		}
	}

	private Range calculateShiftedRange(Range range, Slider slider) {

		int selection = slider.getSelection();
		double min = selection;
		double max = (range.upper - range.lower) + selection;
		return new Range(min, max);
	}
}