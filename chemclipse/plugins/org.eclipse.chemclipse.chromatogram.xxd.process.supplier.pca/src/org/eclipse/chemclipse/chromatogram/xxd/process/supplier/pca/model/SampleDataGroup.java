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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

public class SampleDataGroup<S extends ISample<? extends ISampleData>> implements ISampleDataGroup {

	private int order;
	private List<S> samples;

	public SampleDataGroup(List<S> samples, int order) {
		this.samples = samples;
		this.order = order;
	}

	@Override
	public double getData() {

		double sum = 0;
		int count = samples.size();
		for(S sample : samples) {
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
		for(S sample : samples) {
			sum += sample.getSampleData().get(order).getModifiedData();
		}
		if(count != 0) {
			return sum / count;
		} else {
			return 0;
		}
	}

	@Override
	public boolean isEmpty() {

		return samples.isEmpty();
	}

	@Override
	public void setModifiedData(double normalizedData) {

	}
}
