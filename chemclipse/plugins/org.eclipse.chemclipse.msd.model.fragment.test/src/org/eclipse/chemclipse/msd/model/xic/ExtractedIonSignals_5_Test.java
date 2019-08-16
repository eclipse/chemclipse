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
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;

import junit.framework.TestCase;

public class ExtractedIonSignals_5_Test extends TestCase {

	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;
	private IChromatogramMSD chromatogram;
	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal totalIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		int scans = 100;
		int ionStart = 25;
		int ionStop = 30;
		chromatogram = new ChromatogramMSD();
		extractedIonSignals = new ExtractedIonSignals(scans, chromatogram);
		/*
		 * Add 100 scans with scans of 6 ions.
		 */
		for(int scan = 1; scan <= scans; scan++) {
			extractedIonSignal = new ExtractedIonSignal(ionStart, ionStop);
			extractedIonSignal.setRetentionTime(scan);
			extractedIonSignal.setRetentionIndex(scan / 60.0f);
			for(int ion = ionStart; ion <= ionStop; ion++) {
				extractedIonSignal.setAbundance(ion, ion * scan);
			}
			extractedIonSignals.add(extractedIonSignal);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignals = null;
		extractedIonSignal = null;
		chromatogram = null;
		totalIonSignal = null;
		totalIonSignals = null;
		super.tearDown();
	}

	public void testGetTotalIonSignals_1() {

		IScanRange scanRange = new ScanRange(26, 100);
		totalIonSignals = extractedIonSignals.getTotalIonSignals((int)IIon.TIC_ION, scanRange);
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 16500.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 4290.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 14355.0f, totalIonSignal.getTotalSignal());
	}

	public void testGetTotalIonSignals_2() {

		IScanRange scanRange = new ScanRange(87, 100);
		totalIonSignals = extractedIonSignals.getTotalIonSignals(25, scanRange);
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 2500.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 2175.0f, totalIonSignal.getTotalSignal());
	}

	public void testGetTotalIonSignals_3() {

		IScanRange scanRange = new ScanRange(1, 100);
		totalIonSignals = extractedIonSignals.getTotalIonSignals(30, scanRange);
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 30.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 3000.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 780.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 2610.0f, totalIonSignal.getTotalSignal());
	}

	public void testGetTotalIonSignals_4() {

		IScanRange scanRange = new ScanRange(1, 100);
		totalIonSignals = extractedIonSignals.getTotalIonSignals(24, scanRange);
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
	}

	public void testGetTotalIonSignals_5() {

		IScanRange scanRange = new ScanRange(1, 1);
		totalIonSignals = extractedIonSignals.getTotalIonSignals(31, scanRange);
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 0.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNull(totalIonSignal);
	}
}
