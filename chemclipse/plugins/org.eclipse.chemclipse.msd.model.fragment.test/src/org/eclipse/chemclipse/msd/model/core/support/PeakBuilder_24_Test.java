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
package org.eclipse.chemclipse.msd.model.core.support;

import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanIon;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;

import junit.framework.TestCase;

/**
 * Test the peak exceptions.
 * 
 * @author eselmeister
 */
public class PeakBuilder_24_Test extends TestCase {

	private ITotalScanSignals totalIonSignals;
	private ITotalScanSignal totalIonSignal;
	private IScanRange scanRange;
	private IChromatogramMSD chromatogram;
	private IVendorMassSpectrum massSpectrum;
	private IScanIon defaultIon;
	private IMarkedIons excludedIons;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		scanRange = new ScanRange(1, 10);
		chromatogram = new ChromatogramMSD();
		chromatogram.setScanDelay(500);
		chromatogram.setScanInterval(1000);
		for(int scan = 1; scan <= 10; scan++) {
			massSpectrum = new VendorMassSpectrum();
			for(int ion = 32; ion <= 38; ion++) {
				defaultIon = new ScanIon(ion, ion * 5);
				massSpectrum.addIon(defaultIon);
			}
			chromatogram.addScan(massSpectrum);
		}
		chromatogram.recalculateRetentionTimes();
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.EXCLUDE);
		excludedIons.add(new MarkedIon(34));
		excludedIons.add(new MarkedIon(36));
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		scanRange = null;
		super.tearDown();
	}

	public void testGetTotalIonSignals_1() {

		try {
			totalIonSignals = PeakBuilderMSD.getTotalIonSignals(chromatogram, scanRange, excludedIons);
			assertNotNull(totalIonSignals);
			totalIonSignal = totalIonSignals.getTotalScanSignal(1);
			assertEquals("Signal", 875.0f, totalIonSignal.getTotalSignal());
			totalIonSignal = totalIonSignals.getTotalScanSignal(10);
			assertEquals("Signal", 875.0f, totalIonSignal.getTotalSignal());
		} catch(PeakException e) {
			assertTrue("PeakException", false);
		}
	}

	public void testGetTotalIonSignals_2() {

		try {
			totalIonSignals = PeakBuilderMSD.getTotalIonSignals(null, scanRange, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetTotalIonSignals_3() {

		try {
			totalIonSignals = PeakBuilderMSD.getTotalIonSignals(chromatogram, null, excludedIons);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}

	public void testGetTotalIonSignals_4() {

		try {
			totalIonSignals = PeakBuilderMSD.getTotalIonSignals(chromatogram, scanRange, null);
		} catch(PeakException e) {
			assertTrue("PeakException", true);
		}
	}
}
