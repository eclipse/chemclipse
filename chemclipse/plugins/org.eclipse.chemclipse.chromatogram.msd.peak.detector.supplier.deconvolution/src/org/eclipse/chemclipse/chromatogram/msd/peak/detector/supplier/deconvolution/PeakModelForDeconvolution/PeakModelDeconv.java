/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
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

import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;

public class PeakModelDeconv implements IPeakModelDeconv {

	private double modelIon;
	private int modelRetentionTime;
	private float modelAbundance;
	private int modelScanMax;
	private List<IPeakModelDeconvIon> ionsInModel;

	public PeakModelDeconv(IChromatogramPeakMSD peakModel) {
		modelIon = peakModel.getExtractedMassSpectrum().getHighestIon().getIon();
		modelRetentionTime = peakModel.getExtractedMassSpectrum().getRetentionTime();
		modelAbundance = peakModel.getExtractedMassSpectrum().getHighestAbundance().getAbundance();
		modelScanMax = peakModel.getScanMax();
		ionsInModel = new ArrayList<IPeakModelDeconvIon>(0);
	}

	public double getModelIon() {

		return this.modelIon;
	}

	public int getModelRetentionTime() {

		return this.modelRetentionTime;
	}

	public float getModelAbundance() {

		return this.modelAbundance;
	}

	public int getModelScanMax() {

		return this.modelScanMax;
	}

	public List<IPeakModelDeconvIon> getAllIonsInModel() {

		return this.ionsInModel;
	}

	public IPeakModelDeconvIon getIonInModel(int scan) {

		return this.getIonInModel(scan);
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
