/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.jcampdx.io;

import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.jcampdx.model.IVendorScan;

public class ChromatogramReader_1_ITest extends ChromatogramReaderTestCase {

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1);
		super.setUp();
	}

	public void testImport_1() {

		assertNotNull(chromatogram);
	}

	public void testImport_2() {

		int scans = chromatogram.getNumberOfScans();
		assertEquals(236, scans);
	}

	public void testImport_Scan_1() {

		IVendorScan massSpectrum = (IVendorScan)chromatogram.getScan(1);
		assertEquals(1780, massSpectrum.getRetentionTime());
		assertEquals(4609.0f, massSpectrum.getTotalSignal());
		assertEquals(3, massSpectrum.getNumberOfIons());
		assertEquals("low m/z", 40.01681d, massSpectrum.getLowestIon().getIon());
		assertEquals("high m/z", 44.05768d, massSpectrum.getHighestIon().getIon());
		assertEquals("low abundance", 44.05768d, massSpectrum.getLowestAbundance().getIon());
		assertEquals("high abundance", 40.01681d, massSpectrum.getHighestAbundance().getIon());
	}

	public void testImport_Scan_169() {

		IVendorScan massSpectrum = (IVendorScan)chromatogram.getScan(169);
		assertEquals(190290, massSpectrum.getRetentionTime());
		assertEquals(216013.0f, massSpectrum.getTotalSignal());
		assertEquals(37, massSpectrum.getNumberOfIons());
		assertEquals("low m/z", 39.06028d, massSpectrum.getLowestIon().getIon());
		assertEquals("high m/z", 166.34151d, massSpectrum.getHighestIon().getIon());
		assertEquals("low abundance", 61.09515d, massSpectrum.getLowestAbundance().getIon());
		assertEquals("high abundance", 55.11506d, massSpectrum.getHighestAbundance().getIon());
	}

	public void testImport_Scan_236() {

		IVendorScan massSpectrum = (IVendorScan)chromatogram.getScan(236);
		assertEquals(265460, massSpectrum.getRetentionTime());
		assertEquals(68633.0f, massSpectrum.getTotalSignal());
		assertEquals(20, massSpectrum.getNumberOfIons());
		assertEquals("low m/z", 39.06919d, massSpectrum.getLowestIon().getIon());
		assertEquals("high m/z", 125.29008d, massSpectrum.getHighestIon().getIon());
		assertEquals("low abundance", 125.29008d, massSpectrum.getLowestAbundance().getIon());
		assertEquals("high abundance", 55.11167d, massSpectrum.getHighestAbundance().getIon());
	}
}
