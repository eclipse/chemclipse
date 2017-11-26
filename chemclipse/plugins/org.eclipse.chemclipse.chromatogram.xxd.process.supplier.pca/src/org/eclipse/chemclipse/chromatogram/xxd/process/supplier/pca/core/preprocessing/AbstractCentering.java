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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;

public abstract class AbstractCentering extends AbstractPreprocessing implements ICentering {

	public AbstractCentering() {
	}

	protected <S extends ISample<? extends ISampleData>> double getCenteringValue(List<S> list, int position, int type) {

		boolean onlySelected = isOnlySelected();
		DoubleStream selectedData = list.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(position)).filter(d -> !d.isEmpty()).mapToDouble(d -> d.getModifiedData());
		switch(type) {
			case 1:
				return selectedData.summaryStatistics().getAverage();
			case 2:
				List<Double> data = selectedData.sorted().boxed().collect(Collectors.toList());
				int lenght = data.size();
				double median = lenght % 2 == 0 ? (data.get(lenght / 2 - 1) + data.get(lenght / 2)) / 2.0 // even
						: data.get(lenght / 2); //
				return median;
			default:
				throw new RuntimeException("undefine centering");
		}
	}

	protected <S extends ISample<? extends ISampleData>> double getStandartDeviation(List<S> samples, int position, int type) {

		return Math.sqrt(Math.abs(getVariance(samples, position, type)));
	}

	protected <S extends ISample<? extends ISampleData>> double getVariance(List<S> samples, int position, int type) {

		boolean onlySelected = isOnlySelected();
		List<ISampleData> sampleData = samples.stream().filter(s -> s.isSelected() || onlySelected).map(s -> s.getSampleData().get(position)).collect(Collectors.toList());
		int count = sampleData.size();
		if(count > 1) {
			final double mean = getCenteringValue(samples, position, type);
			double sum = sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(d -> {
				double data = d.getModifiedData();
				return (data - mean) * (data - mean);
			}).sum();
			return sum / (count - 1);
		}
		return 0;
	}
}
