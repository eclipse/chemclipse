/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class RunnablePCA implements IRunnableWithProgress {

	private IExtractionData extractionData;
	private IAnalysisSettings analysisSettings;
	private FilterSettings filterSettings;
	private PreprocessingSettings preprocessingSettings;
	//
	private Samples samples = null;

	public RunnablePCA(IExtractionData extractionData, IAnalysisSettings analysisSettings, FilterSettings filterSettings, PreprocessingSettings preprocessingSettings) {
		this.extractionData = extractionData;
		this.analysisSettings = analysisSettings;
		this.filterSettings = filterSettings;
		this.preprocessingSettings = preprocessingSettings;
	}

	public Samples getSamples() {

		return samples;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * Extract the samples and process the data
		 */
		samples = extractionData.process(monitor);
		preprocessingSettings.process(samples, monitor);
		filterSettings.process(samples, monitor);
		/*
		 * Store the settings
		 */
		samples.setAnalysisSettings(analysisSettings);
		samples.setPreprocessingSettings(preprocessingSettings);
		samples.setFilterSettings(filterSettings);
	}
}
