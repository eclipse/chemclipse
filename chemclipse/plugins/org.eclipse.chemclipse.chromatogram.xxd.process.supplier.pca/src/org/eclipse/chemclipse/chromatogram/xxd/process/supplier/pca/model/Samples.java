/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.model.statistics.AbstractSamples;
import org.eclipse.chemclipse.model.statistics.RetentionTime;

public class Samples extends AbstractSamples<RetentionTime, Sample> implements ISamplesPCA<RetentionTime, Sample> {

	private IAnalysisSettings analysisSettings = new AnalysisSettings();
	private FilterSettings filterSettings = new FilterSettings();
	private PreprocessingSettings preprocessingSettings = new PreprocessingSettings();

	public Samples(Collection<IDataInputEntry> dataInputEntries) {
		super();
		dataInputEntries.forEach(d -> getSampleList().add(new Sample(d)));
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return analysisSettings;
	}

	@Override
	public void setAnalysisSettings(IAnalysisSettings analysisSettings) {

		this.analysisSettings = analysisSettings;
	}

	@Override
	public FilterSettings getFilterSettings() {

		return filterSettings;
	}

	@Override
	public void setFilterSettings(FilterSettings filterSettings) {

		this.filterSettings = filterSettings;
	}

	@Override
	public PreprocessingSettings getPreprocessingSettings() {

		return preprocessingSettings;
	}

	@Override
	public void setPreprocessingSettings(PreprocessingSettings preprocessingSettings) {

		this.preprocessingSettings = preprocessingSettings;
	}
}
