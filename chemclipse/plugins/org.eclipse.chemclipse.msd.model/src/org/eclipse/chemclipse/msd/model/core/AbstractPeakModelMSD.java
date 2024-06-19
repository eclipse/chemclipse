/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakModel;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;

public abstract class AbstractPeakModelMSD extends PeakModel implements IPeakModelMSD {

	private static final long serialVersionUID = -673699436662069257L;

	protected AbstractPeakModelMSD(IPeakMassSpectrum peakMaximum, IPeakIntensityValues peakIntensityValues, float startBackgroundAbundance, float stopBackgroundAbundance) throws IllegalArgumentException, PeakException {

		super(peakMaximum, peakIntensityValues, startBackgroundAbundance, stopBackgroundAbundance);
	}

	@Override
	public IPeakMassSpectrum getPeakMassSpectrum() {

		IScan peakMaximum = getPeakMaximum();
		if(peakMaximum instanceof IPeakMassSpectrum peakMassSpectrum) {
			return peakMassSpectrum;
		}
		return null;
	}

	@Override
	public IPeakMassSpectrum getPeakMassSpectrum(int retentionTime) {

		IPeakMassSpectrum peakMaximum = getPeakMassSpectrum();
		if(peakMaximum != null) {
			/*
			 * Calculate the mass spectrum at a given retention time.
			 */
			if(retentionTime >= getStartRetentionTime() && retentionTime <= getStopRetentionTime()) {
				float intensity = getIntensity(retentionTime);
				if(intensity >= 0) {
					return new PeakMassSpectrum(peakMaximum, intensity);
				}
			}
		}
		return null;
	}
}
