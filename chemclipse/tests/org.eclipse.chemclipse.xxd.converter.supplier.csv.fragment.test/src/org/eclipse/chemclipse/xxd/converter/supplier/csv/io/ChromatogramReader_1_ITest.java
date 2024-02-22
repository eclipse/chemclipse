/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.csv.io;

import java.io.File;

import org.eclipse.chemclipse.model.settings.Delimiter;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.TestPathHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.preferences.PreferenceSupplier;

public class ChromatogramReader_1_ITest extends ChromatogramWriterTestCase {

	@Override
	protected void setUp() throws Exception {

		PreferenceSupplier.setImportDelimiter(Delimiter.COMMA);
		PreferenceSupplier.setImportZeroMarker("0.0");
		/*
		 * Import
		 */
		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1);
		extensionPointImport = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
		/*
		 * Export/Reimport
		 */
		pathExport = TestPathHelper.getAbsolutePath(TestPathHelper.DIRECTORY_EXPORT_TEST) + File.separator + "Test.csv";
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