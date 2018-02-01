/*******************************************************************************
 * Copyright (c) 2018 lgerber.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * lgerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public interface IPcaCalculator {

	void addObservation(double[] obsData);

	void compute(int numComps);

	double getErrorMetric(double[] obs);

	double[] getLoadingVector(int var);

	double[] getScoreVector(int obs);

	void initialize(int numObs, int numVars);
}
