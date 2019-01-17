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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PcaResultsVisualization<R extends IPcaResult, V extends IVaribleExtracted> implements IPcaResultsVisualization {

	private IPcaResults<R, V> delegator;
	private ObservableList<IPcaResultVisualization> pcaResultsVisualization;
	private IPcaSettingsVisualization pcaSettingsVisualization;
	private ObservableList<IVariableExtractedVisalization> variablesExtractedVisalization;

	public PcaResultsVisualization(IPcaResults<R, V> modelData, IPcaSettingsVisualization pcaSettingsVisualization) {

		super();
		this.delegator = modelData;
		this.pcaSettingsVisualization = pcaSettingsVisualization;
		pcaResultsVisualization = FXCollections.observableArrayList(IPcaResultVisualization.extractor());
		variablesExtractedVisalization = FXCollections.observableArrayList(IVariableExtractedVisalization.extractor());
		if(!(modelData == null)) {
			modelData.getPcaResultList().forEach(r -> pcaResultsVisualization.add(new PcaResultVisualization(r)));
			modelData.getExtractedVariables().forEach(v -> variablesExtractedVisalization.add(new VariableExtractedVisualization(v)));
		}
	}

	@Override
	public List<double[]> getLoadingVectors() {

		return delegator.getLoadingVectors();
	}

	@Override
	public ObservableList<IVariableExtractedVisalization> getExtractedVariables() {

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
	public IPcaSettingsVisualization getPcaSettingsVisualization() {

		return pcaSettingsVisualization;
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
