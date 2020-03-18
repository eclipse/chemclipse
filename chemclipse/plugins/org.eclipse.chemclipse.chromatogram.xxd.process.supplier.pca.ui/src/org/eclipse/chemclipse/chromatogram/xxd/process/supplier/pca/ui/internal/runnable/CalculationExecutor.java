/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ProcessorPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ResultsPCA;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class CalculationExecutor implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(CalculationExecutor.class);
	//
	private ISamplesPCA<? extends IVariable, ? extends ISample> samples = null;
	private EvaluationPCA evaluationPCA = null;

	public CalculationExecutor(ISamplesPCA<? extends IVariable, ? extends ISample> samples) {
		this.samples = samples;
	}

	public EvaluationPCA getEvaluationPCA() {

		return evaluationPCA;
	}

	@Override
	public void run(IProgressMonitor arg0) throws InvocationTargetException, InterruptedException {

		if(samples != null) {
			try {
				ProcessorPCA processorPCA = new ProcessorPCA();
				ResultsPCA results = processorPCA.process(samples, new NullProgressMonitor());
				evaluationPCA = new EvaluationPCA(samples, results);
			} catch(MathIllegalArgumentException e) {
				logger.error(e.getLocalizedMessage(), e);
			} catch(Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
