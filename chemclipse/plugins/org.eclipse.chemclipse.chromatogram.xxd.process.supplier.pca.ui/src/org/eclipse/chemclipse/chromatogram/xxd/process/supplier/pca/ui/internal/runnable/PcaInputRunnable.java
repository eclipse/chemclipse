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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaScalingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class PcaInputRunnable implements IRunnableWithProgress {

	private int numberOfPrincipleComponents;
	private IDataExtraction pcaExtractionData;
	private PcaFiltrationData pcaFiltrationData;
	private IPcaResults pcaResults;
	private PcaScalingData pcaScalingData;
	private ISamples samples;

	public PcaInputRunnable(IDataExtraction pcaExtractionData, PcaFiltrationData pcaFiltrationData, PcaScalingData pcaScalingData, int numberOfPrincipleComponents) {
		this.pcaExtractionData = pcaExtractionData;
		this.pcaFiltrationData = pcaFiltrationData;
		this.pcaScalingData = pcaScalingData;
		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	public IPcaResults getPcaResults() {

		return pcaResults;
	}

	public ISamples getSamples() {

		return samples;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		samples = pcaExtractionData.process(monitor);
		pcaScalingData.process(samples, monitor);
		pcaFiltrationData.process(samples, true, monitor);
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		pcaResults = pcaEvaluation.process(samples, numberOfPrincipleComponents, monitor);
	}
}
