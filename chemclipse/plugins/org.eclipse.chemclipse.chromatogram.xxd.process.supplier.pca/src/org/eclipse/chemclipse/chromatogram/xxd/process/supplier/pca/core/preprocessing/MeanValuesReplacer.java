/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class MeanValuesReplacer extends AbstractDataModificator {

	@Override
	public String getDescription() {

		return "Replace NAN value with mean";
	}

	@Override
	public String getName() {

		return "Mean Value Setter";
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		List<S> sampleList = samples.getSampleList();
		for(int i = 0; i < variables.size(); i++) {
			if(skipVariable(samples, i)) {
				continue;
			}
			double sum = 0;
			int count = 0;
			for(S sample : sampleList) {
				if(sample.isSelected() || !isOnlySelected()) {
					double sampleData = getData(sample.getSampleData().get(i));
					if(!Double.isNaN(sampleData)) {
						sum += sampleData;
						count++;
					}
				}
			}
			double mean = count != 0 ? sum / count : 0;
			for(S sample : sampleList) {
				if(sample.isSelected() || !isOnlySelected()) {
					ISampleData sampleData = sample.getSampleData().get(i);
					if(Double.isNaN(getData(sampleData))) {
						sampleData.setModifiedData(mean);
					}
				}
			}
		}
	}
}
