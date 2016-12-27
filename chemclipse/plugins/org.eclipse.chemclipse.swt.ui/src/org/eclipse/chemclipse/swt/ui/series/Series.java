/*******************************************************************************
 * Copyright (c) 2008, 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.series;

import org.eclipse.chemclipse.numeric.statistics.Calculations;

// TODO JUnit
/**
 * Creates a series instance.
 * 
 * @author eselmeister
 */
public class Series implements ISeries {

	private double[] xSeries;
	private double[] ySeries;
	private double xMin = 0;
	private double xMax = 0;
	private double yMin = 0;
	private double yMax = 0;
	String id = "";

	/**
	 * Creates a valid series instance.
	 * 
	 * @param xSeries
	 * @param ySeries
	 * @param id
	 */
	public Series(double[] xSeries, double[] ySeries, String id) {
		if(xSeries != null) {
			this.xSeries = xSeries;
		} else {
			xSeries = new double[]{0};
		}
		if(ySeries != null) {
			this.ySeries = ySeries;
		} else {
			ySeries = new double[]{0};
		}
		if(id != null) {
			this.id = id;
		}
		/*
		 * Set the min and max values. This could be done statically as the user
		 * has no ability to set new x and ySeries.
		 */
		this.xMin = Calculations.getMin(xSeries);
		this.yMin = Calculations.getMin(ySeries);
		this.xMax = Calculations.getMax(xSeries);
		this.yMax = Calculations.getMax(ySeries);
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public double[] getXSeries() {

		return xSeries;
	}

	@Override
	public double[] getYSeries() {

		return ySeries;
	}

	@Override
	public double getXMax() {

		return xMax;
	}

	@Override
	public double getXMin() {

		return xMin;
	}

	@Override
	public double getYMax() {

		return yMax;
	}

	@Override
	public double getYMin() {

		return yMin;
	}

	// -----------------------------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(this == other) {
			return true;
		}
		if(other == null) {
			return false;
		}
		if(getClass() != other.getClass()) {
			return false;
		}
		Series otherSeries = (Series)other;
		return id.equals(otherSeries.getId()) && xSeries.length == otherSeries.getXSeries().length && this.getXMin() == otherSeries.getXMin() && this.getXMax() == otherSeries.getXMax() && ySeries.length == otherSeries.getYSeries().length && this.getYMin() == otherSeries.getYMin() && this.getYMax() == otherSeries.getYMax();
	}

	@Override
	public int hashCode() {

		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("id=" + id);
		builder.append(",");
		builder.append("xMin=" + getXMin());
		builder.append(",");
		builder.append("xMax=" + getXMax());
		builder.append(",");
		builder.append("xSeries.length=" + xSeries.length);
		builder.append(",");
		builder.append("yMin=" + getYMin());
		builder.append(",");
		builder.append("yMax=" + getYMax());
		builder.append(",");
		builder.append("ySeries.length=" + ySeries.length);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------------------------equals, hashCode, toString
}
