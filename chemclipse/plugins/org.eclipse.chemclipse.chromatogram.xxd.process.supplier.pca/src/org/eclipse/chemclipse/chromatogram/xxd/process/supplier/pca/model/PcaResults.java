/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.IVariable;

public class PcaResults implements IPcaResults<IPcaResult, IVariable> {

	private List<double[]> loadingVectors;
	private double[] explainedVariances;
	private double[] cumulativeExplainedVariances;
	private List<IVariable> extractedVariables;
	private List<IPcaResult> pcaResultList;
	//
	private IPcaSettings pcaSettings;

	public PcaResults(IPcaSettings pcaSettings) {

		this.pcaSettings = pcaSettings;
		extractedVariables = new ArrayList<>();
		pcaResultList = new ArrayList<>();
	}

	@Override
	public List<double[]> getLoadingVectors() {

		return loadingVectors;
	}

	@Override
	public List<IVariable> getExtractedVariables() {

		return extractedVariables;
	}

	@Override
	public List<IPcaResult> getPcaResultList() {

		return pcaResultList;
	}

	@Override
	public IPcaSettings getPcaSettings() {

		return pcaSettings;
	}

	@Override
	public void setLoadingVectors(List<double[]> loadingVectors) {

		this.loadingVectors = loadingVectors;
	}

	@Override
	public double[] getExplainedVariances() {

		return this.explainedVariances;
	}

	@Override
	public void setExplainedVariances(double[] explainedVariances) {

		this.explainedVariances = explainedVariances;
	}

	@Override
	public double[] getCumulativeExplainedVariances() {

		return this.cumulativeExplainedVariances;
	}

	@Override
	public void setCumulativeExplainedVariances(double[] cumulativeExplainedVariances) {

		this.cumulativeExplainedVariances = cumulativeExplainedVariances;
	}
}
