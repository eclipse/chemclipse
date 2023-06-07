/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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
import java.util.stream.IntStream;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class Normalization2Norm extends AbstractDataModificator implements INormalization {

	public Normalization2Norm() {

		super();
	}

	@Override
	public String getDescription() {

		return "Euclidean norm, standard norm or 2-norm is the square root of the sum of the squares of the values.";
	}

	@Override
	public String getName() {

		return "Normalization 2-Norm";
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		for(ISample sample : samples.getSampleList()) {
			if(sample.isSelected() || !isOnlySelected()) {
				List<? extends ISampleData<?>> sampleData = sample.getSampleData();
				double sum = Math.sqrt(IntStream.range(0, sampleData.size()).filter(i -> !sampleData.get(i).isEmpty()).filter(i -> !skipVariable(samples, i))//
						.mapToDouble(i -> getData(sampleData.get(i)) * getData(sampleData.get(i))).sum());
				IntStream.range(0, sampleData.size()).filter(i -> !sampleData.get(i).isEmpty()).filter(i -> !skipVariable(samples, i))//
						.forEach(i -> sampleData.get(i).setModifiedData(getData(sampleData.get(i)) / sum));
			}
		}
	}
}
