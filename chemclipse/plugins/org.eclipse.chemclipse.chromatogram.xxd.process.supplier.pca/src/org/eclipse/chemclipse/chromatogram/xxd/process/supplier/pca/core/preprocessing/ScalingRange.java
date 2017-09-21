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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;

public class ScalingRange extends AbstaractScaling {

	public ScalingRange(int centeringType) {
		super(centeringType);
	}

	private double getMax(List<ISample> samples, int index) {

		boolean onlySelected = isOnlySelected();
		return samples.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(index)).filter(d -> !d.isEmpty()).mapToDouble(s -> s.getModifiedData()).summaryStatistics().getMax();
	}

	private double getMin(List<ISample> samples, int index) {

		boolean onlySelected = isOnlySelected();
		return samples.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(index)).filter(d -> !d.isEmpty()).mapToDouble(s -> s.getModifiedData()).summaryStatistics().getMin();
	}

	@Override
	public void process(ISamples samples) {

		boolean onlySelected = isOnlySelected();
		int centeringType = getCenteringType();
		List<IRetentionTime> retentionTime = samples.getExtractedRetentionTimes();
		List<ISample> samplesList = samples.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samplesList, i, centeringType);
			final double max = getMax(samplesList, i);
			final double min = getMin(samplesList, i);
			for(ISample sample : samplesList) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySelected)) {
					double data = sampleData.getModifiedData();
					if(max != min) {
						double scaleData = (data - mean) / (max - min);
						sampleData.setModifiedData(scaleData);
					}
				}
			}
		}
	}
}
