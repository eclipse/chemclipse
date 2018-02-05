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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SamplesVisualization extends AbstractSamplesVisualization<RetentionTimeVisualization, SampleVisualization> {

	private ObservableList<RetentionTimeVisualization> retentionsTimeVisualization;
	private ObservableList<SampleVisualization> samplesVisualization;

	public SamplesVisualization(Samples samples) {
		super(samples);
		samplesVisualization = FXCollections.observableArrayList(ISampleVisualization.extractor());
		retentionsTimeVisualization = FXCollections.observableArrayList(IVariableVisualization.extractor());
		samples.getSampleList().forEach(s -> samplesVisualization.add(new SampleVisualization(s)));
		samples.getVariables().forEach(r -> retentionsTimeVisualization.add(new RetentionTimeVisualization(r)));
	}

	@Override
	public ObservableList<SampleVisualization> getSampleList() {

		return FXCollections.unmodifiableObservableList(samplesVisualization);
	}

	@Override
	public ObservableList<RetentionTimeVisualization> getVariables() {

		return FXCollections.unmodifiableObservableList(retentionsTimeVisualization);
	}
}
