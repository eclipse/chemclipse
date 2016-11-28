/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis;

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
	public static final String TESTFILE_IMPORT_TEST = "testData/files/import/Test.msl";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM = "testData/files/import/Chromtogram.msl";
	public static final String TESTFILE_IMPORT_EMPTY = "testData/files/import/Empty.msl";
	public static final String TESTFILE_IMPORT_NOT_READABLE = "testData/files/import/NotReadable.msl";
	public static final String TESTFILE_IMPORT_PEAKS_1_ELU = "testData/files/import/Peaks1.ELU";
	public static final String TESTFILE_IMPORT_SYNONYMS = "testData/files/import/Synonyms.MSP";
	public static final String TESTFILE_IMPORT_DB_1 = "testData/files/import/DB1.msl";
	public static final String TESTFILE_IMPORT_DB_2 = "testData/files/import/DB2.msl";
	public static final String TESTFILE_IMPORT_DB_3 = "testData/files/import/DB3.msl";
	public static final String TESTFILE_IMPORT_PEAKS_1_MSP = "testData/files/import/Peaks1.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_2_MSP = "testData/files/import/Peaks2.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_3_MSP = "testData/files/import/Peaks3.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_4_MSP = "testData/files/import/Peaks4.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_5_MSP = "testData/files/import/Peaks5.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_6_MSP = "testData/files/import/Peaks6.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_7_MSP = "testData/files/import/Peaks7.MSP";
	public static final String TESTFILE_IMPORT_LIB_1_MSP = "testData/files/import/Lib1.msp";
	public static final String TESTFILE_IMPORT_LIB_2_MSP = "testData/files/import/Lib2.msp";
	/*
	 * EXPORT
	 */
	public static final String TESTDIR_EXPORT = "testData/files/export";
	public static final String TESTFILE_EXPORT_TEST_MSL = TESTDIR_EXPORT + "/Test.msl";
	public static final String TESTFILE_EXPORT_DB_MSL = "DB1.msl";
	public static final String TESTFILE_EXPORT_TEST_MSP = TESTDIR_EXPORT + "/Test.msp";
	public static final String TESTFILE_EXPORT_DB_MSP = TESTDIR_EXPORT + "/DB.msp";
	public static final String TESTFILE_EXPORT_DB_MSL_IDENTIFIER = "DB-Identifier.msl";
	/*
	 * SPECIFICATION VALIDATOR TEST MSL
	 */
	public static final String VALIDATOR_TEST_SPEC_MSL = "testData/files/import/MASSSPECTRA.msl";
	public static final String VALIDATOR_TEST_MSL_1 = "testData/files/import/MASSSPECTRA.msl";
	public static final String VALIDATOR_TEST_MSL_2 = "testData/files/import/MASSSPECTRA";
	public static final String VALIDATOR_TEST_MSL_3 = "testData/files/import";
	/*
	 * SPECIFICATION VALIDATOR TEST MSP
	 */
	public static final String VALIDATOR_TEST_SPEC_MSP = "testData/files/import/MASSSPECTRA.MSP";
	public static final String VALIDATOR_TEST_MSP_1 = "testData/files/import/MASSSPECTRA.MSP";
	public static final String VALIDATOR_TEST_MSP_2 = "testData/files/import/MASSSPECTRA";
	public static final String VALIDATOR_TEST_MSP_3 = "testData/files/import";
}
