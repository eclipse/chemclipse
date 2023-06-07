/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.preprocessing;

import java.util.List;
import java.util.Random;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class SmallValuesReplacer extends AbstractDataModificator implements IReplacer {

	@Override
	public String getDescription() {

		return "Replace NAN value with small random values";
	}

	@Override
	public String getName() {

		return "Small Random Value Setter";
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		List<S> sampleList = samples.getSampleList();
		final Random rand = new Random();
		for(int i = 0; i < variables.size(); i++) {
			if(skipVariable(samples, i)) {
				continue;
			}
			for(S sample : sampleList) {
				if(sample.isSelected() || !isOnlySelected()) {
					ISampleData<?> sampleData = sample.getSampleData().get(i);
					if(Double.isNaN(getData(sampleData))) {
						double replacement = -1.0;
						while(replacement < 0) {
							replacement = Double.longBitsToDouble(rand.nextLong());
							if(!(replacement > 1.e-20 && replacement > 0 && replacement < 1.e-19)) {
								replacement = -1.0;
							}
						}
						sampleData.setModifiedData(replacement);
					}
				}
			}
		}
	}
}
