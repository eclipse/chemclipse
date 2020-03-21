/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

public class CenteringMean extends AbstractCentering {

	@Override
	public String getDescription() {

		return "Mean centering";
	}

	@Override
	public String getName() {

		return "Mean centering";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		for(int i = 0; i < variables.size(); i++) {
			if(skipVariable(samples, i)) {
				continue;
			}
			final double value = getCenteringValue(samples.getSampleList(), i, MEAN);
			final int j = i;
			samples.getSampleList().stream().forEach(s -> {
				ISampleData data = s.getSampleData().get(j);
				data.setModifiedData(getData(data) - value);
			});
		}
	}

	@Override
	public int getCenteringType() {

		return MEAN;
	}
}
