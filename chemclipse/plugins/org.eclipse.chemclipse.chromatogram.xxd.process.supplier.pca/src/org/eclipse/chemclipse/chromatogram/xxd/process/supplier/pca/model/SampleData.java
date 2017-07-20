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

import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeak;

public class SampleData implements ISampleData {

	private double data;
	private double normalizedData;
	private Set<IPeak> peaks;

	public SampleData(double data) {
		this.data = data;
		this.normalizedData = data;
	}

	@Override
	public double getData() {

		return data;
	}

	@Override
	public double getNormalizeData() {

		return normalizedData;
	}

	@Override
	public Set<IPeak> getPeaks() {

		return peaks;
	}

	@Override
	public void setNormalizedData(double normalizedData) {

		this.normalizedData = normalizedData;
	}

	@Override
	public void setPeaks(Set<IPeak> peaks) {

		this.peaks = peaks;
	}
}
