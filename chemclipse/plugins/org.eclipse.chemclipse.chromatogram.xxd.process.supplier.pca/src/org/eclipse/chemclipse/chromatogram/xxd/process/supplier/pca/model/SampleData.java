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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.Optional;

import org.eclipse.chemclipse.model.core.IPeak;

public class SampleData implements ISampleData {

	private double data;
	private boolean isEmpty;
	private double normalizedData;
	private Optional<IPeak> peak;

	public SampleData() {
		this.data = 0.0;
		this.normalizedData = 0.0;
		this.isEmpty = true;
		peak = Optional.empty();
	}

	public SampleData(double data) {
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

	public Optional<IPeak> getPeak() {

		return peak;
	}

	public void setPeak(IPeak peak) {

		this.peak = Optional.of(peak);
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
