/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.io.File;

import org.eclipse.chemclipse.msd.model.TestPathHelper;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;

import junit.framework.TestCase;

public class AbstractChromatogram_2_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private String nameDefault = "default";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testExtractNameFromFile_1() {

		chromatogram.setFile(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_FILE_10102040_CDF)));
		assertEquals("10.102040", chromatogram.extractNameFromFile(nameDefault));
	}

	public void testExtractNameFromFile_2() {

		chromatogram.setFile(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_FILE_102040_MZXML)));
		assertEquals("102040", chromatogram.extractNameFromFile(nameDefault));
	}

	public void testExtractNameFromFile_3() {

		chromatogram.setFile(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_FILE_2012102040_SMS)));
		assertEquals("2012.102040", chromatogram.extractNameFromFile(nameDefault));
	}

	public void testExtractNameFromFile_4() {

		chromatogram.setFile(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_FILE_201220102078_SMS)));
		assertEquals("2012.20.102078", chromatogram.extractNameFromFile(nameDefault));
	}

	public void testExtractNameFromFile_5() {

		chromatogram.setFile(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_FILE_POLYETHYLENE_CDF)));
		assertEquals("polyethylene", chromatogram.extractNameFromFile(nameDefault));
	}

	public void testExtractNameFromFile_6() {

		chromatogram.setFile(new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_FILE_POLYETHYLENE_TEST)));
		assertEquals(nameDefault, chromatogram.extractNameFromFile(nameDefault));
	}
}
