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
package org.eclipse.chemclipse.numeric.statistics.model;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class UnivariateStatistics implements IUnivariateStatistics {

	final private int sampleSize;
	final private double[] abundances;
	final private Mean mean;
	final private Variance variance;
	final private StandardDeviation sd;
	final private Percentile percentile;

	public UnivariateStatistics(int sampleSize, double[] abundances, Mean mean, Variance variance, StandardDeviation sd, Percentile percentile) {
		this.sampleSize = sampleSize;
		this.abundances = abundances;
		this.mean = mean;
		this.variance = variance;
		this.sd = sd;
		this.percentile = percentile;
	}

	public UnivariateStatistics(double[] abundances) {
		this(abundances.length, abundances, new Mean(), new Variance(), new StandardDeviation(), new Percentile());
		mean.setData(abundances);
		variance.setData(abundances);
		sd.setData(abundances);
		percentile.setData(abundances);
	}

	@Override
	public int getSampleSize() {

		return sampleSize;
	}

	@Override
	public double getMean() {

		return mean.evaluate();
	}

	@Override
	public double getVariance() {

		return variance.evaluate();
	}

	@Override
	public double getStandardDeviation() {

		return sd.evaluate();
	}

	@Override
	public double[] getValues() {

		return abundances;
	}

	@Override
	public double getMedian() {

		percentile.setQuantile(50.0d);
		return percentile.evaluate();
	}

	@Override
	public double getRelativeStandardDeviation() {

		double std = sd.evaluate();
		double m = mean.evaluate();
		return std / m;
	}
}
