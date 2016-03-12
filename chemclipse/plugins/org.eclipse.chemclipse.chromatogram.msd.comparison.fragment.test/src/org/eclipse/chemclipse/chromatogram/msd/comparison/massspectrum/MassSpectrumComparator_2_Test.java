/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;

import junit.framework.TestCase;

/**
 * Test the IMassSpectrumComparatorSupport.
 */
public class MassSpectrumComparator_2_Test extends TestCase {

	@SuppressWarnings("unused")
	private IMassSpectrumComparisonResult result;
	private IScanMSD massSpectrum1;
	private IScanMSD massSpectrum2;
	//
	private boolean usePreOptimization = false;
	private double thresholdPreOptimization = 0.1d;

	@Override
	protected void setUp() throws Exception {

		massSpectrum1 = new ScanMSD();
		massSpectrum2 = new ScanMSD();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum1 = null;
		massSpectrum2 = null;
		super.tearDown();
	}

	public void testMassSpectrumComparatorCompare_1() {

		IMassSpectrumComparatorProcessingInfo processingInfo = MassSpectrumComparator.compare(null, null, null, usePreOptimization, thresholdPreOptimization);
		assertTrue(processingInfo.hasErrorMessages());
		try {
			result = processingInfo.getMassSpectrumComparisonResult();
		} catch(Exception e) {
			assertTrue("Exception", true);
		}
	}

	public void testMassSpectrumComparatorCompare_2() {

		IMassSpectrumComparatorProcessingInfo processingInfo = MassSpectrumComparator.compare(massSpectrum1, null, null, usePreOptimization, thresholdPreOptimization);
		assertTrue(processingInfo.hasErrorMessages());
		try {
			result = processingInfo.getMassSpectrumComparisonResult();
		} catch(Exception e) {
			assertTrue("Exception", true);
		}
	}

	public void testMassSpectrumComparatorCompare_3() {

		IMassSpectrumComparatorProcessingInfo processingInfo = MassSpectrumComparator.compare(null, massSpectrum2, null, usePreOptimization, thresholdPreOptimization);
		assertTrue(processingInfo.hasErrorMessages());
		try {
			result = processingInfo.getMassSpectrumComparisonResult();
		} catch(Exception e) {
			assertTrue("Exception", true);
		}
	}

	public void testMassSpectrumComparatorCompare_4() {

		IMassSpectrumComparatorProcessingInfo processingInfo = MassSpectrumComparator.compare(null, null, "?", usePreOptimization, thresholdPreOptimization);
		assertTrue(processingInfo.hasErrorMessages());
		try {
			result = processingInfo.getMassSpectrumComparisonResult();
		} catch(Exception e) {
			assertTrue("Exception", true);
		}
	}

	public void testMassSpectrumComparatorCompare_5() {

		IMassSpectrumComparatorProcessingInfo processingInfo = MassSpectrumComparator.compare(massSpectrum1, massSpectrum2, "?", usePreOptimization, thresholdPreOptimization);
		assertTrue(processingInfo.hasErrorMessages());
		try {
			result = processingInfo.getMassSpectrumComparisonResult();
		} catch(Exception e) {
			assertTrue("Exception", true);
		}
	}

	public void testMassSpectrumComparatorCompare_6() {

		IMassSpectrumComparatorProcessingInfo processingInfo = MassSpectrumComparator.compare(massSpectrum1, null, "?", usePreOptimization, thresholdPreOptimization);
		assertTrue(processingInfo.hasErrorMessages());
		try {
			result = processingInfo.getMassSpectrumComparisonResult();
		} catch(Exception e) {
			assertTrue("Exception", true);
		}
	}
}
