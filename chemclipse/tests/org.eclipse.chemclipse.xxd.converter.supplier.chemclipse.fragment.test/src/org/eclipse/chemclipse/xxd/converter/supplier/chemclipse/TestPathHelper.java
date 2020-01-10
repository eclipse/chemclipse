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
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS A TESTCLASS!
 * 
 * @author eselmeister
 */
public class TestPathHelper extends PathResolver {

	/*
	 * IMPORT MSD
	 */
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0701 = "testData/files/import/Chromatogram1-0701.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0801 = "testData/files/import/Chromatogram1-0801.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0802 = "testData/files/import/Chromatogram1-0802.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0803 = "testData/files/import/Chromatogram1-0803.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0901 = "testData/files/import/Chromatogram1-0901.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0902 = "testData/files/import/Chromatogram1-0902.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_0903 = "testData/files/import/Chromatogram1-0903.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1001 = "testData/files/import/Chromatogram1-1001.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1002 = "testData/files/import/Chromatogram1-1002.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1003 = "testData/files/import/Chromatogram1-1003.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1004 = "testData/files/import/Chromatogram1-1004.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1005 = "testData/files/import/Chromatogram1-1005.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1006 = "testData/files/import/Chromatogram1-1006.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1007 = "testData/files/import/Chromatogram1-1007.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1100 = "testData/files/import/Chromatogram1-1100.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_1_MSD_1300 = "testData/files/import/Chromatogram1-1300.ocb";
	//
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0701 = "testData/files/import/Chromatogram2-0701.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0801 = "testData/files/import/Chromatogram2-0801.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0802 = "testData/files/import/Chromatogram2-0802.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0803 = "testData/files/import/Chromatogram2-0803.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0901 = "testData/files/import/Chromatogram2-0901.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0902 = "testData/files/import/Chromatogram2-0902.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_0903 = "testData/files/import/Chromatogram2-0903.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1001 = "testData/files/import/Chromatogram2-1001.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1002 = "testData/files/import/Chromatogram2-1002.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1003 = "testData/files/import/Chromatogram2-1003.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1004 = "testData/files/import/Chromatogram2-1004.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1005 = "testData/files/import/Chromatogram2-1005.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1006 = "testData/files/import/Chromatogram2-1006.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1007 = "testData/files/import/Chromatogram2-1007.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1100 = "testData/files/import/Chromatogram2-1100.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_2_MSD_1300 = "testData/files/import/Chromatogram2-1300.ocb";
	/*
	 * IMPORT FID
	 */
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1001 = "testData/files/import/Chromatogram3-1001.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1002 = "testData/files/import/Chromatogram3-1002.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1003 = "testData/files/import/Chromatogram3-1003.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1004 = "testData/files/import/Chromatogram3-1004.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1005 = "testData/files/import/Chromatogram3-1005.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1006 = "testData/files/import/Chromatogram3-1006.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1007 = "testData/files/import/Chromatogram3-1007.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1100 = "testData/files/import/Chromatogram3-1100.ocb";
	public static final String TESTFILE_IMPORT_CHROMATOGRAM_3_FID_1300 = "testData/files/import/Chromatogram3-1300.ocb";
	/*
	 * Import WSD
	 */
	/*
	 * EXPORT
	 */
}
