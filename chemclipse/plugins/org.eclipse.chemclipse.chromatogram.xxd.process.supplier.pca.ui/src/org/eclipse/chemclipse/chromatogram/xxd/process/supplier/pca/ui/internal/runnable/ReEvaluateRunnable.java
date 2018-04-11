/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaModelResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ReEvaluateRunnable implements IRunnableWithProgress {

	private IPcaResults pcaResults;
	private ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples;
	private IPcaSettings settings;
	private IPcaModelResult modelResults;

	public ReEvaluateRunnable(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples, IPcaSettings settings, IPcaModelResult modelResults) {
		this.samples = samples;
		this.settings = settings;
		this.modelResults = modelResults;
	}

	public IPcaResults getPcaResults() {

		return pcaResults;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * Extraction type argument 0 for peaks, 1 for scans
		 */
		PcaEvaluation pcaEvaluation = new PcaEvaluation();
		pcaResults = pcaEvaluation.process(samples, settings, modelResults, monitor);
	}
}
