/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IIon;

import junit.framework.TestCase;

/**
 * Test the interface IChromatogramOverview with 1 scan.
 * 
 * @author eselmeister
 */
public class ChromatogramOverview_2_Test extends TestCase {

	private ChromatogramMSD chrom;
	private IChromatogramOverview chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private IIon ion;
	private Date date;
	private ITotalScanSignalExtractor totalIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chrom = new ChromatogramMSD();
		// ------------------------------Scan 1
		supplierMassSpectrum = new VendorMassSpectrum();
		supplierMassSpectrum.setRetentionTime(7896);
		ion = new Ion(IIon.TIC_ION, 470746.9f);
		supplierMassSpectrum.addIon(ion);
		chrom.addScan(supplierMassSpectrum);
		// ------------------------------Scan 1
		chromatogram = chrom;
		date = new Date();
		chromatogram.setDate(date);
		chromatogram.setFile(new File(""));
		chromatogram.setMiscInfo("This is a test chromatogram.");
		chromatogram.setOperator("eselmeister");
		chromatogram.setScanDelay(5500);
		chromatogram.setScanInterval(1500);
		//
		totalIonSignalExtractor = new TotalScanSignalExtractor(chrom);
	}

	@Override
	protected void tearDown() throws Exception {

		chrom = null;
		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		date = null;
		super.tearDown();
	}

	public void testGetMinSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("minSignal", 470746, (int)chromatogram.getMinSignal());
	}

	public void testGetMaxSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("maxSignal", 470746, (int)chromatogram.getMaxSignal());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("startRetentionTime", 7896, chromatogram.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("stopRetentionTime", 7896, chromatogram.getStopRetentionTime());
	}

	public void testGetScanDelay_1() {

		assertEquals("scanDelay", 5500, chromatogram.getScanDelay());
	}

	public void testGetScanInterval_1() {

		assertEquals("scanInterval", 1500, chromatogram.getScanInterval());
	}

	public void testGetNumberOfScans_1() {

		assertEquals("numberOfScans", 1, chromatogram.getNumberOfScans());
	}

	public void testGetName_1() {

		assertEquals("name", "", chromatogram.getName());
	}

	public void testGetFile_1() {

		assertEquals("file", new File(""), chromatogram.getFile());
	}

	public void testGetOperator_1() {

		assertEquals("operator", "eselmeister", chromatogram.getOperator());
	}

	public void testGetDate_1() {

		assertTrue("date", chromatogram.getDate() != null);
	}

	public void testGetDate_2() {

		assertEquals("date", date.toString(), chromatogram.getDate().toString());
	}

	public void testGetMiscInfo_1() {

		assertEquals("miscInfo", "This is a test chromatogram.", chromatogram.getMiscInfo());
	}

	public void testGetTotalIonSignals_1() {

		ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals();
		assertEquals("List<ITotalIonSignal> size", 1, signals.size());
	}

	public void testGetTotalIonSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("totalIonSignal", 470746, (int)chromatogram.getTotalSignal());
	}
}
