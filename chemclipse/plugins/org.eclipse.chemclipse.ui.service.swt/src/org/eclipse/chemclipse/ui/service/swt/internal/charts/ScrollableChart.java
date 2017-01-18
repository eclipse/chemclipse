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

import org.eclipse.chemclipse.ui.service.swt.core.AbstractChartSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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

public class ScrollableChart extends Composite implements Listener, PaintListener, IEventHandler {

	private Slider sliderVertical;
	private Slider sliderHorizontal;
	private BaseChart baseChart;

	public ScrollableChart(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void applySettings(AbstractChartSettings settings) {

		sliderVertical.setVisible(settings.isVerticalSliderVisible());
		sliderHorizontal.setVisible(settings.isHorizontalSliderVisible());
		//
		baseChart.setOrientation(settings.getOrientation());
		baseChart.getLegend().setVisible(settings.isLegendVisible());
		baseChart.getTitle().setVisible(settings.isTitleVisible());
		baseChart.setBackground(settings.getBackground());
		baseChart.setBackgroundInPlotArea(settings.getBackgroundInPlotArea());
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

		baseChart.handleEvent(event);
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

	}

	@Override
	public void handleMouseDownEvent(Event event) {

	}

	@Override
	public void handleMouseUpEvent(Event event) {

		IAxis xAxis = baseChart.getAxisSet().getXAxis(0);
		if(xAxis != null) {
			int selection = (int)(xAxis.getRange().lower + ((xAxis.getRange().upper - xAxis.getRange().lower) / 2));
			if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
				sliderHorizontal.setSelection(selection);
			} else {
				sliderVertical.setSelection(selection);
			}
		}
		IAxis yAxis = baseChart.getAxisSet().getYAxis(0);
		if(yAxis != null) {
			int selection = (int)(yAxis.getRange().lower + ((yAxis.getRange().upper - yAxis.getRange().lower) / 2));
			if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
				sliderVertical.setSelection(selection);
			} else {
				sliderHorizontal.setSelection(selection);
			}
		}
	}

	@Override
	public void handleMouseWheel(Event event) {

	}

	@Override
	public void handleMouseDoubleClick(Event event) {

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

	@Override
	public void paintControl(PaintEvent e) {

	}

	private void resetSlider() {

		if((baseChart.getOrientation() == SWT.HORIZONTAL)) {
			/*
			 * Horizontal
			 */
			sliderVertical.setMinimum((int)baseChart.getMinY());
			sliderVertical.setMaximum((int)baseChart.getMaxY());
			sliderVertical.setSelection((int)(baseChart.getMaxY() - baseChart.getMinY()));
			//
			sliderHorizontal.setMinimum((int)baseChart.getMinX());
			sliderHorizontal.setMaximum((int)baseChart.getMaxX());
			sliderHorizontal.setSelection((int)(baseChart.getMaxX() - baseChart.getMinX()));
		} else {
			/*
			 * Vertical
			 */
			sliderVertical.setMinimum((int)baseChart.getMinX());
			sliderVertical.setMaximum((int)baseChart.getMaxX());
			sliderVertical.setSelection((int)(baseChart.getMaxX() - baseChart.getMinX()));
			//
			sliderHorizontal.setMinimum((int)baseChart.getMinY());
			sliderHorizontal.setMaximum((int)baseChart.getMaxY());
			sliderHorizontal.setSelection((int)(baseChart.getMaxY() - baseChart.getMinY()));
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
		sliderHorizontal.setVisible(true);
	}
}