/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.msd.identifier.supplier.nist.TestPathHelper;

public class WindowsSupport_1_ITest extends AbstractBackgroundTestCase {

	private IExtendedRuntimeSupport runtimeSupport;
	private String nistApplication;
	private String nistApplicationPath;
	private File testfileNistApplication;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		nistApplication = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_WINDOWS_NIST_APPLICATION);
		testfileNistApplication = new File(nistApplication);
		nistApplicationPath = testfileNistApplication.getParent();
		runtimeSupport = new WindowsSupport(testfileNistApplication.getParentFile(), parameterBackground);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetAutoimpFile_1() {

		String result = nistApplicationPath + File.separator + INistSupport.AUTOIMP_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getAutoimpFile());
	}

	public void testGetFilespecFile_1() {

		String result = TestPathHelper.getStoragePath() + File.separator + INistSupport.FILESPEC_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getFilespecFile());
	}

	public void testGetMassSpectraFile_1() {

		String result = TestPathHelper.getStoragePath() + File.separator + INistSupport.MASSSPECTRA_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getMassSpectraFile());
	}

	public void testGetNistlogFile_1() {

		String result = nistApplicationPath + File.separator + INistSupport.NISTLOG_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getNistlogFile());
	}

	public void testGetScrreadyFile_1() {

		String result = nistApplicationPath + File.separator + INistSupport.SRCREADY_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getSrcreadyFile());
	}

	public void testGetSrcresltFile_1() {

		String result = nistApplicationPath + File.separator + INistSupport.SRCRESLT_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getSrcresltFile());
	}

	public void testGetNistSettingsFile_1() {

		String result = nistApplicationPath + File.separator + INistSupport.NIST_SETTINGS_FILE;
		assertEquals(result, runtimeSupport.getNistSupport().getNISTSettingsFile());
	}

	public void testGetNistApplication_1() throws IOException {

		String result = nistApplicationPath + File.separator + "nistms.exe";
		assertEquals(result, runtimeSupport.getApplication());
	}

	public void testGetNistApplicationPath_1() {

		String result = nistApplicationPath;
		assertEquals(result, runtimeSupport.getApplicationPath());
	}

	public void testGetNistExecutable_1() {

		String result = "nistms.exe";
		assertEquals(result, runtimeSupport.getApplicationExecutable());
	}

	public void testIsValidNistExecutable_1() {

		assertTrue(runtimeSupport.isValidApplicationExecutable());
	}
}
