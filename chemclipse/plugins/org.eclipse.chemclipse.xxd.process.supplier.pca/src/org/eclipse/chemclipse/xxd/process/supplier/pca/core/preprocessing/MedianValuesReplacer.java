/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class MedianValuesReplacer extends AbstractDataModificator implements IReplacer {

	@Override
	public String getDescription() {

		return "Replace NAN value with median";
	}

	@Override
	public String getName() {

		return "Median Value Setter";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		List<S> sampleList = samples.getSampleList();
		for(int i = 0; i < variables.size(); i++) {
			if(skipVariable(samples, i)) {
				continue;
			}
			List<Double> collectedValues = new ArrayList<>();
			for(S sample : sampleList) {
				if(sample.isSelected() || !isOnlySelected()) {
					double sampleData = getData(sample.getSampleData().get(i));
					if(!Double.isNaN(sampleData)) {
						collectedValues.add(sampleData);
					}
				}
			}
			int lenght = collectedValues.size();
			collectedValues.sort((d1, d2) -> Double.compare(d1, d2));
			double median = 0;
			if(lenght != 0) {
				median = lenght % 2 == 0 ? (collectedValues.get(lenght / 2 - 1) + collectedValues.get(lenght / 2)) / 2.0 // even
						: collectedValues.get(lenght / 2); //
			}
			for(S sample : sampleList) {
				if(sample.isSelected() || !isOnlySelected()) {
					ISampleData sampleData = sample.getSampleData().get(i);
					if(Double.isNaN(getData(sampleData))) {
						sampleData.setModifiedData(median);
					}
				}
			}
		}
	}
}
