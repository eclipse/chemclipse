/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing;

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class ScalingRange extends AbstractScaling {

	public ScalingRange(int centeringType) {

		super(centeringType);
	}

	@Override
	public String getDescription() {

		return "Range scaling";
	}

	@Override
	public String getName() {

		return "Range scaling";
	}

	private <S extends ISample> double getMax(List<S> samples, int index) {

		boolean onlySelected = isOnlySelected();
		return samples.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(index)).mapToDouble(value -> getData(value)).summaryStatistics().getMax();
	}

	private <S extends ISample> double getMin(List<S> samples, int index) {

		boolean onlySelected = isOnlySelected();
		return samples.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(index)).mapToDouble(value -> getData(value)).summaryStatistics().getMin();
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		boolean onlySelected = isOnlySelected();
		int centeringType = getCenteringType();
		List<V> variables = samples.getVariables();
		List<S> samplesList = samples.getSamples();
		for(int i = 0; i < variables.size(); i++) {
			if(useVariable(samples, i)) {
				double mean = getCenteringValue(samplesList, i, centeringType);
				double max = getMax(samplesList, i);
				double min = getMin(samplesList, i);
				for(ISample sample : samplesList) {
					ISampleData<?> sampleData = sample.getSampleData().get(i);
					if((sample.isSelected() || !onlySelected)) {
						double data = getData(sampleData);
						double scaleData = (data - mean) / (max - min);
						sampleData.setModifiedData(scaleData);
					}
				}
			}
		}
	}
}