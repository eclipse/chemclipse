/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.model.statistics.IVariable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PcaResultsVisualization<R extends IPcaResult, V extends IVariableVisualization> implements IPcaResultsVisualization {

	private IPcaResults<IPcaResult, IVariable> delegator;
	private ObservableList<IPcaResultVisualization> pcaResultsVisualization;
	private IPcaVisualization pcaVisualization;
	private ObservableList<IVariableVisualization> variablesExtractedVisalization;

	public PcaResultsVisualization(IPcaResults<IPcaResult, IVariable> modelData, IPcaVisualization pcaVisualization) {

		super();
		this.delegator = modelData;
		this.pcaVisualization = pcaVisualization;
		pcaResultsVisualization = FXCollections.observableArrayList(IPcaResultVisualization.extractor());
		variablesExtractedVisalization = FXCollections.observableArrayList(IVariableVisualization.extractor());
		if(!(modelData == null)) {
			modelData.getPcaResultList().forEach(r -> pcaResultsVisualization.add(new PcaResultVisualization(r)));
			modelData.getExtractedVariables().forEach(v -> {
				if(v instanceof IVariableVisualization) {
					variablesExtractedVisalization.add((IVariableVisualization)v);
				} else {
					variablesExtractedVisalization.add(new VariableVisualization(v));
				}
			});
		}
	}

	@Override
	public List<double[]> getLoadingVectors() {

		return delegator.getLoadingVectors();
	}

	@Override
	public ObservableList<IVariableVisualization> getExtractedVariables() {

		return variablesExtractedVisalization;
	}

	@Override
	public ObservableList<IPcaResultVisualization> getPcaResultList() {

		return pcaResultsVisualization;
	}

	@Override
	public IPcaSettings getPcaSettings() {

		return delegator.getPcaSettings();
	}

	@Override
	public IPcaVisualization getPcaVisualization() {

		return pcaVisualization;
	}

	@Override
	public void setLoadingVectors(List<double[]> loadingVectors) {

		delegator.setLoadingVectors(loadingVectors);
	}

	@Override
	public double[] getExplainedVariances() {

		return delegator.getExplainedVariances();
	}

	@Override
	public void setExplainedVariances(double[] explainedVariances) {

		delegator.setExplainedVariances(explainedVariances);
	}

	@Override
	public double[] getCumulativeExplainedVariances() {

		return delegator.getCumulativeExplainedVariances();
	}

	@Override
	public void setCumulativeExplainedVariances(double[] cumulativeExplainedVariances) {

		delegator.setCumulativeExplainedVariances(cumulativeExplainedVariances);
	}
}
