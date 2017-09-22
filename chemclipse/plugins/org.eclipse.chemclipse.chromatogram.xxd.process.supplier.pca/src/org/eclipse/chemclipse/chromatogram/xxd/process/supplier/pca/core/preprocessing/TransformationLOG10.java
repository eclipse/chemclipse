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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;

public class TransformationLOG10 extends AbstractPreprocessing implements ITransformation {

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "Decadic logarithm transformation";
	}

	@Override
	public void process(ISamples samples) {

		samples.getSampleList().stream().filter(s -> s.isSelected() || !isOnlySelected()).forEach(s -> {
			for(ISampleData data : s.getSampleData()) {
				if(!data.isEmpty()) {
					data.setModifiedData(10.0 * Math.log10(data.getModifiedData()));
				}
			}
		});
	}
}
