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

public class ScalingVast extends AbstaractScaling {

	public ScalingVast(int centeringType) {
		super(centeringType);
	}

	@Override
	public void process(ISamples samples) {

		boolean onlySelected = isOnlySelected();
		int centeringType = getCenteringType();
		List<IRetentionTime> retentionTime = samples.getExtractedRetentionTimes();
		List<ISample> samplesList = samples.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samplesList, i, centeringType);
			final double variace = getVariance(samplesList, i, centeringType);
			for(ISample sample : samplesList) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySelected)) {
					double data = sampleData.getModifiedData();
					if(variace != 0) {
						double scaleData = ((data - mean) / variace) * mean;
						sampleData.setModifiedData(scaleData);
					}
				}
			}
		}
	}
}
