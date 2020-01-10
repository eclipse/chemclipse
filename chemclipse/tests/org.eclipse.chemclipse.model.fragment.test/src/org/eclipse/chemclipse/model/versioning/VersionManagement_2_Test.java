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
package org.eclipse.chemclipse.model.versioning;

import java.io.File;

import org.eclipse.chemclipse.model.versioning.IVersionManagement;
import org.eclipse.chemclipse.model.versioning.PathHelper;
import org.eclipse.chemclipse.model.versioning.VersionManagement;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

import junit.framework.TestCase;

/**
 * This TestCase tests the state of VersionManagement after the initial state
 * and the following operations:<br/>
 * doOperation(): _r1
 * 
 * @author eselmeister
 */
public class VersionManagement_2_Test extends TestCase {

	private IVersionManagement versionManagement;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		versionManagement = new VersionManagement();
		versionManagement.doOperation();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		versionManagement = null;
	}

	public void testStorageDirectory() {

		/*
		 * Test the storage path cases.<br/>The time id could differ in the last
		 * digits so leave the last digits out of view.<br/><br/> test =
		 * /home/eselmeister
		 * /.chemclipse/org.eclipse.chemclipse.msd.model/serializedChromatograms
		 * /chromatogram@1b2e165_12246569375[02]<br/> actual =
		 * /home/eselmeister/
		 * .chemclipse/org.eclipse.chemclipse.msd.model/serializedChromatograms
		 * /chromatogram@1b2e165_12246569375[03]<br/>
		 */
		File storagePath = new File(ApplicationSettings.getSettingsDirectory() + File.separator + PathHelper.CHROMATOGRAM_MODELS + File.separator + PathHelper.SERIALIZED_CHROMATOGRAMS + File.separator + versionManagement.getChromatogramIdentifier());
		String test = storagePath.getAbsolutePath();
		String actual = versionManagement.getStorageDirectory().getAbsolutePath();
		assertTrue("storageDirectory: " + actual, test.regionMatches(0, actual, 0, actual.length() - 2));
	}

	public void testGetActualScanRevision_1() {

		String absolutePath = versionManagement.getStorageDirectory().getAbsolutePath() + File.separator + "scans" + File.separator + "dumpScans";
		String revisionPath = absolutePath + "_r1";
		assertEquals("getActualScanRevision", revisionPath, versionManagement.getActualScanRevision().getAbsolutePath());
	}

	public void testGetNextScanRevision_1() {

		String absolutePath = versionManagement.getStorageDirectory().getAbsolutePath() + File.separator + "scans" + File.separator + "dumpScans";
		String revisionPath = absolutePath + "_r2";
		assertEquals("getNextScanRevision", revisionPath, versionManagement.getNextScanRevision().getAbsolutePath());
	}

	public void testGetPreviousScanRevision_1() {

		String absolutePath = versionManagement.getStorageDirectory().getAbsolutePath() + File.separator + "scans" + File.separator + "dumpScans";
		String revisionPath = absolutePath + "_r0";
		assertEquals("getPreviousScanRevision", revisionPath, versionManagement.getPreviousScanRevision().getAbsolutePath());
	}
}
