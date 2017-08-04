/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PcaPeaksInputRunnable implements IRunnableWithProgress {

	private int numberOfPrincipleComponents;
	private PcaExtractionData pcaExtractionData;
	private PcaFiltrationData pcaFiltrationData;
	private PcaNormalizationData pcaNormalizationData;
	private IPcaResults pcaResults;

	public PcaPeaksInputRunnable(PcaExtractionData pcaExtractionData, PcaFiltrationData pcaFiltrationData, PcaNormalizationData pcaNormalizationData, int numberOfPrincipleComponents) {
		this.pcaExtractionData = pcaExtractionData;
		this.pcaFiltrationData = pcaFiltrationData;
		this.pcaNormalizationData = pcaNormalizationData;
		this.pcaNormalizationData = pcaNormalizationData;
		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	public IPcaResults getPcaResults() {

		return pcaResults;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		pcaResults = pcaExtractionData.proccess(monitor);
		pcaNormalizationData.process(pcaResults, monitor);
		pcaFiltrationData.process(pcaResults, true, monitor);
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		pcaEvaluation.process(pcaResults, numberOfPrincipleComponents, monitor);
	}
}
