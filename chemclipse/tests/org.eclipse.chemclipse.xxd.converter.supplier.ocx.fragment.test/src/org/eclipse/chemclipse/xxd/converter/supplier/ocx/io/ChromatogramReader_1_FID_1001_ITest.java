/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.io;

import org.eclipse.chemclipse.xxd.converter.supplier.ocx.TestPathHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences.PreferenceSupplier;

public class ChromatogramReader_1_FID_1001_ITest extends ChromatogramReaderFIDTestCase {

	@Override
	protected void setUp() throws Exception {

		PreferenceSupplier.setForceLoadAlternateDetector(true);
		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1001);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		PreferenceSupplier.setForceLoadAlternateDetector(false);
	}

	public void testReader_1() {

		assertEquals(110, chromatogram.getNumberOfScans());
	}

	public void testReader_2() {

		assertEquals("Chromatogram1-1001-fromMSD", chromatogram.getName());
	}

	public void testReader_3() {

		assertEquals(841111, chromatogram.getStartRetentionTime());
	}

	public void testReader_4() {

		assertEquals(918652, chromatogram.getStopRetentionTime());
	}

	public void testReader_5() {

		assertEquals(442733.0f, chromatogram.getMaxSignal());
	}

	public void testReader_6() {

		assertEquals(21543.0f, chromatogram.getMinSignal());
	}

	public void testReader_7() {

		assertEquals(841111, chromatogram.getScanDelay());
	}

	public void testReader_8() {

		assertEquals(8351, chromatogram.getScanInterval());
	}
}
