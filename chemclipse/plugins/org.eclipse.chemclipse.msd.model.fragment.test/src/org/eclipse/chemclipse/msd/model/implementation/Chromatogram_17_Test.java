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

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;

import junit.framework.TestCase;

/**
 * Add 100 scans and get an {@link ITotalScanSignals} object.
 * 
 * @author eselmeister
 */
public class Chromatogram_17_Test extends TestCase {

	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum supplierMassSpectrum;
	private IScanIon ion;
	private IMarkedIons excludedIons;
	private IScanMSD ms;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		// ------------------------------Scan 1-100
		for(int i = 1; i <= 100; i++) {
			supplierMassSpectrum = new VendorMassSpectrum();
			supplierMassSpectrum.setRetentionTime(i);
			for(int j = 1; j <= 50; j++) {
				ion = new ScanIon(j, j);
				supplierMassSpectrum.addIon(ion);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		// ------------------------------Scan 1-100
		excludedIons = new MarkedIons();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		supplierMassSpectrum = null;
		ion = null;
		excludedIons = null;
		ms = null;
		super.tearDown();
	}

	public void testGetNumberOfScans_1() {

		assertEquals("numberOfScans", 100, chromatogram.getNumberOfScans());
	}

	public void testGetStartRetentionTime_1() {

		assertEquals("startRetentionTime", 1, chromatogram.getStartRetentionTime());
	}

	public void testGetStopRetentionTime_1() {

		assertEquals("stopRetentionTime", 100, chromatogram.getStopRetentionTime());
	}

	public void testGetTotalIonSignals_1() {

		excludedIons.add(1, 50);
		ms = chromatogram.getScan(1, excludedIons);
		assertEquals("TotalSignal", 0.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(20, excludedIons);
		assertEquals("TotalSignal", 0.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(100, excludedIons);
		assertEquals("TotalSignal", 0.0f, ms.getTotalSignal());
	}

	public void testGetTotalIonSignals_2() {

		excludedIons.add(26, 50);
		ms = chromatogram.getScan(1, excludedIons);
		assertEquals("TotalSignal", 325.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(20, excludedIons);
		assertEquals("TotalSignal", 325.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(100, excludedIons);
		assertEquals("TotalSignal", 325.0f, ms.getTotalSignal());
	}

	public void testGetTotalIonSignals_3() {

		excludedIons.add(new MarkedIon(26));
		ms = chromatogram.getScan(1, excludedIons);
		assertEquals("TotalSignal", 1249.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(20, excludedIons);
		assertEquals("TotalSignal", 1249.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(100, excludedIons);
		assertEquals("TotalSignal", 1249.0f, ms.getTotalSignal());
	}

	public void testGetTotalIonSignals_4() {

		ms = chromatogram.getScan(1, null);
		assertEquals("TotalSignal", 1275.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(20, excludedIons);
		assertEquals("TotalSignal", 1275.0f, ms.getTotalSignal());
		ms = chromatogram.getScan(100, excludedIons);
		assertEquals("TotalSignal", 1275.0f, ms.getTotalSignal());
	}
}
