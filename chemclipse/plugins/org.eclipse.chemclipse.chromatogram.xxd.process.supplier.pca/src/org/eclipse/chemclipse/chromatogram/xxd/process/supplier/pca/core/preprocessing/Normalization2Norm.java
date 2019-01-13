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

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class Normalization2Norm extends AbstractPreprocessing implements INormalization {

	public Normalization2Norm() {

		super();
	}

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "Normalization 2-norm";
	}

	@Override
	public <V extends IVariable, S extends ISample> void process(ISamples<V, S> samples) {

		for(ISample sample : samples.getSampleList()) {
			if(sample.isSelected() || !isOnlySelected()) {
				double sum = Math.sqrt(sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(d -> getData(d) * getData(d)).sum());
				sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> d.setModifiedData(getData(d) / sum));
			}
		}
	}
}
