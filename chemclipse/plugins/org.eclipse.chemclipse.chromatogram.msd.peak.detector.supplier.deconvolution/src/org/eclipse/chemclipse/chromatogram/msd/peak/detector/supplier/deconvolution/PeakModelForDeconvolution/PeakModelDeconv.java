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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

public class PeakModelDeconv implements IPeakModelDeconv {

	private double modelIon;
	private int modelRetentionTime;
	private float modelAbundance;
	private int modelScanMax;
	private IScanRange modelScanRange;
	private float ticAbundanceMax;
	private List<IPeakModelDeconvIon> ionsInModel;

	public PeakModelDeconv(IChromatogramPeakMSD peakModel, IScanRange scanRange, float ticSignalAbundanceMax) {
		modelIon = peakModel.getExtractedMassSpectrum().getHighestIon().getIon();
		modelRetentionTime = peakModel.getExtractedMassSpectrum().getRetentionTime();
		modelAbundance = peakModel.getExtractedMassSpectrum().getHighestAbundance().getAbundance();
		modelScanMax = peakModel.getScanMax();
		ionsInModel = new ArrayList<IPeakModelDeconvIon>(0);
		modelScanRange = scanRange;
		ticAbundanceMax = ticSignalAbundanceMax;
	}

	public double getModelIon() {

		return modelIon;
	}

	public int getModelRetentionTime() {

		return modelRetentionTime;
	}

	public float getModelAbundance() {

		return modelAbundance;
	}

	public int getModelScanMax() {

		return modelScanMax;
	}

	public IScanRange getScanRange() {

		return modelScanRange;
	}

	public float getTicAbundanceMax() {

		return ticAbundanceMax;
	}

	public List<IPeakModelDeconvIon> getAllIonsInModel() {

		return ionsInModel;
	}

	public IPeakModelDeconvIon getIonInModel(int scan) {

		return getIonInModel(scan);
	}

	public int sizeOfIonsWithoutModelIon() {

		return ionsInModel.size();
	}

	public void addIonToDeconvModel(IPeakModelDeconvIon deconIon) {

		ionsInModel.add(deconIon);
	}

	public void deletePositionInIonsList(int pos) {

		ionsInModel.remove(pos);
	}
}
