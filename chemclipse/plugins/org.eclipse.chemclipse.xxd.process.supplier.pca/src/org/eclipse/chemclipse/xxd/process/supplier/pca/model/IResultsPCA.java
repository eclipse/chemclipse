/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.IVariable;

public interface IResultsPCA<R extends IResultPCA, V extends IVariable> {

	List<double[]> getLoadingVectors();

	double[] getExplainedVariances();

	List<V> getExtractedVariables();

	List<R> getPcaResultList();

	IAnalysisSettings getPcaSettings();

	void setLoadingVectors(List<double[]> loadingVectors);

	void setExplainedVariances(double[] explainedVariances);

	double[] getCumulativeExplainedVariances();

	void setCumulativeExplainedVariances(double[] cumulativeExplainedVariances);
}