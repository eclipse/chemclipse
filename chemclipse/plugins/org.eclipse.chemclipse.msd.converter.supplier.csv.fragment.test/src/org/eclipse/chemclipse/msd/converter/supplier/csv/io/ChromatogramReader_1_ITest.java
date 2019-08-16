/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.io;

import org.eclipse.chemclipse.msd.converter.supplier.csv.TestPathHelper;

public class ChromatogramReader_1_ITest extends ChromatogramWriterTestCase {

	@Override
	protected void setUp() throws Exception {

		/*
		 * Import
		 */
		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1);
		extensionPointImport = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
		/*
		 * Export/Reimport
		 */
		pathExport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_EXPORT_TEST);
		extensionPointExportReimport = "org.eclipse.chemclipse.msd.converter.supplier.csv";
		super.setUp();
	}

	public void testReimport_1() {

		assertNotNull(chromatogram);
	}

	public void testReimport_2() {

		assertEquals(5726, chromatogram.getNumberOfScans());
	}
}
