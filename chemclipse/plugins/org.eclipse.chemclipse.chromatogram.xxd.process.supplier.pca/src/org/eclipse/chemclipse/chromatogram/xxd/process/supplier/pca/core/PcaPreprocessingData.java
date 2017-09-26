/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ICentering;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.INormalization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.ITransformation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaPreprocessingData implements IDataModification {

	private ICentering centeringScaling;
	private INormalization normalization;
	private boolean onlySelected;
	private ITransformation transformation;

	public PcaPreprocessingData() {
	}

	@Override
	public boolean availableModification() {

		return normalization != null || transformation != null || centeringScaling != null;
	}

	public ICentering getCenteringScaling() {

		return centeringScaling;
	}

	public INormalization getNormalization() {

		return normalization;
	}

	public ITransformation getTransformation() {

		return transformation;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public void process(ISamples samples, IProgressMonitor monitor) {

		for(ISample sample : samples.getSampleList()) {
			sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> {
				double data = d.getData();
				d.setModifiedData(data);
			});
		}
		if(normalization != null) {
			normalization.setOnlySelected(onlySelected);
			normalization.process(samples);
		}
		if(transformation != null) {
			transformation.setOnlySelected(onlySelected);
			transformation.process(samples);
		}
		if(centeringScaling != null) {
			centeringScaling.setOnlySelected(onlySelected);
			centeringScaling.process(samples);
		}
	}

	public void setCenteringScaling(ICentering centeringScaling) {

		this.centeringScaling = centeringScaling;
	}

	public void setNormalization(INormalization normalization) {

		this.normalization = normalization;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	public void setTransformation(ITransformation transformation) {

		this.transformation = transformation;
	}
}
