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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;

public class Samples extends AbstractSamples<RetentionTime, Sample> {

	private PcaFiltrationData pcaFiltrationData;
	private PcaPreprocessingData pcaPreprocessingData;

	public Samples(List<IDataInputEntry> dataInputEntries) {
		super();
		dataInputEntries.forEach(d -> getSampleList().add(new Sample(d)));
	}

	public PcaFiltrationData getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	public PcaPreprocessingData getPcaPreprocessingData() {

		return pcaPreprocessingData;
	}

	public void setPcaFiltrationData(PcaFiltrationData pcaFiltrationData) {

		this.pcaFiltrationData = pcaFiltrationData;
	}

	public void setPcaPreprocessingData(PcaPreprocessingData pcaPreprocessingData) {

		this.pcaPreprocessingData = pcaPreprocessingData;
	}
}
