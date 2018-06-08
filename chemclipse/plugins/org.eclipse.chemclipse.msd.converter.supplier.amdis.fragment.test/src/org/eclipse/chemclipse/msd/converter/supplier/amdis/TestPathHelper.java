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
package org.eclipse.chemclipse.msd.converter.supplier.amdis;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS A TESTCLASS!
 * 
 * @author eselmeister
 */
public class TestPathHelper extends PathResolver {

	/*
	 * IMPORT MSL
	 */
	public static final String TESTFILE_IMPORT_TEST = "testData/files/import/msl/Test.msl";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM = "testData/files/import/msl/Chromtogram.msl";
	public static final String TESTFILE_IMPORT_EMPTY = "testData/files/import/msl/Empty.msl";
	public static final String TESTFILE_IMPORT_NOT_READABLE = "testData/files/import/msl/NotReadable.msl";
	public static final String TESTFILE_IMPORT_DB_1 = "testData/files/import/msl/DB1.msl";
	public static final String TESTFILE_IMPORT_DB_2 = "testData/files/import/msl/DB2.msl";
	public static final String TESTFILE_IMPORT_DB_3 = "testData/files/import/msl/DB3.msl";
	public static final String TESTFILE_IMPORT_GOLMDB_TEST_MSL = "testData/files/import/msl/GolmDB-Test.MSL";
	/*
	 * IMPORT MSP
	 */
	public static final String TESTFILE_IMPORT_PEAKS_1_MSP = "testData/files/import/msp/Peaks1.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_2_MSP = "testData/files/import/msp/Peaks2.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_3_MSP = "testData/files/import/msp/Peaks3.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_4_MSP = "testData/files/import/msp/Peaks4.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_5_MSP = "testData/files/import/msp/Peaks5.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_6_MSP = "testData/files/import/msp/Peaks6.MSP";
	public static final String TESTFILE_IMPORT_PEAKS_7_MSP = "testData/files/import/msp/Peaks7.MSP";
	public static final String TESTFILE_IMPORT_LIB_1_MSP = "testData/files/import/msp/Lib1.msp";
	public static final String TESTFILE_IMPORT_LIB_2_MSP = "testData/files/import/msp/Lib2.msp";
	public static final String TESTFILE_IMPORT_LIB_3_MSP = "testData/files/import/msp/Lib3.MSP";
	public static final String TESTFILE_IMPORT_LIB_4_MSP = "testData/files/import/msp/Lib4.MSP";
	public static final String TESTFILE_IMPORT_SYNONYMS = "testData/files/import/msp/Synonyms.MSP";
	public static final String TESTFILE_IMPORT_GOLMDB_TEST_MSP = "testData/files/import/msp/GolmDB-Test.MSP";
	/*
	 * IMPORT ELU
	 */
	public static final String TESTFILE_IMPORT_PEAKS_1_ELU = "testData/files/import/elu/Peaks1.ELU";
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
	public static final String VALIDATOR_TEST_SPEC_MSL = "testData/files/import/msl/MASSSPECTRA.msl";
	public static final String VALIDATOR_TEST_MSL_1 = "testData/files/import/msl/MASSSPECTRA.msl";
	public static final String VALIDATOR_TEST_MSL_2 = "testData/files/import/msl/MASSSPECTRA";
	public static final String VALIDATOR_TEST_MSL_3 = "testData/files/import/msl";
	/*
	 * SPECIFICATION VALIDATOR TEST MSP
	 */
	public static final String VALIDATOR_TEST_SPEC_MSP = "testData/files/import/msp/MASSSPECTRA.MSP";
	public static final String VALIDATOR_TEST_MSP_1 = "testData/files/import/msp/MASSSPECTRA.MSP";
	public static final String VALIDATOR_TEST_MSP_2 = "testData/files/import/msp/MASSSPECTRA";
	public static final String VALIDATOR_TEST_MSP_3 = "testData/files/import/msp";
}
