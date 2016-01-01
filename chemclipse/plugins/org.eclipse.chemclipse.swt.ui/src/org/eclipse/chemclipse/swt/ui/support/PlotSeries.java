/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.numeric.statistics.Calculations;

// TODO Junit
/**
 * ChromatogramSeries contains the x and y data of a swtchart compatible
 * converted chromatogram.
 * 
 * @author eselmeister
 */
public class PlotSeries {

	private double[] xSeries = {0};
	private double[] ySeries = {0};
	private double xMin = 0;
	private double xMax = 0;
	private double yMin = 0;
	private double yMax = 0;

	/**
	 * Constructs the object.<br/>
	 * xSeries - retention time<br/>
	 * ySeries - abundance
	 * 
	 * @param xSeries
	 * @param ySeries
	 */
	public PlotSeries(double[] xSeries, double[] ySeries) {
		if(xSeries != null && ySeries != null) {
			this.xSeries = xSeries;
			this.ySeries = ySeries;
			/*
			 * The min values are 0.
			 */
			this.xMax = Calculations.getMax(xSeries);
			this.yMax = Calculations.getMax(ySeries);
		}
	}

	/**
	 * Returns the minimum retention time (milliseconds) value.
	 * 
	 * @return double
	 */
	public double getXMin() {

		return xMin;
	}

	/**
	 * Returns the maximum retention time (milliseconds) value.
	 * 
	 * @return double
	 */
	public double getXMax() {

		return xMax;
	}

	/**
	 * Returns the minimum abundance value.
	 * 
	 * @return double
	 */
	public double getYMin() {

		return yMin;
	}

	/**
	 * Returns the maximum abundance value.
	 * 
	 * @return double
	 */
	public double getYMax() {

		return yMax;
	}

	/**
	 * Returns the x series (retention time).
	 * 
	 * @return double[]
	 */
	public double[] getXSeries() {

		return xSeries;
	}

	/**
	 * Returns the y series (abundance).
	 * 
	 * @return double[]
	 */
	public double[] getYSeries() {

		return ySeries;
	}

	// TODO anpassen für Massenspektrum
	/**
	 * Returns the start retention time in minutes.
	 * 
	 * @return double
	 */
	public double getStartRetentionTimeInMinutes() {

		double result;
		if(xSeries == null || xSeries.length < 1) {
			result = 0.0d;
		} else {
			result = xSeries[0] / IChromatogram.MINUTE_CORRELATION_FACTOR;
		}
		return result;
	}

	// TODO anpassen für Massenspektrum
	/**
	 * Returns the stop retention time in minutes.
	 * 
	 * @return double
	 */
	public double getStopRetentionTimeInMinutes() {

		double result;
		if(xSeries == null || xSeries.length < 1) {
			result = 0.0d;
		} else {
			result = xSeries[xSeries.length - 1] / IChromatogram.MINUTE_CORRELATION_FACTOR;
		}
		return result;
	}
}
