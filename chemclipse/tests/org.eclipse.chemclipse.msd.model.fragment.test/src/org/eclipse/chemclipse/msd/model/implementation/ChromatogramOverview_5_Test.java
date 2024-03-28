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
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * Test the interface IChromatogramOverview with 1 scan.
 * 
 * @author eselmeister
 */
public class ChromatogramOverview_5_Test extends TestCase {

	private ChromatogramMSD chrom;
	private IChromatogramOverview chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private IIon ion;
	private float RT_FACTOR = 1000.0f * 60.0f;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chrom = new ChromatogramMSD();
		// ------------------------------Scan 1 - 200
		for(int i = 1; i <= 200; i++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			ion = new Ion(IIon.TIC_ION, 4500.0f);
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

		float min = 12736 / RT_FACTOR;
		assertEquals("ScanNumber", 5, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_2() {

		float min = 5501 / RT_FACTOR;
		assertEquals("ScanNumber", 1, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_3() {

		float min = 5500 / RT_FACTOR;
		assertEquals("ScanNumber", 1, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_4() {

		float min = 5499 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_5() {

		float min = 0 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_6() {

		float min = -1 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_7() {

		assertEquals("stopRetentionTime", 304000, chromatogram.getStopRetentionTime());
		assertEquals("NumberOfScans", 200, chromatogram.getNumberOfScans());
		float min = 304000 / RT_FACTOR;
		assertEquals("ScanNumber", 200, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_9() {

		float min = 125637 / RT_FACTOR;
		assertEquals("ScanNumber", 81, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_10() {

		float min = 218499 / RT_FACTOR;
		assertEquals("ScanNumber", 142, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_11() {

		float min = 48000 / RT_FACTOR;
		assertEquals("ScanNumber", 29, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_12() {

		float min = 299000 / RT_FACTOR;
		assertEquals("ScanNumber", 196, chromatogram.getScanNumber(min));
	}
}
