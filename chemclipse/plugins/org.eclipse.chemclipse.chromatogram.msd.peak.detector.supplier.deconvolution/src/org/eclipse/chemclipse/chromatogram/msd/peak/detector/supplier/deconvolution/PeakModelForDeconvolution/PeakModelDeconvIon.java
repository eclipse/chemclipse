/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.PeakModelForDeconvolution;

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

public class PeakModelDeconvIon implements IPeakModelDeconvIon {

	private double ion;
	private int retentionTime;
	private float abundance;
	private int scanMax;

	public PeakModelDeconvIon(IChromatogramPeakMSD peakModel) {
		ion = peakModel.getExtractedMassSpectrum().getHighestIon().getIon();
		retentionTime = peakModel.getExtractedMassSpectrum().getRetentionTime();
		abundance = peakModel.getExtractedMassSpectrum().getHighestAbundance().getAbundance();
		scanMax = peakModel.getScanMax();
	}

	public double getIon() {

		return ion;
	}

	public int getRetentionTime() {

		return retentionTime;
	}

	public float getAbundance() {

		return abundance;
	}

	public int getScanMax() {

		return scanMax;
	}
}
