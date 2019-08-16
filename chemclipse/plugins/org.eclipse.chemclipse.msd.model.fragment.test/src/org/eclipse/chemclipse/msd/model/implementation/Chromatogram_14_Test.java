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

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * Add 100 scans and get an {@link ITotalScanSignals} object.
 * 
 * @author eselmeister
 */
public class Chromatogram_14_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private ScanIon ion;
	private ITotalScanSignalExtractor totalIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// ------------------------------Scan 1-100
		for(int i = 1; i <= 100; i++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(i);
			ion = new ScanIon(IIon.TIC_ION, i);
			supplierMassSpectrum.addIon(ion);
			chromatogram.addScan(supplierMassSpectrum);
		}
		// ------------------------------Scan 1-100
		//
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetNumberOfScans_1() {

		assertEquals("numberOfScans", 100, chromatogram.getNumberOfScans());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("startRetentionTime", 1, chromatogram.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("stopRetentionTime", 100, chromatogram.getStopRetentionTime());
	}

	public void testGetTotalIonSignals_1() {

		ITotalScanSignal signal;
		ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(20, 50);
		assertEquals("Size", 31, signals.size());
		assertEquals("startScan", 20, signals.getStartScan());
		assertEquals("startScan", 50, signals.getStopScan());
		signal = signals.getTotalScanSignal(20);
		assertEquals("Scan", 20, signal.getRetentionTime());
		signal = signals.getTotalScanSignal(50);
		assertEquals("Scan", 50, signal.getRetentionTime());
	}

	public void testGetTotalIonSignals_2() {

		ITotalScanSignal signal;
		ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(50, 20);
		assertEquals("Size", 31, signals.size());
		assertEquals("startScan", 20, signals.getStartScan());
		assertEquals("startScan", 50, signals.getStopScan());
		signal = signals.getTotalScanSignal(20);
		assertEquals("Scan", 20, signal.getRetentionTime());
		signal = signals.getTotalScanSignal(50);
		assertEquals("Scan", 50, signal.getRetentionTime());
	}
}
