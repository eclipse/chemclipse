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

import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

/**
 * Test the interface IChromatogram with 1 scan.
 * 
 * @author eselmeister
 */
public class Chromatogram_4_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private ScanIon ion;
	private Date date;
	private ITotalScanSignalExtractor totalIonSignalExtractor;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// ------------------------------Scan 1
		supplierMassSpectrum = new VendorMassSpectrum();
		supplierMassSpectrum.setRetentionTime(7896);
		ion = new ScanIon(45.4f, 65883.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(104.3f, 102453.3f);
		supplierMassSpectrum.addIon(ion);
		ion = new ScanIon(86.2f, 302410.3f);
		supplierMassSpectrum.addIon(ion);
		chromatogram.addScan(supplierMassSpectrum);
		// ------------------------------Scan 1
		date = new Date();
		chromatogram.setDate(date);
		chromatogram.setFile(new File(""));
		chromatogram.setMiscInfo("This is a test chromatogram.");
		chromatogram.setOperator("eselmeister");
		chromatogram.setScanDelay(5500);
		chromatogram.setScanInterval(1500);
		//
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		date = null;
		super.tearDown();
	}

	public void testGetScanDelay_1() {

		assertEquals("scanDelay", 5500, chromatogram.getScanDelay());
	}

	public void testGetScanInterval_1() {

		assertEquals("scanInterval", 1500, chromatogram.getScanInterval());
	}

	public void testGetOperator_1() {

		assertEquals("operator", "eselmeister", chromatogram.getOperator());
	}

	public void testGetFile_1() {

		assertEquals("file", new File(""), chromatogram.getFile());
	}

	public void testGetName_1() {

		assertEquals("name", "", chromatogram.getName());
	}

	public void testGetDate_1() {

		assertTrue("date", chromatogram.getDate() != null);
	}

	public void testGetDate_2() {

		assertEquals("date", date.toString(), chromatogram.getDate().toString());
	}

	public void testGetNumberOfScans_1() {

		assertEquals("numberOfScans", 1, chromatogram.getNumberOfScans());
	}

	public void testGetNumberOfScanIons_1() {

		assertEquals("numberOfScanIons", 3, chromatogram.getNumberOfScanIons());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("startRetentionTime", 7896, chromatogram.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("stopRetentionTime", 7896, chromatogram.getStopRetentionTime());
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

	public void testGetExtractedIonSignals_1() {

		IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
		assertEquals("List<IExtractedIonSignal> size", 1, signals.size());
	}

	public void testGetExtractedIonSignals_2() {

		IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals(14.2f, 105.6f);
		assertEquals("List<IExtractedIonSignal> size", 1, signals.size());
	}

	public void testIsUndoable_1() {

		assertEquals("isUndoable", true, chromatogram.isUndoable());
	}
}
