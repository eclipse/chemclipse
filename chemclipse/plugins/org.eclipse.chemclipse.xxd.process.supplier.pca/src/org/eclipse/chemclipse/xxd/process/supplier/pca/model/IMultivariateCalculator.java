/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 * Philip Wenig - color refactorings
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.statistics.ISample;

public interface IMultivariateCalculator {

	void addObservation(double[] obsData, ISample sampleKey, String groupName);

	void compute();

	boolean getComputeStatus();

	void setComputeSuccess();

	double getErrorMetric(double[] obs);

	double[] getLoadingVector(int var);

	double[] getScoreVector(ISample sampleId);

	double getSummedVariance();

	double getExplainedVariance(int var);
}