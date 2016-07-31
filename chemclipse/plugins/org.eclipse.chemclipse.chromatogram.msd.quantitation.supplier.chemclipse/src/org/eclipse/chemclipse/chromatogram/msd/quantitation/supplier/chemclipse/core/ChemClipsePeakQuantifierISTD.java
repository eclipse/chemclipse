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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.core;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.PeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChemClipsePeakQuantifierISTD {

	public IPeakQuantifierProcessingInfo quantifySelectedPeak(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IPeakQuantifierProcessingInfo processingInfo = new PeakQuantifierProcessingInfo();
		return processingInfo;
	}

	public IPeakQuantifierProcessingInfo quantifyAllPeaks(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IPeakQuantifierProcessingInfo processingInfo = new PeakQuantifierProcessingInfo();
		return processingInfo;
	}
}