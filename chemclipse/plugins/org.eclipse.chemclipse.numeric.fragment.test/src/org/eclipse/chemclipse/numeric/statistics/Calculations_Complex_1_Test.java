/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics;

import junit.framework.TestCase;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.eclipse.chemclipse.numeric.statistics.model.UnivariateStatistics;

public class Calculations_Complex_1_Test extends TestCase {

	private double[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new double[13];
		values[0] = 735.0d;
		values[1] = 504.0d;
		values[2] = 561.0d;
		values[3] = 504.0d;
		values[4] = 400.0d;
		values[5] = 420.0d;
		values[6] = 501.0d;
		values[7] = 443.0d;
		values[8] = 430.0d;
		values[9] = 337.0d;
		values[10] = 345.0d;
		values[11] = 304.0d;
		values[12] = 381.0d;
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testCalculations_Complex_1() {

		Mean mean = new Mean();
		mean.setData(values);
		Variance variance = new Variance();
		variance.setData(values);
		StandardDeviation standardDeviation = new StandardDeviation();
		standardDeviation.setData(values);
		Percentile percentile = new Percentile();
		percentile.setData(values);
		/*
		 * Set up the statistics object;
		 */
		UnivariateStatistics statistics = new UnivariateStatistics(values.length, values, mean, variance, standardDeviation, percentile);
		assertEquals("getMean", 451.1538d, statistics.getMean(), 1E-4d);
		assertEquals("getVariance", 12973.47d, statistics.getVariance(), 1E-2d);
		assertEquals("getStandardDeviation", 113.9012d, statistics.getStandardDeviation(), 1E-3d);
		assertEquals("getMedian", 430d, statistics.getMedian());
		assertEquals("getRelativeStandardDeviation", 0.2524663d, statistics.getRelativeStandardDeviation(), 1E-7d);
	}
}