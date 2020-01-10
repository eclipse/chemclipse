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
package org.eclipse.chemclipse.msd.converter.supplier.openchromx;

import org.eclipse.chemclipse.msd.converter.supplier.openchromx.PathResolver;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS A TESTCLASS!
 * 
 * @author eselmeister
 */
public class TestPathHelper extends PathResolver {

	/*
	 * IMPORT
	 */
	public static final String TESTFILE_IMPORT_TEST = "testData/files/import/TEST.chrom";
	public static final String TESTFILE_IMPORT_EMPTY = "testData/files/import/EMPTY.chrom";
	public static final String TESTFILE_IMPORT_NOT_READABLE = "testData/files/import/NOT_READABLE.chrom";
	public static final String TESTFILE_IMPORT_TEST_LC = "testData/files/import/test_lc.chrom";
	public static final String TESTFILE_IMPORT_TEST_UC = "testData/files/import/TEST_UC.CHROM";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1 = "testData/files/import/Chromatogram1.chrom";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_AGILENT = "testData/files/import/Chromatogram1.D/DATA.MS";
	/*
	 * EXPORT
	 */
	public static final String TESTFILE_EXPORT_TEST = "testData/files/export/TEST.chrom";
	/*
	 * SPECIFICATION VALIDATOR TEST
	 */
	public static final String VALIDATOR_TEST_SPEC = "testData/files/import/CHROMATOGRAM.chrom";
	public static final String VALIDATOR_TEST_1 = "testData/files/import/CHROMATOGRAM.chrom";
	// Works not under Microsoft Windows.
	// public static final String VALIDATOR_TEST_2 =
	// "testData/files/import/CHROMATOGRAM.";
	public static final String VALIDATOR_TEST_3 = "testData/files/import/CHROMATOGRAM";
	public static final String VALIDATOR_TEST_4 = "testData/files/import";
}
