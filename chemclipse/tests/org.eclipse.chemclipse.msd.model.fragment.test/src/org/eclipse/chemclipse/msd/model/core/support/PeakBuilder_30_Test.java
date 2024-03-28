/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_30_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum massSpectrum;
	private IIon defaultIon;
	private IExtractedIonSignals extractedIonSignals;
	private IScanRange scanRange;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * chromatogram
		 */
		List<Float> intensities = new ArrayList<Float>();
		intensities.add(1000.0f);
		intensities.add(5578.14f);
		intensities.add(7596.27f);
		intensities.add(9386.37f);
		intensities.add(5000.0f);
		intensities.add(2709.21f);
		intensities.add(1440.9f);
		intensities.add(810.72f);
		intensities.add(538.22f);
		intensities.add(400.00f);
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(500);
		chromatogram.setScanInterval(1000);
		/*
		 * ScanRange
		 */
		scanRange = new ScanRange(1, 10);
		for(int scan = 1; scan <= 10; scan++) {
			massSpectrum = new VendorMassSpectrum();
			for(int ion = 32; ion <= 38; ion++) {
				defaultIon = new Ion(ion, ion * scan * intensities.get(scan - 1));
				massSpectrum.addIon(defaultIon);
			}
			chromatogram.addScan(massSpectrum);
		}
		chromatogram.recalculateRetentionTimes();
		/*
		 * Total ion signals.
		 */
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		defaultIon = null;
		massSpectrum = null;
		scanRange = null;
		super.tearDown();
	}

	public void testGetPeakIntensityValues_1() {

		IChromatogramPeakMSD peak;
		try {
			peak = PeakBuilderMSD.createPeak(extractedIonSignals, scanRange);
			assertNotNull(peak);
			float totalSignal = peak.getPeakModel().getPeakMassSpectrum().getTotalSignal();
			assertEquals("TotalSignal", 8708643.0f, totalSignal);
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testGetPeakIntensityValues_2() {

		extractedIonSignals = null;
		try {
			PeakBuilderMSD.createPeak(extractedIonSignals, scanRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetPeakIntensityValues_3() {

		try {
			PeakBuilderMSD.createPeak(extractedIonSignals, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
