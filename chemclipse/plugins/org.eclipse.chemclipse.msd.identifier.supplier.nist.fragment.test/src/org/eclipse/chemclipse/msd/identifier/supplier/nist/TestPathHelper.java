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
package org.eclipse.chemclipse.msd.identifier.supplier.nist;

import java.io.File;

import org.eclipse.chemclipse.logging.support.Settings;

/**
 * THIS CLASS IS NOT SUITED FOR PRODUCTIVE USE!<br/>
 * IT IS A TESTCLASS!
 * 
 * @author eselmeister
 */
public class TestPathHelper extends PathResolver {

	/*
	 * NIST Result file
	 */
	public static final String TESTFILE_NIST_SRCRESLT_1 = "testData/files/nist/SRCRESLT-1.TXT";
	public static final String TESTFILE_NIST_SRCRESLT_2 = "testData/files/nist/SRCRESLT-2.TXT";
	public static final String TESTFILE_NIST_SRCRESLT_3 = "testData/files/nist/SRCRESLT-3.TXT";
	public static final String TESTFILE_NIST_SRCRESLT_4 = "testData/files/nist/SRCRESLT-4.TXT";
	public static final String TESTFILE_NIST_SRCRESLT_5 = "testData/files/nist/SRCRESLT-5.TXT";
	/*
	 * WINDOWS
	 */
	public static final String TESTFILE_WINDOWS_NIST_APPLICATION = "testData/files/windows/programs/nist/nistms$.exe";
	public static final String TESTFILE_WINDOWS_NISTLOG = "testData/files/windows/programs/nist/NISTLOG.TXT";
	public static final String TESTFILE_WINDOWS_SRCREADY = "testData/files/windows/programs/nist/SRCREADY.TXT";
	public static final String TESTFILE_WINDOWS_SRCRESLT = "testData/files/windows/programs/nist/SRCRESLT.TXT";
	public static final String TESTFILE_WINDOWS_AUTOIMP = "testData/files/windows/programs/nist/AUTOIMP.MSD";
	public static final String TESTFILE_WINDOWS_NIST_SETTINGS = "testData/files/windows/programs/nist/nistms.INI";
	/*
	 * WINE DOSDEVICES
	 */
	public static final String TESTFILE_WINE_DOSDEVICES_NIST_APPLICATION = "testData/files/wine/dosdevices/c/programs/nist/nistms$.exe";
	public static final String TESTFILE_WINE_DOSDEVICES_NISTLOG = "testData/files/wine/dosdevices/c/programs/nist/NISTLOG.TXT";
	public static final String TESTFILE_WINE_DOSDEVICES_SRCREADY = "testData/files/wine/dosdevices/c/programs/nist/SRCREADY.TXT";
	public static final String TESTFILE_WINE_DOSDEVICES_SRCRESLT = "testData/files/wine/dosdevices/c/programs/nist/SRCRESLT.TXT";
	public static final String TESTFILE_WINE_DOSDEVICES_AUTOIMP = "testData/files/wine/dosdevices/c/programs/nist/AUTOIMP.MSD";
	public static final String TESTFILE_WINE_DOSDEVICES_NIST_SETTINGS = "testData/files/wine/dosdevices/c/programs/nist/nistms.INI";
	/*
	 * WINE DRIVE
	 */
	public static final String TESTFILE_WINE_DRIVE_NIST_APPLICATION = "testData/files/wine/drive_c/programs/nist/nistms$.exe";
	public static final String TESTFILE_WINE_DRIVE_NISTLOG = "testData/files/wine/drive_c/programs/nist/NISTLOG.TXT";
	public static final String TESTFILE_WINE_DRIVE_SRCREADY = "testData/files/wine/drive_c/programs/nist/SRCREADY.TXT";
	public static final String TESTFILE_WINE_DRIVE_SRCRESLT = "testData/files/wine/drive_c/programs/nist/SRCRESLT.TXT";
	public static final String TESTFILE_WINE_DRIVE_AUTOIMP = "testData/files/wine/drive_c/programs/nist/AUTOIMP.MSD";
	public static final String TESTFILE_WINE_DRIVE_NIST_SETTINGS = "testData/files/wine/drive_c/programs/nist/nistms.INI";
	public static final String TESTDIR_WINE = "testData/files/wine";
	/*
	 * Wine binary
	 */
	public static final String TEST_WINE_BINARY_ROOT_DIR = "testData/files/wine/Applications";

	/*
	 * Storage path test!
	 */
	public static String getStoragePath() {

		return System.getProperty("user.home") + File.separator + "." + Settings.getApplicationName().toLowerCase() + "/" + Settings.getVersionIdentifier() + "/org.eclipse.chemclipse.msd.identifier.supplier.nist";
	}
}
