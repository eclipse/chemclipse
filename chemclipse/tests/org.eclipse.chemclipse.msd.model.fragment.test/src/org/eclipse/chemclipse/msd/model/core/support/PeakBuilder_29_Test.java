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

import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_29_Test extends TestCase {

	private ITotalScanSignals totalIonSignals;
	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum massSpectrum;
	private IIon defaultIon;
	private LinearEquation backgroundEquation;
	private IMarkedIons excludedIons;
	private IExtractedIonSignals extractedIonSignals;
	private ITotalScanSignalExtractor totalIonSignalExtractor;
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
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		totalIonSignals = totalIonSignalExtractor.getTotalScanSignals();
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals();
		/*
		 * background equation
		 */
		IPoint p1 = new Point(10, 0);
		IPoint p2 = new Point(100, 0);
		backgroundEquation = Equations.createLinearEquation(p1, p2);
		/*
		 * Excluded ions
		 */
		excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		backgroundEquation = null;
		excludedIons = null;
		totalIonSignals = null;
		super.tearDown();
	}

	public void testGetPeakIntensityValues_1() {

		IPeakMassSpectrum peakMassSpectrum;
		try {
			peakMassSpectrum = PeakBuilderMSD.getPeakMassSpectrum(extractedIonSignals, chromatogram, totalIonSignals, backgroundEquation, excludedIons);
			assertNotNull(peakMassSpectrum);
			assertEquals("Total Signal", 9198643.0f, peakMassSpectrum.getTotalSignal());
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testGetPeakIntensityValues_2() {

		try {
			PeakBuilderMSD.getPeakMassSpectrum(null, chromatogram, totalIonSignals, backgroundEquation, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetPeakIntensityValues_3() {

		try {
			PeakBuilderMSD.getPeakMassSpectrum(extractedIonSignals, null, totalIonSignals, backgroundEquation, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetPeakIntensityValues_4() {

		try {
			PeakBuilderMSD.getPeakMassSpectrum(extractedIonSignals, chromatogram, null, backgroundEquation, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetPeakIntensityValues_5() {

		try {
			PeakBuilderMSD.getPeakMassSpectrum(extractedIonSignals, chromatogram, totalIonSignals, null, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetPeakIntensityValues_6() {

		try {
			PeakBuilderMSD.getPeakMassSpectrum(extractedIonSignals, chromatogram, totalIonSignals, backgroundEquation, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetPeakIntensityValues_7() {

		try {
			PeakBuilderMSD.getPeakMassSpectrum(extractedIonSignals, chromatogram, totalIonSignals, backgroundEquation, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
