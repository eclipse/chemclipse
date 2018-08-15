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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.core;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.core.PeakQuantitationCalculatorISTD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChemClipsePeakQuantifierISTD {

	private PeakQuantitationCalculatorISTD calculatorISTD;

	public ChemClipsePeakQuantifierISTD() {
		calculatorISTD = new PeakQuantitationCalculatorISTD();
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo quantifySelectedPeak(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		return calculatorISTD.quantifySelectedPeak(chromatogramSelection, monitor);
	}

	@SuppressWarnings("rawtypes")
	public IProcessingInfo quantifyAllPeaks(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		return calculatorISTD.quantifyAllPeaks(chromatogramSelection, monitor);
	}
}