/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

public class GaussianPeakMSDFactory {

	private static final float RATIO_OF_RETENTION_TIME_TO_CONSIDER = 0.005f;
	private static final double NORMALIZATION_VALUE = 1000d;

	private GaussianPeakMSDFactory() {
	}

	public static IPeakMSD createGaussianPeakMSD(IChromatogramMSD chromatogramMSD, float height, int retentionTime, float startBackgroundAbundance, float stopBackgroundAbundance) throws IllegalArgumentException, PeakException {

		if(chromatogramMSD == null) {
			throw new PeakException("The chromatogram must not be null.");
		}
		final int scanNumber = chromatogramMSD.getScanNumber(retentionTime);
		int retentionTimeRange = (int)(RATIO_OF_RETENTION_TIME_TO_CONSIDER * retentionTime);
		// rounding
		if(retentionTimeRange % 2 == 1) {
			retentionTimeRange += 1;
		}
		// create the gaussian curve, and fit peak values
		final Gaussian gaussian = new Gaussian(NORMALIZATION_VALUE, retentionTime, retentionTimeRange);
		final IPeakIntensityValues peakIntensities = new PeakIntensityValues(height);
		for(int rt = retentionTime - 3 * retentionTimeRange; rt <= retentionTime + 3 * retentionTimeRange; rt += retentionTimeRange / 2) {
			peakIntensities.addIntensityValue(rt, (float)gaussian.value(rt));
		}
		peakIntensities.normalize();
		IPeakModelMSD peakModelMSD = new PeakModelMSD(new PeakMassSpectrum(chromatogramMSD.getSupplierScan(scanNumber)), peakIntensities, startBackgroundAbundance, stopBackgroundAbundance);
		return new PeakMSD(peakModelMSD);
	}
}
