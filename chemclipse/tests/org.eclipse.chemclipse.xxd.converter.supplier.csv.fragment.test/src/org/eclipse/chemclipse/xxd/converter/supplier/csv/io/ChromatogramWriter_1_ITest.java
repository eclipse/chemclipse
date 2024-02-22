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

import org.eclipse.chemclipse.xxd.converter.supplier.csv.TestPathHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.csv.io.core.ChromatogramWriter;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ChromatogramWriter_1_ITest extends ChromatogramReaderTestCase {

	private ChromatogramWriter chromatogramWriter;

	@Override
	protected void setUp() throws Exception {

		pathImport = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM_1);
		chromatogramWriter = new ChromatogramWriter();
		super.setUp();
	}

	public void testExport_1() {

		try {
			File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.DIRECTORY_EXPORT_TEST) + File.separator + "Test.csv");
			chromatogramWriter.writeChromatogram(file, chromatogram, new NullProgressMonitor());
			file.delete();
			assertTrue(true);
		} catch(Exception e) {
			assertTrue("Exception", false);
		}
	}
}