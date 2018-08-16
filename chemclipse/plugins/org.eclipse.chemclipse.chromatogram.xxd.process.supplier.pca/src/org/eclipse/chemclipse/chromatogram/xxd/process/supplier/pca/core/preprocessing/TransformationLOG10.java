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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

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
	public <V extends IVariable, S extends ISample<? extends ISampleData>> void process(ISamples<V, S> samples) {

		samples.getSampleList().stream().filter(s -> s.isSelected() || !isOnlySelected()).forEach(s -> {
			for(ISampleData data : s.getSampleData()) {
				data.setModifiedData(10.0 * Math.log10(getData(data)));
			}
		});
	}
}
