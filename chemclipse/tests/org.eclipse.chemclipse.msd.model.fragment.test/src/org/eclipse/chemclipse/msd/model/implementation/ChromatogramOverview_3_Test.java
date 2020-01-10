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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * Test the interface IChromatogramOverview with 1 scan.
 * 
 * @author eselmeister
 */
public class ChromatogramOverview_3_Test extends TestCase {

	private ChromatogramMSD chrom;
	private IChromatogramOverview chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private ScanIon ion;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chrom = new ChromatogramMSD();
		// ------------------------------Scan 1 - 200
		for(int i = 1; i <= 200; i++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			ion = new ScanIon(IIon.TIC_ION, 4500.0f);
			supplierMassSpectrum.addIon(ion);
			chrom.addScan(supplierMassSpectrum);
		}
		// ------------------------------Scan 1 - 200
		chromatogram = chrom;
		chromatogram.setScanDelay(5500);
		chromatogram.setScanInterval(1500);
		chromatogram.recalculateRetentionTimes();
	}

	@Override
	protected void tearDown() throws Exception {

		chrom = null;
		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetScanNumber_1() {

		assertEquals("ScanNumber", 5, chromatogram.getScanNumber(12736));
	}

	public void testGetScanNumber_2() {

		assertEquals("ScanNumber", 1, chromatogram.getScanNumber(5501));
	}

	public void testGetScanNumber_3() {

		assertEquals("ScanNumber", 1, chromatogram.getScanNumber(5500));
	}

	public void testGetScanNumber_4() {

		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(5499));
	}

	public void testGetScanNumber_5() {

		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(0));
	}

	public void testGetScanNumber_6() {

		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(-1));
	}

	public void testGetScanNumber_7() {

		assertEquals("stopRetentionTime", 304000, chromatogram.getStopRetentionTime());
		assertEquals("NumberOfScans", 200, chromatogram.getNumberOfScans());
		assertEquals("ScanNumber", 200, chromatogram.getScanNumber(304000));
	}

	public void testGetScanNumber_9() {

		assertEquals("ScanNumber", 81, chromatogram.getScanNumber(125637));
	}

	public void testGetScanNumber_10() {

		assertEquals("ScanNumber", 142, chromatogram.getScanNumber(218499));
	}

	public void testGetScanNumber_11() {

		assertEquals("ScanNumber", 29, chromatogram.getScanNumber(48000));
	}

	public void testGetScanNumber_12() {

		assertEquals("ScanNumber", 196, chromatogram.getScanNumber(299000));
	}
}
