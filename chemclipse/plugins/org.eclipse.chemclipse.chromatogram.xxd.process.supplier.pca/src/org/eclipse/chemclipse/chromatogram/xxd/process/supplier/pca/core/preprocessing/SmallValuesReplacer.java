/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * lgerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing;

import java.util.List;
import java.util.Random;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class SmallValuesReplacer extends AbstractPreprocessing {

	@Override
	public String getDescription() {

		return "Repleace NAN value with small random values";
	}

	@Override
	public String getName() {

		return "Small Random Value Setter";
	}

	@Override
	public <V extends IVariable, S extends ISample<? extends ISampleData>> void process(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		List<S> sampleList = samples.getSampleList();
		for(int i = 0; i < variables.size(); i++) {
			final Random rand = new Random();
			double replacement = -1.0;
			while(replacement < 0) {
				replacement = Double.longBitsToDouble(rand.nextLong());
				if(!(replacement < Double.POSITIVE_INFINITY && replacement > 0)) {
					replacement = -1.0;
				}
			}
			double smallRandomValue = 1d - Math.random();
			for(S sample : sampleList) {
				if(sample.isSelected() || !isOnlySelected()) {
					ISampleData sampleData = sample.getSampleData().get(i);
					if(Double.isNaN(sampleData.getModifiedData())) {
						sampleData.setModifiedData(smallRandomValue);
					}
				}
			}
		}
	}
}
