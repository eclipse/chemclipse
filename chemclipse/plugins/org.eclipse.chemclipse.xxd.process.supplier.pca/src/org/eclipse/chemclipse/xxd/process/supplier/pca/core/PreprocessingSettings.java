/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
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
package org.eclipse.chemclipse.xxd.process.supplier.pca.core;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.IReplacer;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing.SmallValuesReplacer;
import org.eclipse.core.runtime.IProgressMonitor;

public class PreprocessingSettings implements IPreprocessingSettings {

	/*
	 * Replace must be set.
	 * By default, the small values replace is the most robust choice.
	 */
	private ICentering centering = null;
	private INormalization normalization = null;
	private ITransformation transformation = null;
	private IReplacer replacer = new SmallValuesReplacer();

	public PreprocessingSettings() {

	}

	public PreprocessingSettings(IPreprocessingSettings preprocessingSettings) {

		setCentering(preprocessingSettings.getCentering());
		setNormalization(preprocessingSettings.getNormalization());
		setTransformation(preprocessingSettings.getTransformation());
		setReplacer(preprocessingSettings.getReplacer());
	}

	@Override
	public boolean availableModification() {

		return normalization != null || transformation != null || centering != null;
	}

	@Override
	public ICentering getCentering() {

		return centering;
	}

	@Override
	public void setCentering(ICentering centering) {

		this.centering = centering;
	}

	@Override
	public INormalization getNormalization() {

		return normalization;
	}

	@Override
	public void setNormalization(INormalization normalization) {

		this.normalization = normalization;
	}

	@Override
	public ITransformation getTransformation() {

		return transformation;
	}

	@Override
	public void setTransformation(ITransformation transformation) {

		this.transformation = transformation;
	}

	@Override
	public IReplacer getReplacer() {

		return replacer;
	}

	@Override
	public void setReplacer(IReplacer replacer) {

		if(replacer != null) {
			this.replacer = replacer;
		} else {
			this.replacer = new SmallValuesReplacer();
		}
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples, IProgressMonitor monitor) {

		for(ISample sample : samples.getSamples()) {
			sample.getSampleData().stream().forEach(sampleData -> {
				double data = sampleData.getData();
				sampleData.setModifiedData(data);
			});
		}
		//
		normalize(samples);
		replaceEmptyValues(samples);
		transform(samples);
		centerAndScale(samples);
	}

	private <V extends IVariable, S extends ISample> void normalize(ISamples<V, S> samples) {

		if(normalization != null) {
			normalization.process(samples);
		}
	}

	private <V extends IVariable, S extends ISample> void replaceEmptyValues(ISamples<V, S> samples) {

		if(replacer != null) {
			replacer.process(samples);
		}
	}

	private <V extends IVariable, S extends ISample> void transform(ISamples<V, S> samples) {

		if(transformation != null) {
			transformation.process(samples);
		}
	}

	private <V extends IVariable, S extends ISample> void centerAndScale(ISamples<V, S> samples) {

		if(centering != null) {
			centering.process(samples);
		}
	}
}