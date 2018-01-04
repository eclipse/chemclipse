/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.internal.converter;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;

public class PeakSupport {

	private IPeakIntensityValues peakIntensityValues;
	private IPeakMassSpectrum peakMaximum;
	private String modelDescription;

	public PeakSupport() {
		peakIntensityValues = new PeakIntensityValues();
		peakMaximum = new PeakMassSpectrum();
	}

	public IPeakIntensityValues getPeakIntensityValues() {

		return peakIntensityValues;
	}

	public void setPeakIntensityValues(IPeakIntensityValues peakIntensityValues) {

		this.peakIntensityValues = peakIntensityValues;
	}

	public IPeakMassSpectrum getPeakMaximum() {

		return peakMaximum;
	}

	public void setPeakMaximum(IPeakMassSpectrum peakMaximum) {

		this.peakMaximum = peakMaximum;
	}

	public String getModelDescription() {

		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {

		this.modelDescription = modelDescription;
	}

	public IPeakMSD getPeak() throws IllegalArgumentException, PeakException {

		peakIntensityValues.normalize();
		IPeakModelMSD peakModel = new PeakModelMSD(peakMaximum, peakIntensityValues, 0.0f, 0.0f);
		IPeakMSD peak = new PeakMSD(peakModel, modelDescription);
		return peak;
	}
}
