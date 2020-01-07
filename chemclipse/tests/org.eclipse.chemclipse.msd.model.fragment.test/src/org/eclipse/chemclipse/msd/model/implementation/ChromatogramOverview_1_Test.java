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
package org.eclipse.chemclipse.msd.model.implementation;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.versioning.PathHelper;
import org.eclipse.chemclipse.model.versioning.VersionManagement;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;

import junit.framework.TestCase;

/**
 * Test the interface IChromatogramOverview.
 * 
 * @author eselmeister
 */
public class ChromatogramOverview_1_Test extends TestCase {

	private ChromatogramMSD chrom;
	private IChromatogramOverview chromatogram;
	private Date date;
	private ITotalScanSignalExtractor totalIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chrom = new ChromatogramMSD();
		date = new Date();
		chromatogram = chrom;
		//
		totalIonSignalExtractor = new TotalScanSignalExtractor(chrom);
	}

	@Override
	protected void tearDown() throws Exception {

		chrom = null;
		date = null;
		super.tearDown();
	}

	public void testGetIdentifier_1() {

		/*
		 * chromatogram@[116f2e6]_[1226516556244]
		 * chromatogram@[1cfb84c]_[1226516556243]
		 */
		String input = chromatogram.getIdentifier();
		String regex = "(chromatogram@)+[0-9a-f]*(_)[0-9]*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		assertTrue("getIdentifier: " + input, matcher.find());
	}

	public void testGetStorageDirectory_1() {

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
		VersionManagement versionManagement = new VersionManagement();
		File storagePath = new File(ApplicationSettings.getSettingsDirectory() + File.separator + PathHelper.CHROMATOGRAM_MODELS + File.separator + PathHelper.SERIALIZED_CHROMATOGRAMS + File.separator + versionManagement.getChromatogramIdentifier());
		String test = storagePath.getAbsolutePath();
		String actual = versionManagement.getStorageDirectory().getAbsolutePath();
		assertTrue("storageDirectory: " + actual, test.regionMatches(0, actual, 0, actual.length() - 2));
	}

	public void testGetMinSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("minSignal", 0, (int)chromatogram.getMinSignal());
	}

	public void testGetMaxSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("maxSignal", 0, (int)chromatogram.getMaxSignal());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("startRetentionTime", 0, chromatogram.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("stopRetentionTime", 0, chromatogram.getStopRetentionTime());
	}

	public void testGetScanDelay_1() {

		assertEquals("scanDelay", 4500, chromatogram.getScanDelay());
	}

	public void testGetScanInterval_1() {

		assertEquals("scanInterval", 1000, chromatogram.getScanInterval());
	}

	public void testGetNumberOfScans_1() {

		assertEquals("numberOfScans", 0, chromatogram.getNumberOfScans());
	}

	public void testGetName_1() {

		assertEquals("name", "Chromatogram", chromatogram.getName());
	}

	public void testGetFile_1() {

		assertEquals("file", null, chromatogram.getFile());
	}

	public void testGetOperator_1() {

		assertEquals("operator", "", chromatogram.getOperator());
	}

	public void testGetDate_1() {

		assertTrue("date", chromatogram.getDate() != null);
	}

	public void testGetDate_2() {

		chromatogram.setDate(date);
		assertEquals("date", date.toString(), chromatogram.getDate().toString());
	}

	public void testGetMiscInfo_1() {

		assertEquals("miscInfo", "", chromatogram.getMiscInfo());
	}

	public void testGetTotalIonSignals_1() {

		ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals();
		assertEquals("List<ITotalIonSignal> size", 0, signals.size());
	}

	public void testGetTotalIonSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("totalIonSignal", 0, (int)chromatogram.getTotalSignal());
	}
}
