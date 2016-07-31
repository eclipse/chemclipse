/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.AbstractPeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.core.PeakQuantitationCalculatorESTD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.settings.IChemClipsePeakQuantifierSettings;

public class ChemClipsePeakQuantifierESTD extends AbstractPeakQuantifier implements IPeakQuantifier {

	@Override
	public IPeakQuantifierProcessingInfo quantify(IPeakMSD peak, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		return quantify(peaks, peakQuantifierSettings, monitor);
	}

	@Override
	public IPeakQuantifierProcessingInfo quantify(IPeakMSD peak, IProgressMonitor monitor) {

		List<IPeakMSD> peaks = new ArrayList<IPeakMSD>();
		peaks.add(peak);
		IChemClipsePeakQuantifierSettings peakQuantifierSettings = PreferenceSupplier.getPeakQuantifierSetting();
		return quantify(peaks, peakQuantifierSettings, monitor);
	}

	@Override
	public IPeakQuantifierProcessingInfo quantify(List<IPeakMSD> peaks, IPeakQuantifierSettings peakQuantifierSettings, IProgressMonitor monitor) {

		PeakQuantitationCalculatorESTD peakQuantitationCalculator = new PeakQuantitationCalculatorESTD();
		return peakQuantitationCalculator.quantify(peaks, peakQuantifierSettings, monitor);
	}

	@Override
	public IPeakQuantifierProcessingInfo quantify(List<IPeakMSD> peaks, IProgressMonitor monitor) {

		IChemClipsePeakQuantifierSettings peakQuantifierSettings = PreferenceSupplier.getPeakQuantifierSetting();
		return quantify(peaks, peakQuantifierSettings, monitor);
	}
}