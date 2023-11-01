/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.io;

import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.TestPathHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences.PreferenceSupplier;

public class ChromatogramReader_4_DAD_1501_ITest extends ChromatogramReaderWSDTestCase {

	@Override
	protected void setUp() throws Exception {

		PreferenceSupplier.setForceLoadAlternateDetector(true);
		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_4_DAD_1501);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		PreferenceSupplier.setForceLoadAlternateDetector(false);
	}

	public void testReader_1() {

		assertEquals(1443, chromatogram.getNumberOfScans());
	}

	public void testReader_2() {

		assertEquals("Chromatogram4-1501", chromatogram.getName());
	}

	public void testReader_3() {

		assertEquals(510325, chromatogram.getStartRetentionTime());
	}

	public void testReader_4() {

		assertEquals(1087125, chromatogram.getStopRetentionTime());
	}

	public void testReader_5() {

		assertEquals(3.54757344E8f, chromatogram.getMaxSignal());
	}

	public void testReader_6() {

		assertEquals(4.2400872E7f, chromatogram.getMinSignal());
	}

	public void testReader_7() {

		assertEquals(510325, chromatogram.getScanDelay());
	}

	public void testReader_8() {

		assertEquals(753, chromatogram.getScanInterval());
	}

	public void testReader_9() {

		IScanWSD scan = chromatogram.getSupplierScan(1);
		//
		assertEquals(226, scan.getScanSignals().size());
		assertEquals(5.9459736E7f, scan.getTotalSignal());
	}

	public void testReader_10() {

		assertEquals(4, chromatogram.getNumberOfPeaks());
	}

	public void testReader_11() {

		assertEquals(4.057491060101555E10d, chromatogram.getPeakIntegratedArea());
	}

	public void testReader_12() {

		assertEquals(0.0d, chromatogram.getChromatogramIntegratedArea());
	}

	public void testReader_13() {

		assertEquals(0.0d, chromatogram.getBackgroundIntegratedArea());
	}

	public void testReader_14() {

		assertEquals(0.0d, chromatogram.getSampleWeight());
	}
}