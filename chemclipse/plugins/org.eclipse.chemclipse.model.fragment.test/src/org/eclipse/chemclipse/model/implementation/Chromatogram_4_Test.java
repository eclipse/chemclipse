/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class Chromatogram_4_Test extends TestCase {

	private IChromatogram chromatogram;

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

		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 1);
		addScan(chromatogram, 2000, 289830.65f, 1);
		addScan(chromatogram, 2500, 3983.0f, 1);
		assertEquals(false, chromatogram.containsScanCycles());
		//
		assertEquals(0, chromatogram.getScanCycleScans(0).size());
		assertEquals(5, chromatogram.getScanCycleScans(1).size());
	}

	public void test_2() {

		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 1);
		addScan(chromatogram, 2000, 289830.65f, 1);
		addScan(chromatogram, 2500, 3983.0f, 1);
		assertEquals(false, chromatogram.containsScanCycles());
		//
		assertEquals(0.0f, getIntensity(chromatogram.getScanCycleScans(0)));
		assertEquals(433449.53f, getIntensity(chromatogram.getScanCycleScans(1)));
		assertEquals(0.0f, getIntensity(chromatogram.getScanCycleScans(2)));
		assertEquals(0.0f, getIntensity(chromatogram.getScanCycleScans(3)));
	}

	public void test_3() {

		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 2);
		addScan(chromatogram, 2000, 289830.65f, 2);
		addScan(chromatogram, 2500, 3983.0f, 3);
		assertEquals(true, chromatogram.containsScanCycles());
		//
		assertEquals(0, chromatogram.getScanCycleScans(0).size());
		assertEquals(2, chromatogram.getScanCycleScans(1).size());
		assertEquals(2, chromatogram.getScanCycleScans(2).size());
		assertEquals(1, chromatogram.getScanCycleScans(3).size());
	}

	public void test_4() {

		addScan(chromatogram, 500, 2898.2f, 1);
		addScan(chromatogram, 1000, 7837.2f, 1);
		addScan(chromatogram, 1500, 128900.48f, 2);
		addScan(chromatogram, 2000, 289830.65f, 2);
		addScan(chromatogram, 2500, 3983.0f, 3);
		assertEquals(true, chromatogram.containsScanCycles());
		//
		assertEquals(0.0f, getIntensity(chromatogram.getScanCycleScans(0)));
		assertEquals(10735.4f, getIntensity(chromatogram.getScanCycleScans(1)));
		assertEquals(418731.13f, getIntensity(chromatogram.getScanCycleScans(2)));
		assertEquals(3983.0f, getIntensity(chromatogram.getScanCycleScans(3)));
	}

	private float getIntensity(List<IScan> scans) {

		float intensity = 0;
		for(IScan scan : scans) {
			intensity += scan.getTotalSignal();
		}
		return intensity;
	}

	private void addScan(IChromatogram chromatogram, int retentionTime, float intensity, int cycleNumber) {

		IScan scan = new Scan(intensity);
		scan.setRetentionTime(retentionTime);
		scan.setCycleNumber(cycleNumber);
		chromatogram.addScan(scan);
	}
}
