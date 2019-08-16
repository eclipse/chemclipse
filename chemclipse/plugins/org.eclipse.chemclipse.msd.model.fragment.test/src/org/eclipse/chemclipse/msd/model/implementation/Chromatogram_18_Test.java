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

import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

/**
 * Add 100 scans and get an {@link ITotalScanSignals} object.
 * 
 * @author eselmeister
 */
public class Chromatogram_18_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum supplierMassSpectrum;
	private IScanIon ion;
	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;
	private ChromatogramSelectionMSD chromatogramSelection;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// ------------------------------Scan 1-100
		for(int i = 1; i <= 100; i++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(i);
			for(int j = 1; j <= 50; j++) {
				ion = new ScanIon(j, i * j);
				supplierMassSpectrum.addIon(ion);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		// ------------------------------Scan 1-100
		//
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
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

	public void testGetExtractedIonSignals_1() {

		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(20, 50);
		assertEquals("StartScan", 20, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 50, extractedIonSignals.getStopScan());
		int scan;
		scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 25500.0f, extractedIonSignal.getTotalSignal());
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 63750.0f, extractedIonSignal.getTotalSignal());
	}

	public void testGetExtractedIonSignals_2() {

		/*
		 * Retention time and scan # are identical in this special case.
		 */
		try {
			chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
			chromatogramSelection.setStartRetentionTime(20);
			chromatogramSelection.setStopRetentionTime(50);
		} catch(ChromatogramIsNullException e1) {
			assertTrue("ChromatogramIsNullException", false);
		}
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
		assertEquals("StartScan", 20, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 50, extractedIonSignals.getStopScan());
		int scan;
		scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 25500.0f, extractedIonSignal.getTotalSignal());
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 63750.0f, extractedIonSignal.getTotalSignal());
	}

	public void testGetExtractedIonSignals_3() {

		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(50, 20);
		assertEquals("StartScan", 20, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 50, extractedIonSignals.getStopScan());
		int scan;
		scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 25500.0f, extractedIonSignal.getTotalSignal());
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 63750.0f, extractedIonSignal.getTotalSignal());
	}

	public void testGetExtractedIonSignals_4() {

		/*
		 * Retention time and scan # are identical in this special case.
		 */
		try {
			chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
			chromatogramSelection.setStartRetentionTime(50);
			chromatogramSelection.setStopRetentionTime(20);
		} catch(ChromatogramIsNullException e1) {
			assertTrue("ChromatogramIsNullException", false);
		}
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
		assertEquals("StartScan", 50, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 100, extractedIonSignals.getStopScan());
		int scan;
		scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 63750.0f, extractedIonSignal.getTotalSignal());
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 127500.0f, extractedIonSignal.getTotalSignal());
	}

	public void testGetExtractedIonSignals_5() {

		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(0, 180);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
		int scan;
		scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
	}

	public void testGetExtractedIonSignals_6() {

		/*
		 * Retention time and scan # are identical in this special case.
		 */
		try {
			chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
			chromatogramSelection.setStartRetentionTime(0);
			chromatogramSelection.setStopRetentionTime(180);
			assertEquals("StartRetentionTime", 1, chromatogramSelection.getStartRetentionTime());
			assertEquals("StopRetentionTime", 100, chromatogramSelection.getStopRetentionTime());
		} catch(ChromatogramIsNullException e1) {
			assertTrue("ChromatogramIsNullException", false);
		}
		extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
		assertEquals("StartScan", 1, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 100, extractedIonSignals.getStopScan());
		int scan;
		scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 1275.0f, extractedIonSignal.getTotalSignal());
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("TotalSignal", 127500.0f, extractedIonSignal.getTotalSignal());
	}
}
