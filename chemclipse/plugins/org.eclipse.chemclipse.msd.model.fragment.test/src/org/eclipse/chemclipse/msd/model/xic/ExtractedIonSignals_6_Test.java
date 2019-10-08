/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;

import junit.framework.TestCase;

public class ExtractedIonSignals_6_Test extends TestCase {

	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;
	private IChromatogramMSD chromatogram;
	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal totalIonSignal;
	private IScanMSD massSpectrum;
	private IMarkedIons excludedIons;

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

		totalIonSignals = extractedIonSignals.getTotalIonSignals();
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 165.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 16500.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 4290.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 14355.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(101);
		assertNull(totalIonSignal);
	}

	public void testGetTotalIonSignals_3() {

		IScanRange scanRange = new ScanRange(26, 87);
		totalIonSignals = extractedIonSignals.getTotalIonSignals(scanRange);
		totalIonSignal = totalIonSignals.getTotalScanSignal(1);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(100);
		assertNull(totalIonSignal);
		totalIonSignal = totalIonSignals.getTotalScanSignal(26);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 4290.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(87);
		assertNotNull(totalIonSignal);
		assertEquals("TotalSignal", 14355.0f, totalIonSignal.getTotalSignal());
		totalIonSignal = totalIonSignals.getTotalScanSignal(101);
		assertNull(totalIonSignal);
	}

	public void testGetTotalIonSignals_4() {

		totalIonSignals = extractedIonSignals.getTotalIonSignals(null);
	}

	public void testGetScan_1() {

		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		massSpectrum = extractedIonSignals.getScan(26, excludedIons);
		assertEquals("Ions", 6, massSpectrum.getIons().size());
		assertEquals("TotalSignal", 4290.0f, massSpectrum.getTotalSignal());
	}

	public void testGetScan_2() {

		massSpectrum = extractedIonSignals.getScan(26);
		assertEquals("Ions", 6, massSpectrum.getIons().size());
		assertEquals("TotalSignal", 4290.0f, massSpectrum.getTotalSignal());
	}

	public void testGetScan_3() {

		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		excludedIons.add(new MarkedIon(25));
		excludedIons.add(new MarkedIon(30));
		massSpectrum = extractedIonSignals.getScan(26, excludedIons);
		assertEquals("Ions", 4, massSpectrum.getIons().size());
		assertEquals("TotalSignal", 2860.0f, massSpectrum.getTotalSignal());
	}

	public void testGetScan_4() {

		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		excludedIons.add(new MarkedIon(25));
		excludedIons.add(new MarkedIon(26));
		excludedIons.add(new MarkedIon(27));
		excludedIons.add(new MarkedIon(28));
		excludedIons.add(new MarkedIon(30));
		massSpectrum = extractedIonSignals.getScan(26, excludedIons);
		assertEquals("Ions", 1, massSpectrum.getIons().size());
		assertEquals("TotalSignal", 754.0f, massSpectrum.getTotalSignal());
	}

	public void testGetScan_5() {

		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		excludedIons.add(new MarkedIon(25));
		excludedIons.add(new MarkedIon(26));
		excludedIons.add(new MarkedIon(27));
		excludedIons.add(new MarkedIon(28));
		excludedIons.add(new MarkedIon(29));
		excludedIons.add(new MarkedIon(30));
		massSpectrum = extractedIonSignals.getScan(26, excludedIons);
		assertEquals("Ions", 0, massSpectrum.getIons().size());
		assertEquals("TotalSignal", 0.0f, massSpectrum.getTotalSignal());
	}
}
