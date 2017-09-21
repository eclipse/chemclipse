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

public class TransformationPower extends AbstractPreprocessing {

	@Override
	public void process(ISamples samples) {

		samples.getSampleList().stream().filter(ISample::isSelected).forEach(s -> {
			for(ISampleData data : s.getSampleData()) {
				data.setModifiedData(Math.sqrt(Math.abs(data.getModifiedData())));
			}
		});
	}
}
