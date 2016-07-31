/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.core.ChemClipsePeakQuantifierISTD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class QuantifySelectedPeakISTDRunnable implements IRunnableWithProgress {

	private IChromatogramSelection chromatogramSelection;

	public QuantifySelectedPeakISTDRunnable(IChromatogramSelection chromatogramSelection) {
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Peak Quantifier (ISTD)", IProgressMonitor.UNKNOWN);
			ChemClipsePeakQuantifierISTD peakQuantifierISTD = new ChemClipsePeakQuantifierISTD();
			peakQuantifierISTD.quantifySelectedPeak(chromatogramSelection, monitor);
			((ChromatogramSelectionMSD)chromatogramSelection).update(true);
		} finally {
			monitor.done();
		}
	}
}
