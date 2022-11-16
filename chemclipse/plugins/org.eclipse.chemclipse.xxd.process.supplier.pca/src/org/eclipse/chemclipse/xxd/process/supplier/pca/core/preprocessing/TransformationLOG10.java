/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
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

public class TransformationLOG10 extends AbstractDataModificator implements ITransformation {

	@Override
	public String getDescription() {

		return "Decadic Logarithm Transformation";
	}

	@Override
	public String getName() {

		return "Decadic Logarithm Transformation";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		samples.getSampleList().stream().filter(s -> s.isSelected() || !isOnlySelected()).forEach(s -> {
			List<? extends ISampleData> sampleData = s.getSampleData();
			for(int i = 0; i < sampleData.size(); i++) {
				if(skipVariable(samples, i)) {
					continue;
				}
				ISampleData data = sampleData.get(i);
				data.setModifiedData(10.0 * Math.log10(getData(data)));
			}
		});
	}
}
