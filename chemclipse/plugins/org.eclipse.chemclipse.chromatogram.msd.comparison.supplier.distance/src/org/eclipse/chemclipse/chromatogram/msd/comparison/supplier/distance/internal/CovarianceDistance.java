/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.internal;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.stat.correlation.Covariance;

public class CovarianceDistance implements DistanceMeasure {

	private static final long serialVersionUID = -7925412928959772827L;

	@Override
	public double compute(double[] x, double[] y) {

		if(x.length != y.length || x.length == 0) {
			throw new IllegalArgumentException("Arrays must be non-empty and of the same length");
		}
		Covariance covariance = new Covariance();
		double covXY = covariance.covariance(x, y);
		double covXX = covariance.covariance(x, x);
		if(covXX == 0) {
			throw new ArithmeticException("Covariance of x with itself is zero, cannot divide by zero.");
		}
		double normalizedCovariance = covXY / covXX;
		return 1 - normalizedCovariance;
	}
}
