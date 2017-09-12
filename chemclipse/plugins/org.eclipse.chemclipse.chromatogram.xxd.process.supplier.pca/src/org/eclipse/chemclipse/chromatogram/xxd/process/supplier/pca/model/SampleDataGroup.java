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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeak;

public class SampleDataGroup implements ISampleData {

	private int order;
	private List<ISample> samples;
	private Set<IPeak> peaks;

	public SampleDataGroup(List<ISample> samples, int order) {
		this.samples = samples;
		this.order = order;
		this.peaks = new HashSet<>();
	}

	@Override
	public double getData() {

		double sum = 0;
		int count = samples.size();
		for(ISample sample : samples) {
			sum += sample.getSampleData().get(order).getData();
		}
		if(count != 0) {
			return sum / count;
		} else {
			return 0;
		}
	}

	@Override
	public double getModifiedData() {

		double sum = 0;
		int count = samples.size();
		for(ISample sample : samples) {
			sum += sample.getSampleData().get(order).getModifiedData();
		}
		if(count != 0) {
			return sum / count;
		} else {
			return 0;
		}
	}

	@Override
	public Set<IPeak> getPeaks() {

		return peaks;
	}

	@Override
	public boolean isEmpty() {

		return samples.isEmpty();
	}

	@Override
	public void setModifiedData(double normalizedData) {

	}
}
