/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PrincipleComponentProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class ReEvaluateRunnable implements IRunnableWithProgress {

	private IPcaResults pcaResults;

	public ReEvaluateRunnable(IPcaResults pcaResults) {
		this.pcaResults = pcaResults;
	}

	public IPcaResults getPcaResults() {

		return pcaResults;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/*
		 * Extraction type argument 0 for peaks, 1 for scans
		 */
		PrincipleComponentProcessor principleComponentProcessor = new PrincipleComponentProcessor();
		pcaResults = principleComponentProcessor.reEvaluate(pcaResults);
	}
}
