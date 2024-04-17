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
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class PearsonDistance implements DistanceMeasure {

	private static final long serialVersionUID = -376787276211043721L;

	@Override
	public double compute(double[] a, double[] b) {

		PearsonsCorrelation correlation = new PearsonsCorrelation();
		double pearsonCorrelation = correlation.correlation(a, b);
		return 1 - pearsonCorrelation; // Pearson distance D1
	}
}
