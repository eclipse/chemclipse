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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public class AbstractSampleData implements ISampleData {

	private double data;
	private boolean isEmpty;
	private double normalizedData;

	public AbstractSampleData() {
		this.data = 0.0;
		this.normalizedData = 0.0;
		this.isEmpty = true;
	}

	public AbstractSampleData(double data) {
		this();
		this.data = data;
		this.normalizedData = data;
		this.isEmpty = false;
	}

	@Override
	public double getData() {

		return data;
	}

	@Override
	public double getModifiedData() {

		return normalizedData;
	}

	@Override
	public boolean isEmpty() {

		return isEmpty;
	}

	@Override
	public void setModifiedData(double normalizedData) {

		this.normalizedData = normalizedData;
	}
}
