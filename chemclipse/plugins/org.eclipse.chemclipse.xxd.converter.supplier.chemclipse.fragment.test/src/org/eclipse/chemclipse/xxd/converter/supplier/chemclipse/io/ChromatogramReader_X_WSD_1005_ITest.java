/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.io;

import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.TestPathHelper;

public class ChromatogramReader_X_WSD_1005_ITest extends ChromatogramReaderWSDTestCase {

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_X_WSD_1005);
		super.setUp();
	}

	public void testReader_1() {

		assertEquals(4950, chromatogram.getNumberOfScans());
	}

	public void testReader_2() {

		assertEquals("Chromatogram", chromatogram.getName());
	}

	public void testReader_3() {

		assertEquals(325, chromatogram.getStartRetentionTime());
	}

	public void testReader_4() {

		assertEquals(1979925, chromatogram.getStopRetentionTime());
	}

	public void testReader_5() {

		assertEquals(910011.0f, chromatogram.getMaxSignal());
	}

	public void testReader_6() {

		assertEquals(534.0f, chromatogram.getMinSignal());
	}

	public void testReader_7() {

		assertEquals(4500, chromatogram.getScanDelay());
	}

	public void testReader_8() {

		assertEquals(1000, chromatogram.getScanInterval());
	}

	public void testReader_9() {

		IScanWSD supplierScan = chromatogram.getSupplierScan(1);
		//
		assertEquals(325, supplierScan.getRetentionTime());
		assertEquals(0.0f, supplierScan.getRetentionIndex());
		assertEquals(534.0f, supplierScan.getTotalSignal());
	}

	public void testReader_10() {

		IScanWSD supplierScan = chromatogram.getSupplierScan(680);
		//
		assertEquals(271925, supplierScan.getRetentionTime());
		assertEquals(0.0f, supplierScan.getRetentionIndex());
		assertEquals(118465.0f, supplierScan.getTotalSignal());
	}

	public void testReader_11() {

		IScanWSD supplierScan = chromatogram.getSupplierScan(2354);
		//
		assertEquals(941525, supplierScan.getRetentionTime());
		assertEquals(0.0f, supplierScan.getRetentionIndex());
		assertEquals(84836.0f, supplierScan.getTotalSignal());
	}

	public void testReader_12() {

		IScanWSD supplierScan = chromatogram.getSupplierScan(4950);
		//
		assertEquals(1979925, supplierScan.getRetentionTime());
		assertEquals(0.0f, supplierScan.getRetentionIndex());
		assertEquals(28888.0f, supplierScan.getTotalSignal());
	}
}
