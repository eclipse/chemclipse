/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swtchart.IAxis;
import org.eclipse.swtchart.Range;

/**
 * This class offers some utility methods to draw charts.
 * 
 * @author eselmeister
 */
public class ChartUtil {

	public static final double MIN_RELATIVE_ABUNDANCE = 0.0d;
	public static final double MAX_RELATIVE_ABUNDANCE = 1.0d;

	/*
	 * Use only static methods.
	 */
	private ChartUtil() {
	}

	/**
	 * Returns the actual signal as a relative value to max signal.<br/>
	 * The relative abundance scale is given by: MIN_RELATIVE_ABUNDANCE and
	 * MAX_RELATIVE_ABUNDANCE.
	 * 
	 * @param maxSignal
	 * @param actualSignal
	 * @return double
	 */
	public static double getRelativeAbundance(double maxSignal, double actualSignal) {

		double result = MIN_RELATIVE_ABUNDANCE;
		/*
		 * Do not divide through zero.
		 */
		if(maxSignal != 0) {
			result = (MAX_RELATIVE_ABUNDANCE / maxSignal) * actualSignal;
		}
		return result;
	}

	/**
	 * Sets the axis title and tick color.
	 * 
	 * @param axis
	 * @param color
	 */
	public static void setAxisColor(IAxis axis, Color color) {

		if(axis != null && color != null) {
			axis.getTitle().setForeground(color);
			axis.getTick().setForeground(color);
		}
	}

	/**
	 * Validates the range of e.g. the millisecond and abundance axis and sets
	 * it.
	 * 
	 * @param axis
	 * @param min
	 * @param max
	 */
	public synchronized static void checkAndSetRange(IAxis axis, double min, double max) {

		if(axis != null) {
			Range range = axis.getRange();
			if(range.lower < min || range.lower >= max) {
				range.lower = min;
			}
			if(range.upper <= min || range.upper > max) {
				range.upper = max;
			}
			if(range.lower >= range.upper) {
				double tmp = range.upper;
				range.upper = range.lower;
				range.lower = tmp;
			}
			axis.setRange(range);
		}
	}

	/**
	 * Set the range for the given axis, e.g. minutes, relative abundance.
	 * 
	 * @param axis
	 * @param min
	 * @param max
	 */
	public static void setRange(IAxis axis, double min, double max) {

		if(axis != null) {
			Range range = axis.getRange();
			range.lower = min;
			range.upper = max;
			if(range.lower >= range.upper) {
				double tmp = range.upper;
				range.upper = range.lower;
				range.lower = tmp;
			}
			axis.setRange(range);
		}
	}
}
