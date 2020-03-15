/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDefaultPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IFilterVariables;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SamplesVisualization extends AbstractSamplesVisualization<RetentionTimeVisualization, SampleVisualization> implements IDataPreprocessing, IFilterVariables, IDefaultPcaSettings {

	private ObservableList<RetentionTimeVisualization> retentionsTimeVisualization;
	private Samples samples;
	private ObservableList<SampleVisualization> samplesVisualization;

	public SamplesVisualization(Samples samples) {
		super(samples);
		this.samples = samples;
		samplesVisualization = FXCollections.observableArrayList(ISampleVisualization.extractor());
		retentionsTimeVisualization = FXCollections.observableArrayList(IVariableVisualization.extractor());
		samples.getSampleList().forEach(s -> samplesVisualization.add(new SampleVisualization(s)));
		samples.getVariables().forEach(r -> retentionsTimeVisualization.add(new RetentionTimeVisualization(r)));
	}

	@Override
	public FilterSettings getFilterSettings() {

		return samples.getFilterSettings();
	}

	@Override
	public PreprocessingSettings getPreprocessingSettings() {

		return samples.getPreprocessingSettings();
	}

	@Override
	public ObservableList<SampleVisualization> getSampleList() {

		return samplesVisualization;
	}

	@Override
	public ObservableList<RetentionTimeVisualization> getVariables() {

		return retentionsTimeVisualization;
	}

	@Override
	public IAnalysisSettings getDefaultPcaSettings() {

		return samples.getAnalysisSettings();
	}
}
