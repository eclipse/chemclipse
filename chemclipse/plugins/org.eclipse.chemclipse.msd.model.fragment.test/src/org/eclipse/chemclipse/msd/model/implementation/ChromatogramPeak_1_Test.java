/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;

/**
 * The chromatogram and peak will be initialized in DefaultPeakTestCase.<br/>
 * The peak has 15 scans, starting at a retention time of 1500 milliseconds (ms)
 * and ends at a retention time of 15500 ms.<br/>
 * The chromatogram has 17 scans, starting at a retention time of 500 ms and
 * ends at a retention time of 16500 ms. It has a background of 1750 units.
 * 
 * @author eselmeister
 */
public class ChromatogramPeak_1_Test extends ChromatogramPeakTestCase {

	private IChromatogramPeakMSD peak;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		peak = new ChromatogramPeakMSD(getPeakModel(), getChromatogram());
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		super.tearDown();
	}

	public void testGetBackgroundAbundanceAtScan_1() {

		float background = peak.getBackgroundAbundanceAtScan(5);
		assertEquals("background", 1760.1072f, background);
	}

	public void testGetBackgroundAbundanceAtScan_2() {

		float background = peak.getBackgroundAbundanceAtScan(1);
		assertEquals("background", 0.0f, background);
	}

	public void testGetBackgroundAbundanceAtScan_3() {

		float background = peak.getBackgroundAbundanceAtScan(17);
		assertEquals("background", 0.0f, background);
	}

	public void testGetBackgroundAbundanceAtScan_4() {

		float background = peak.getBackgroundAbundanceAtScan(0);
		assertEquals("background", 0.0f, background);
	}

	public void testGetBackgroundAbundanceAtScan_5() {

		float background = peak.getBackgroundAbundanceAtScan(200);
		assertEquals("background", 0.0f, background);
	}

	public void testGetChromatogramMassSpectrum_1() {

		IScanMSD massSpectrum = peak.getChromatogramMassSpectrum();
		assertNotNull(massSpectrum);
		assertEquals("NumberOfIons", 11, massSpectrum.getNumberOfIons());
	}

	public void testGetExctractedMassSpectrum_1() {

		IPeakMassSpectrum massSpectrum = peak.getExtractedMassSpectrum();
		assertNotNull(massSpectrum);
		assertEquals("NumberOfIons", 8, massSpectrum.getNumberOfIons());
	}

	public void testGetModelDescription_1() {

		String modelDescription = peak.getModelDescription();
		assertEquals("ModelDescription", "", modelDescription);
	}

	public void testGetModelDescription_2() {

		String md = "TIC";
		peak.setModelDescription(md);
		String modelDescription = peak.getModelDescription();
		assertEquals("ModelDescription", md, modelDescription);
	}

	public void testGetModelDescription_3() {

		String md = "+104 -56";
		peak.setModelDescription(md);
		String modelDescription = peak.getModelDescription();
		assertEquals("ModelDescription", md, modelDescription);
	}

	public void testGetParentChromatogram_1() {

		IChromatogramMSD chromatogram = peak.getChromatogram();
		assertNotNull("Chromatogram", chromatogram);
	}

	public void testGetPeakAbundanceAtScan_1() {

		float abundance = peak.getPeakAbundanceAtScan(1);
		assertEquals("abundance", 0.0f, abundance);
	}

	public void testGetPeakAbundanceAtScan_2() {

		// At scan 10 is the peak maximum.
		float abundance = peak.getPeakAbundanceAtScan(10);
		assertEquals("abundance", 5231.0f, abundance);
	}

	public void testGetPeakAbundanceAtScan_3() {

		// At scan 5 is the peak abundance at 15% of peak maximum.
		float abundance = peak.getPeakAbundanceAtScan(5);
		assertEquals("abundance", 784.65f, abundance);
	}

	public void testGetPeakAbundanceAtScan_4() {

		// At scan 5 is the peak abundance at 30% of peak maximum.
		float abundance = peak.getPeakAbundanceAtScan(14);
		assertEquals("abundance", 1569.3f, abundance);
	}

	public void testGetPeakAbundanceAtScan_5() {

		float abundance = peak.getPeakAbundanceAtScan(17);
		assertEquals("abundance", 0.0f, abundance);
	}

	public void testGetPeakModel_1() {

		IPeakModelMSD peakModel = peak.getPeakModel();
		assertNotNull("PeakModel", peakModel);
		assertEquals("StartRetentionTime", 1500, peakModel.getStartRetentionTime());
		assertEquals("StopRetentionTime", 15500, peakModel.getStopRetentionTime());
	}

	public void testGetScanMax_1() {

		int scanMax = peak.getScanMax();
		assertEquals("ScanMax", 10, scanMax);
	}

	public void testGetWidthBaselineTotalInScans_1() {

		int width = peak.getWidthBaselineTotalInScans();
		assertEquals("Width", 15, width);
	}

	public void testGetIntegratorDescription_1() {

		assertEquals("IntegratorDescription", "", peak.getIntegratorDescription());
	}

	public void testGetIntegratorDescription_2() {

		String description = "Default Integrator";
		peak.setIntegratorDescription(description);
		assertEquals("IntegratorDescription", description, peak.getIntegratorDescription());
		peak.setIntegratorDescription("");
		assertEquals("IntegratorDescription", "", peak.getIntegratorDescription());
	}

	public void testGetIntegratorDescription_3() {

		peak.setIntegratorDescription(null);
		assertEquals("IntegratorDescription", "", peak.getIntegratorDescription());
	}

	public void testGetDetectorDescription_1() {

		assertEquals("DetectorDescription", "", peak.getDetectorDescription());
	}

	public void testGetDetectorDescription_2() {

		String description = "Default Detector";
		peak.setDetectorDescription(description);
		assertEquals("DetectorDescription", description, peak.getDetectorDescription());
		peak.setDetectorDescription("");
		assertEquals("DetectorDescription", "", peak.getDetectorDescription());
	}

	public void testGetDetectorDescription_3() {

		peak.setDetectorDescription(null);
		assertEquals("DetectorDescription", "", peak.getDetectorDescription());
	}

	public void testGetSN_1() {

		/*
		 * It is null, because the noise factor of the parent chromatogram is 0.
		 */
		assertEquals("NumberOfScans", 17, peak.getChromatogram().getNumberOfScans());
	}
}
