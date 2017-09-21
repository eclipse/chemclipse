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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaScalingData implements IDataModification {

	private IPreprocessing centering;
	private boolean enableModificationData;
	private boolean onlySelected;
	private IPreprocessing scaling;
	private IPreprocessing transformation;

	public PcaScalingData(boolean enableModificationData) {
	}

	public IPreprocessing getCentering() {

		return centering;
	}

	public IPreprocessing getScaling() {

		return scaling;
	}

	public IPreprocessing getTransformation() {

		return transformation;
	}

	@Override
	public boolean isEnableModificationData() {

		return enableModificationData;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public void process(ISamples samples, IProgressMonitor monitor) {

		if(enableModificationData) {
			if(transformation != null) {
				transformation.setOnlySelected(onlySelected);
				transformation.process(samples);
			}
			if(centering != null) {
				centering.setOnlySelected(onlySelected);
				centering.process(samples);
			}
			if(scaling != null) {
				scaling.setOnlySelected(onlySelected);
				scaling.process(samples);
			}
		}
	}

	public void setCentering(IPreprocessing centering) {

		this.centering = centering;
	}

	@Override
	public void setEnableModificationData(boolean enable) {

		enableModificationData = enable;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	public void setScaling(IPreprocessing scaling) {

		this.scaling = scaling;
	}

	public void setTransformation(IPreprocessing transformation) {

		this.transformation = transformation;
	}
}
