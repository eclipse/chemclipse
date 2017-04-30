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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.core.EvaluationProcessor;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class EvaluationSelectionRunnable implements IRunnableWithProgress {

	private IChromatogramSelectionMSD chromatogramSelectionMSD;

	public EvaluationSelectionRunnable(IChromatogramSelectionMSD chromatogramSelectionMSD) {
		this.chromatogramSelectionMSD = chromatogramSelectionMSD;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Process Chromatogram", IProgressMonitor.UNKNOWN);
			EvaluationProcessor evaluationProcessor = new EvaluationProcessor();
			evaluationProcessor.processChromatogram(chromatogramSelectionMSD, monitor);
			chromatogramSelectionMSD.update(true);
		} finally {
			monitor.done();
		}
	}
}
