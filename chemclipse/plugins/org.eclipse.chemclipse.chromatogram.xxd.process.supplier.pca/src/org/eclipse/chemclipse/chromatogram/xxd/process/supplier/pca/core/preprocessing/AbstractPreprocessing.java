/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public abstract class AbstractPreprocessing implements IPreprocessing {

	private boolean onlySelected;
	private DATA_TYPE_PROCESSING dataTypeProcessing;

	public AbstractPreprocessing() {

		this.onlySelected = true;
		this.dataTypeProcessing = DATA_TYPE_PROCESSING.MODIFIED_DATA;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	@Override
	public void setDataTypeProcessing(DATA_TYPE_PROCESSING processDataType) {

		this.dataTypeProcessing = processDataType;
	}

	@Override
	public DATA_TYPE_PROCESSING getDataTypeProcessing() {

		return dataTypeProcessing;
	}

	protected <V extends IVariable, S extends ISample> List<S> selectSamples(ISamples<V, S> samples) {

		return samples.getSampleList().stream().filter(s -> s.isSelected() || !onlySelected).collect(Collectors.toList());
	}

	protected double getData(ISampleData sampleData) {

		switch(dataTypeProcessing) {
			case MODIFIED_DATA:
				return sampleData.getModifiedData();
			case RAW_DATA:
				return sampleData.getData();
		}
		throw new UnsupportedOperationException();
	}
}
