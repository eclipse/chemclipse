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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;

public class NormalizationInfNorm implements INormalization {

	private boolean isOnlySelected;

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "Normalization 2-norm";
	}

	@Override
	public boolean isOnlySelected() {

		return isOnlySelected;
	}

	@Override
	public void process(ISamples samples) {

		for(ISample sample : samples.getSampleList()) {
			if(sample.isSelected() || !isOnlySelected) {
				double max = sample.getSampleData().stream().filter(d -> !d.isEmpty()).mapToDouble(d -> d.getModifiedData()).summaryStatistics().getMax();
				if(max != 0) {
					sample.getSampleData().stream().filter(d -> !d.isEmpty()).forEach(d -> d.setModifiedData(d.getModifiedData() / max));
				}
			}
		}
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.isOnlySelected = onlySelected;
	}
}
