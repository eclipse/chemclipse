/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class Normalization1Norm implements INormalization {

	private boolean isOnlySelected;

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "Normalization 1-norm";
	}

	@Override
	public boolean isOnlySelected() {

		return isOnlySelected;
	}

	@Override
	public <V extends IVariable, S extends ISample<? extends ISampleData>> void process(ISamples<V, S> samples) {

		for(ISample<?> sample : samples.getSampleList()) {
			if(sample.isSelected() || !isOnlySelected) {
				double sum = sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(d -> Math.abs(d.getModifiedData())).sum();
				if(sum != 0) {
					sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> d.setModifiedData(d.getModifiedData() / sum));
				}
			}
		}
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.isOnlySelected = onlySelected;
	}
}
