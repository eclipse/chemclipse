/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;

import org.eclipse.chemclipse.model.statistics.ISample;

public interface IMultivariateCalculator {

	void addObservation(double[] obsData, ISample sampleKey, String groupName);

	double[] applyLoadings(double[] obs);

	void compute();

	boolean getComputeStatus();

	void setComputeSuccess();

	boolean isPcaValid();

	void invalidatePca();

	double getErrorMetric(double[] obs);

	double[] getLoadingVector(int var);

	double[] getScoreVector(ISample sampleId);

	ArrayList<String> getGroupNames();

	void initialize(int numObs, int numVars, int numComps);

	double getSummedVariance();

	double getExplainedVariance(int var);

	void setNumComps(int numComps);

	int getNumComps();
}
