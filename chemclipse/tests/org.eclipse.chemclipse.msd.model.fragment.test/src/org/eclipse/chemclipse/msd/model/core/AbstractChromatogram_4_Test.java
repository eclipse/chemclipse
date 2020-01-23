/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.Scan;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;

import junit.framework.TestCase;

public class AbstractChromatogram_4_Test extends TestCase {

	private static final int RT_VARIANCE = 10;

	public void testAddRemovePeak() throws AbundanceLimitExceededException, IonLimitExceededException {

		/*
		 * We have removed the peak identity as it made several problems.
		 * See:
		 * https://github.com/eclipse/chemclipse/issues/216
		 * https://github.com/eclipse/chemclipse/commit/b148ba3c64e043614a7fa6d803f2eda124b04016
		 */
		ChromatogramMSD chromatogram = new ChromatogramMSD();
		chromatogram.addScan(createScanAt(0));
		chromatogram.addScan(createScanAt(1000));
		chromatogram.addScan(createScanAt(2000));
		IPeakModelMSD model1 = createPeakModel(1000, 500);
		IPeakModelMSD model2 = createPeakModel(1000, 600);
		IPeakModelMSD model3 = createPeakModel(1500, 600);
		assertFalse("Peakmodels are equal", model1.equals(model2));
		chromatogram.addPeak(createPeakForModel(chromatogram, model1));
		assertEquals("Peak was not added", 1, chromatogram.getNumberOfPeaks());
		// now add a new peak with the same peakmodel
		chromatogram.addPeak(createPeakForModel(chromatogram, model1));
		assertEquals("Peak with same peak model was added again!", 2, chromatogram.getNumberOfPeaks()); // before 1
		// add it with another model
		chromatogram.addPeak(createPeakForModel(chromatogram, model2));
		assertEquals("Peak with same rtmax but different model was not added!", 3, chromatogram.getNumberOfPeaks()); // before 2
		// add peak with same model but different RT
		chromatogram.addPeak(createPeakForModel(chromatogram, model3));
		assertEquals("Peak with different rtmax but same model was not added!", 4, chromatogram.getNumberOfPeaks()); // before 3
		// now remove one of the peaks
		chromatogram.removePeak(createPeakForModel(chromatogram, model1));
		assertEquals("Peak was not removed", 4, chromatogram.getNumberOfPeaks()); // before 2
		// remove it again should not matter...
		chromatogram.removePeak(createPeakForModel(chromatogram, model1));
		assertEquals("Peak was not removed", 4, chromatogram.getNumberOfPeaks()); // befor 2
	}

	public void testGetPeaks() throws AbundanceLimitExceededException, IonLimitExceededException {

		ChromatogramMSD chromatogram = new ChromatogramMSD();
		chromatogram.addScan(createScanAt(0));
		chromatogram.addScan(createScanAt(1000));
		chromatogram.addScan(createScanAt(2000));
		int numberOfPeaks = 1000;
		int peakStart = 100;
		for(int i = 0; i < numberOfPeaks / 2; i++) {
			// create peaks that are very close together and overlap in their shapes and two peaks for each RT
			chromatogram.addPeak(createPeakForModel(chromatogram, createPeakModel(peakStart + i, 500)));
			chromatogram.addPeak(createPeakForModel(chromatogram, createPeakModel(peakStart + i, 600)));
		}
		assertEquals("Peak count differs", numberOfPeaks, chromatogram.getNumberOfPeaks());
		assertTrue("Peaks are returned even if no peak rt-max should be in this range", chromatogram.getPeaks(Integer.MIN_VALUE, peakStart - 1).isEmpty());
		assertEquals("Peak list differs in expected size", 2, chromatogram.getPeaks(Integer.MIN_VALUE, peakStart).size());
		assertEquals("Peak list differs in expected size", 4, chromatogram.getPeaks(Integer.MIN_VALUE, peakStart + 1).size());
		assertEquals("Peak list differs in expected size", 4, chromatogram.getPeaks(peakStart, peakStart + 1).size());
		assertEquals("Peak list differs in expected size", 2, chromatogram.getPeaks(peakStart, peakStart).size());
		assertTrue("Peaks are returned even if no peak rt-max should be in this range", chromatogram.getPeaks(peakStart + numberOfPeaks / 2, Integer.MAX_VALUE).isEmpty());
	}

	private static Scan createScanAt(int retentionTime) {

		Scan scan = new Scan(100);
		scan.setRetentionTime(retentionTime);
		return scan;
	}

	private static IChromatogramPeakMSD createPeakForModel(ChromatogramMSD chromatogram, IPeakModelMSD peakModelMSD) {

		final IIntegrationEntry integrationEntry = new IntegrationEntry(18, 1);
		final List<IIntegrationEntry> integrationEntries = new ArrayList<>();
		integrationEntries.add(integrationEntry);
		final IChromatogramPeakMSD peakMSD = new ChromatogramPeakMSD(peakModelMSD, chromatogram);
		peakMSD.setIntegratedArea(integrationEntries, "Unit-Test");
		return peakMSD;
	}

	protected static IPeakModelMSD createPeakModel(int retentionTime, int height) throws AbundanceLimitExceededException, IonLimitExceededException {

		final IPeakIntensityValues peakIntensities = new PeakIntensityValues(1000);
		peakIntensities.addIntensityValue(retentionTime, 1000);
		peakIntensities.addIntensityValue(retentionTime - RT_VARIANCE, height);
		peakIntensities.addIntensityValue(retentionTime + RT_VARIANCE, height);
		final IPeakMassSpectrum peakMassSpectrum = new PeakMassSpectrum();
		peakMassSpectrum.addIon(new Ion(10, 100));
		peakMassSpectrum.addIon(new Ion(12, 1000));
		peakMassSpectrum.addIon(new Ion(13, 100));
		final IPeakModelMSD peakModelMSD = new PeakModelMSD(peakMassSpectrum, peakIntensities, 0, 0);
		return peakModelMSD;
	}
}
