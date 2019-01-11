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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class ScalingAuto extends AbstaractScaling {

	public ScalingAuto(int centeringType) {

		super(centeringType);
	}

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "AutoScaling";
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		boolean onlySeleted = isOnlySelected();
		int centeringType = getCenteringType();
		List<V> variables = samples.getVariables();
		List<S> samplesList = samples.getSampleList();
		for(int i = 0; i < variables.size(); i++) {
			final double mean = getCenteringValue(samplesList, i, centeringType);
			final double deviation = getStandartDeviation(samplesList, i, centeringType);
			for(ISample sample : samplesList) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if((sample.isSelected() || !onlySeleted)) {
					double data = getData(sampleData);
					double scaleData = 0;
					scaleData = (data - mean) / deviation;
					sampleData.setModifiedData(scaleData);
				}
			}
		}
	}
}
