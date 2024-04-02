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

import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

import junit.framework.TestCase;

/**
 * Add 100 scans and remove number 51 to 70.
 * 
 * @author eselmeister
 */
public class Chromatogram_8_Test extends TestCase {

	private ChromatogramMSD chromatogram;
	private VendorMassSpectrum supplierMassSpectrum;
	private IIon ion;
	private ITotalScanSignalExtractor totalIonSignalExtractor;
	private IExtractedIonSignalExtractor extractedIonSignalExtractor;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// ------------------------------Scan 1-100
		for(int i = 1; i <= 100; i++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(i);
			ion = new Ion(IIon.TIC_ION, i);
			supplierMassSpectrum.addIon(ion);
			chromatogram.addScan(supplierMassSpectrum);
		}
		// ------------------------------Scan 1-100
		chromatogram.removeScans(51, 70);
		//
		totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
		extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		super.tearDown();
	}

	public void testGetNumberOfScans_1() {

		assertEquals("numberOfScans", 80, chromatogram.getNumberOfScans());
	}

	public void testGetNumberOfScanIons_1() {

		// 100 - 20
		assertEquals("numberOfScanIons", 80, chromatogram.getNumberOfScanIons());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("startRetentionTime", 1, chromatogram.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("stopRetentionTime", 100, chromatogram.getStopRetentionTime());
	}

	public void testGetMinSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("minSignal", 1, (int)chromatogram.getMinSignal());
	}

	public void testGetMaxSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("maxSignal", 100, (int)chromatogram.getMaxSignal());
	}

	public void testGetTotalIonSignals_1() {

		ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals();
		assertEquals("List<ITotalIonSignal> size", 80, signals.size());
	}

	public void testGetTotalIonSignal_1() {

		// Cast to int because float is not as precise as for example
		// java.lang.Math.
		assertEquals("totalIonSignal", 3840, (int)chromatogram.getTotalSignal());
	}

	public void testGetExtractedIonSignals_1() {

		IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals();
		assertEquals("List<IExtractedIonSignal> size", 80, signals.size());
	}

	public void testGetExtractedIonSignals_2() {

		IExtractedIonSignals signals = extractedIonSignalExtractor.getExtractedIonSignals(1.0f, 100.0f);
		assertEquals("List<IExtractedIonSignal> size", 80, signals.size());
	}

	public void testGetScan_1() {

		IScanMSD massSpectrum;
		massSpectrum = chromatogram.getSupplierScan(50);
		assertEquals("TotalSignal", 50.0f, massSpectrum.getTotalSignal());
	}

	public void testGetScan_2() {

		IScanMSD massSpectrum;
		massSpectrum = chromatogram.getSupplierScan(51);
		assertEquals("TotalSignal", 71.0f, massSpectrum.getTotalSignal());
	}
}
