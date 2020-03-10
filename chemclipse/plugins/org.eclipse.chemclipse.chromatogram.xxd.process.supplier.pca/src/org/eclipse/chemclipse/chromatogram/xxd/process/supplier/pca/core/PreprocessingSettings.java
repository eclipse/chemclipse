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
 * Dr. Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IReplacer;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.MeanValuesReplacer;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;

public class PreprocessingSettings implements IDataModification {

	private ICentering centering = null;
	private INormalization normalization = null;
	private ITransformation transformation = null;
	/*
	 * Replace must be set.
	 */
	private IReplacer replacer = new MeanValuesReplacer();
	//
	private boolean onlySelected = false;
	private boolean removeUselessVariables = true;
	private boolean modifyOnlySelectedVariable = false;

	@Override
	public boolean availableModification() {

		return normalization != null || transformation != null || centering != null;
	}

	public ICentering getCentering() {

		return centering;
	}

	public void setCentering(ICentering centering) {

		this.centering = centering;
	}

	public INormalization getNormalization() {

		return normalization;
	}

	public void setNormalization(INormalization normalization) {

		this.normalization = normalization;
	}

	public ITransformation getTransformation() {

		return transformation;
	}

	public void setTransformation(ITransformation transformation) {

		this.transformation = transformation;
	}

	public IReplacer getReplacer() {

		return replacer;
	}

	public void setReplacer(IReplacer replacer) {

		this.replacer = replacer;
	}

	public boolean isModifyOnlySelectedVariable() {

		return modifyOnlySelectedVariable;
	}

	public void setModifyOnlySelectedVariable(boolean modifyOnlySelectedVariable) {

		this.modifyOnlySelectedVariable = modifyOnlySelectedVariable;
	}

	public boolean isOnlySelected() {

		return onlySelected;
	}

	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	public boolean isRemoveUselessVariables() {

		return removeUselessVariables;
	}

	public void setRemoveUselessVariables(boolean removeUselessVariables) {

		this.removeUselessVariables = removeUselessVariables;
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples, IProgressMonitor monitor) {

		for(ISample sample : samples.getSampleList()) {
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
			normalization.setOnlySelected(onlySelected);
			normalization.setRemoveUselessVariables(removeUselessVariables);
			normalization.setModifyOnlySelectedVariable(modifyOnlySelectedVariable);
			normalization.process(samples);
		}
	}

	private <V extends IVariable, S extends ISample> void replaceEmptyValues(ISamples<V, S> samples) {

		if(replacer != null) {
			replacer.setOnlySelected(onlySelected);
			replacer.setRemoveUselessVariables(removeUselessVariables);
			replacer.setModifyOnlySelectedVariable(modifyOnlySelectedVariable);
			replacer.process(samples);
		}
	}

	private <V extends IVariable, S extends ISample> void transform(ISamples<V, S> samples) {

		if(transformation != null) {
			transformation.setOnlySelected(onlySelected);
			transformation.setRemoveUselessVariables(removeUselessVariables);
			transformation.setModifyOnlySelectedVariable(modifyOnlySelectedVariable);
			transformation.process(samples);
		}
	}

	private <V extends IVariable, S extends ISample> void centerAndScale(ISamples<V, S> samples) {

		if(centering != null) {
			centering.setOnlySelected(onlySelected);
			centering.setRemoveUselessVariables(removeUselessVariables);
			centering.setModifyOnlySelectedVariable(modifyOnlySelectedVariable);
			centering.process(samples);
		}
	}
}
