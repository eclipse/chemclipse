/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.BackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IBackgroundAbundanceRange;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_17_Test extends TestCase {

	@SuppressWarnings("unused")
	private IChromatogramPeakMSD peak;
	private IScanRange scanRange;
	private IMarkedIons excludedIons;
	private IBackgroundAbundanceRange backgroundAbundanceRange;
	private IPeakMassSpectrum peakMassSpectrum;
	private ITotalScanSignals totalIonSignals;
	private IChromatogramMSD chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scanRange = new ScanRange(1, 20);
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		backgroundAbundanceRange = new BackgroundAbundanceRange(20.0f, 40.0f);
		peakMassSpectrum = new PeakMassSpectrum();
		totalIonSignals = new TotalScanSignals(200);
		chromatogram = new ChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		peak = null;
		scanRange = null;
		excludedIons = null;
		backgroundAbundanceRange = null;
		peakMassSpectrum = null;
		totalIonSignals = null;
		chromatogram = null;
		super.tearDown();
	}

	public void testCreatePeak_2() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, null, true);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_3() {

		try {
			peak = PeakBuilderMSD.createPeak(null, scanRange, backgroundAbundanceRange, true);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_4() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, null, backgroundAbundanceRange, true);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_5() {

		try {
			peak = PeakBuilderMSD.createPeak(null, scanRange, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_6() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, null, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_7() {

		try {
			peak = PeakBuilderMSD.createPeak(null, totalIonSignals, peakMassSpectrum);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_8() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, null, peakMassSpectrum);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_9() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, totalIonSignals, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_10() {

		try {
			peak = PeakBuilderMSD.createPeak(null, scanRange, backgroundAbundanceRange, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_11() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, null, backgroundAbundanceRange, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_12() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, null, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_13() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, scanRange, backgroundAbundanceRange, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_14() {

		try {
			peak = PeakBuilderMSD.createPeak(null, totalIonSignals, peakMassSpectrum, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_15() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, null, peakMassSpectrum, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_16() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, totalIonSignals, null, backgroundAbundanceRange);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testCreatePeak_17() {

		try {
			peak = PeakBuilderMSD.createPeak(chromatogram, totalIonSignals, peakMassSpectrum, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
