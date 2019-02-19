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

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.model.statistics.AbstractSamples;
import org.eclipse.chemclipse.model.statistics.RetentionTime;

public class Samples extends AbstractSamples<RetentionTime, Sample> implements IDataPreprocessing, IVariablesFiltration, IDefaultPcaSettings {

	private PcaFiltrationData pcaFiltrationData;
	private PcaPreprocessingData pcaPreprocessingData;
	private IPcaSettings pcaSettings;

	public Samples(Collection<IDataInputEntry> dataInputEntries) {

		super();
		dataInputEntries.forEach(d -> getSampleList().add(new Sample(d)));
	}

	@Override
	public PcaFiltrationData getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	@Override
	public PcaPreprocessingData getPcaPreprocessingData() {

		return pcaPreprocessingData;
	}

	public void setPcaFiltrationData(PcaFiltrationData pcaFiltrationData) {

		this.pcaFiltrationData = pcaFiltrationData;
	}

	public void setPcaPreprocessingData(PcaPreprocessingData pcaPreprocessingData) {

		this.pcaPreprocessingData = pcaPreprocessingData;
	}

	public void setDefaultPcaSettings(IPcaSettings pcaSettings) {

		this.pcaSettings = pcaSettings;
	}

	@Override
	public IPcaSettings getDefaultPcaSettings() {

		return pcaSettings;
	}
}
