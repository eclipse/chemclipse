/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.signals;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.implementation.Chromatogram;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class TotalScanSignalExtractor_1_Test extends TestCase {

	private IChromatogram<?> chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void test_1() {

		boolean condenseCycleNumberScans = false;
		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 1);
		addScan(chromatogram, 2000, 289830.65f, 1);
		addScan(chromatogram, 2500, 3983.0f, 1);
		assertEquals(false, chromatogram.containsScanCycles());
		//
		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(1, 5, true, condenseCycleNumberScans);
			assertEquals(2898.2f, signals.getMinSignal());
			assertEquals(289830.65f, signals.getMaxSignal());
			assertEquals(5, signals.size());
		} catch(ChromatogramIsNullException e) {
			assertTrue(false);
		}
	}

	public void test_2() {

		boolean condenseCycleNumberScans = true;
		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 1);
		addScan(chromatogram, 2000, 289830.65f, 1);
		addScan(chromatogram, 2500, 3983.0f, 1);
		assertEquals(false, chromatogram.containsScanCycles());
		//
		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(1, 5, true, condenseCycleNumberScans);
			assertEquals(433449.53f, signals.getMinSignal());
			assertEquals(433449.53f, signals.getMaxSignal());
			assertEquals(1, signals.size());
		} catch(ChromatogramIsNullException e) {
			assertTrue(false);
		}
	}

	public void test_3() {

		boolean condenseCycleNumberScans = false;
		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 2);
		addScan(chromatogram, 2000, 289830.65f, 2);
		addScan(chromatogram, 2500, 3983.0f, 3);
		assertEquals(true, chromatogram.containsScanCycles());
		//
		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(1, 5, true, condenseCycleNumberScans);
			assertEquals(2898.2f, signals.getMinSignal());
			assertEquals(289830.65f, signals.getMaxSignal());
			assertEquals(5, signals.size());
		} catch(ChromatogramIsNullException e) {
			assertTrue(false);
		}
	}

	public void test_4() {

		boolean condenseCycleNumberScans = true;
		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 2);
		addScan(chromatogram, 2000, 289830.65f, 2);
		addScan(chromatogram, 2500, 3983.0f, 3);
		assertEquals(true, chromatogram.containsScanCycles());
		//
		try {
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals signals = totalIonSignalExtractor.getTotalScanSignals(1, 5, true, condenseCycleNumberScans);
			assertEquals(3983.0f, signals.getMinSignal());
			assertEquals(418731, 13f, signals.getMaxSignal());
			assertEquals(3, signals.size());
		} catch(ChromatogramIsNullException e) {
			assertTrue(false);
		}
	}

	private void addScan(IChromatogram<?> chromatogram, int retentionTime, float intensity, int cycleNumber) {

		IScan scan = new Scan(intensity);
		scan.setRetentionTime(retentionTime);
		scan.setCycleNumber(cycleNumber);
		chromatogram.addScan(scan);
	}
}
