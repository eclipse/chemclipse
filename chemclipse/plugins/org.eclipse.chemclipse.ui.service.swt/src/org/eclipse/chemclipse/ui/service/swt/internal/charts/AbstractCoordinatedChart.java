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

import org.eclipse.chemclipse.ui.service.swt.exceptions.SeriesException;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Direction;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;

public abstract class AbstractCoordinatedChart extends AbstractHandledChart implements IDataCoordinates {

	private boolean useZeroY;
	private boolean useZeroX;
	private double length;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	public AbstractCoordinatedChart(Composite parent, int style) {
		super(parent, style);
		/*
		 * Min/Max values will be set dynamically via Math.min and Math.max.
		 * Using the default double value 0 could lead to errors when using
		 * Math.min(...), hence initialize the values with the lowest/highest value.
		 */
		length = 0;
		minX = Double.MAX_VALUE;
		maxX = Double.MIN_VALUE;
		minY = Double.MAX_VALUE;
		maxY = Double.MIN_VALUE;
	}

	@Override
	public boolean isUseZeroY() {

		return useZeroY;
	}

	@Override
	public void setUseZeroY(boolean useZeroY) {

		this.useZeroY = useZeroY;
	}

	@Override
	public boolean isUseZeroX() {

		return useZeroX;
	}

	@Override
	public void setUseZeroX(boolean useZeroX) {

		this.useZeroX = useZeroX;
	}

	@Override
	public double getLength() {

		return length;
	}

	@Override
	public double getMinX() {

		return minX;
	}

	@Override
	public double getMaxX() {

		return maxX;
	}

	@Override
	public double getMinY() {

		return minY;
	}

	@Override
	public double getMaxY() {

		return maxY;
	}

	@Override
	public ISeries createSeries(SeriesType seriesType, double[] xSeries, double[] ySeries, String id) throws SeriesException {

		if(xSeries.length == ySeries.length) {
			ISeriesSet seriesSet = getSeriesSet();
			ISeries series = seriesSet.createSeries(seriesType, id);
			series.setXSeries(xSeries);
			series.setYSeries(ySeries);
			//
			double seriesMinX = Arrays.stream(series.getXSeries()).min().getAsDouble();
			double seriesMaxX = Arrays.stream(series.getXSeries()).max().getAsDouble();
			double seriesMinY = Arrays.stream(series.getYSeries()).min().getAsDouble();
			double seriesMaxY = Arrays.stream(series.getYSeries()).max().getAsDouble();
			//
			length = Math.max(length, xSeries.length);
			minX = Math.min(minX, seriesMinX);
			maxX = Math.max(maxX, seriesMaxX);
			minY = Math.min(minY, seriesMinY);
			maxY = Math.max(maxY, seriesMaxY);
			//
			return series;
		} else {
			throw new SeriesException("The length of x and y series differs.");
		}
	}

	@Override
	public void adjustMinMaxRange(IAxis axis) {

		if(axis != null) {
			Range range = axis.getRange();
			if(axis.getDirection().equals(Direction.X)) {
				/*
				 * X-AXIS
				 */
				if(useZeroX) {
					range.lower = (range.lower < 0) ? 0 : range.lower;
				} else {
					range.lower = (range.lower < minX) ? minX : range.lower;
				}
				range.upper = (range.upper > maxX) ? maxX : range.upper;
			} else {
				/*
				 * Y-AXIS
				 */
				if(useZeroY) {
					range.lower = (range.lower < 0) ? 0 : range.lower;
				} else {
					range.lower = (range.lower < minY) ? minY : range.lower;
				}
				range.upper = (range.upper > maxY) ? maxY : range.upper;
			}
			/*
			 * Adjust the range.
			 */
			axis.setRange(range);
		}
	}
}
