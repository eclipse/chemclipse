/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.AbstractSamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class Samples extends AbstractSamples<IVariable, Sample> implements ISamplesPCA<IVariable, Sample> {

	private IAnalysisSettings analysisSettings = new AnalysisSettings();

	public Samples(List<Sample> samples) {

		super();
		for(Sample sample : samples) {
			getSampleList().add(sample);
		}
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return analysisSettings;
	}

	@Override
	public void setAnalysisSettings(IAnalysisSettings analysisSettings) {

		this.analysisSettings = analysisSettings;
	}
}
