/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.AbstractPreprocessing;
import org.eclipse.chemclipse.model.statistics.ISampleData;

public abstract class AbstractFilter extends AbstractPreprocessing implements IFilter {

	private DataTypeProcessing dataTypeProcessing;

	public AbstractFilter(DataTypeProcessing dataTypeProcessing) {

		this.dataTypeProcessing = dataTypeProcessing;
	}

	protected double getData(ISampleData<?> sampleData) {

		switch(dataTypeProcessing) {
			case MODIFIED_DATA:
				return sampleData.getModifiedData();
			case RAW_DATA:
				return sampleData.getData();
			default:
				throw new UnsupportedOperationException();
		}
	}

	@Override
	public void setDataTypeProcessing(DataTypeProcessing processDataType) {

		this.dataTypeProcessing = processDataType;
	}

	@Override
	public DataTypeProcessing getDataTypeProcessing() {

		return dataTypeProcessing;
	}
}
