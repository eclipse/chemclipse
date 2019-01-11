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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class NormalizationInfNorm extends AbstractPreprocessing implements INormalization {

	public NormalizationInfNorm() {

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
				double max = sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(d -> Math.abs(getData(d))).summaryStatistics().getMax();
				sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> d.setModifiedData(getData(d) / max));
			}
		}
	}
}
