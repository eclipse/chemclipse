/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;

public class GaussianPeakFactoryMSD {

	private static final float RATIO_OF_RETENTION_TIME_TO_CONSIDER = 0.005f;
	private static final double NORMALIZATION_VALUE = 1000d;
	private static final double ION_VALUE = 18d;
	private static final String INTEGRATOR_DESCRIPTION = "Gaussian Peak generator";

	private GaussianPeakFactoryMSD() {
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
		final IVendorMassSpectrum vendorMassSpectrum = chromatogramMSD.getSupplierScan(scanNumber);
		final IPeakModelMSD peakModelMSD = new PeakModelMSD(new PeakMassSpectrum(vendorMassSpectrum), peakIntensities, startBackgroundAbundance, stopBackgroundAbundance);
		return new PeakMSD(peakModelMSD);
	}

	public static IPeakMSD createGaussianPeakMSD(IChromatogramMSD chromatogramMSD, float height, double area, int retentionTime, float startBackgroundAbundance, float stopBackgroundAbundance, float retentionIndex, int setRetentionTimeColumn1, int setRetentionTimeColumn2) throws IllegalArgumentException, PeakException {

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
		final IVendorMassSpectrum vendorMassSpectrum = chromatogramMSD.getSupplierScan(scanNumber);
		/*
		 * Here we pass the retention times and indexes
		 */
		final IPeakMassSpectrum peakMassSpectrum = new PeakMassSpectrum(vendorMassSpectrum);
		peakMassSpectrum.setRetentionIndex(retentionIndex);
		peakMassSpectrum.setRetentionTimeColumn1(setRetentionTimeColumn1);
		peakMassSpectrum.setRetentionTimeColumn2(setRetentionTimeColumn2);
		final IPeakModelMSD peakModelMSD = new PeakModelMSD(peakMassSpectrum, peakIntensities, startBackgroundAbundance, stopBackgroundAbundance);
		/*
		 * Set Peak Area
		 */
		final IIntegrationEntry integrationEntry = new IntegrationEntryMSD(ION_VALUE, area);
		final List<IIntegrationEntry> integrationEntries = new ArrayList<>();
		integrationEntries.add(integrationEntry);
		final IPeakMSD peakMSD = new PeakMSD(peakModelMSD);
		peakMSD.setIntegratedArea(integrationEntries, INTEGRATOR_DESCRIPTION);
		return peakMSD;
	}
}
