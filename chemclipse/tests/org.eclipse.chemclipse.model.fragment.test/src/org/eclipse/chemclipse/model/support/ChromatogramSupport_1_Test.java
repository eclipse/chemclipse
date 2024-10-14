/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.implementation.Chromatogram;
import org.eclipse.chemclipse.model.implementation.Scan;

import junit.framework.TestCase;

public class ChromatogramSupport_1_Test extends TestCase {

	private IChromatogram<?> chromatogram = new Chromatogram();

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

	public void test1() {

		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
		//
		assertEquals(0, chromatogram.getScanDelay());
		assertEquals(100, chromatogram.getScanInterval());
	}

	public void test2() {

		chromatogram.addScan(getScan(0));
		//
		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
		//
		assertEquals(0, chromatogram.getScanDelay());
		assertEquals(100, chromatogram.getScanInterval());
	}

	public void test3() {

		chromatogram.addScan(getScan(0));
		chromatogram.addScan(getScan(500));
		//
		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
		//
		assertEquals(0, chromatogram.getScanDelay());
		assertEquals(500, chromatogram.getScanInterval());
	}

	public void test4() {

		chromatogram.addScan(getScan(0));
		chromatogram.addScan(getScan(500));
		chromatogram.addScan(getScan(1000));
		//
		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
		//
		assertEquals(0, chromatogram.getScanDelay());
		assertEquals(500, chromatogram.getScanInterval());
	}

	public void test5() {

		chromatogram.addScan(getScan(500));
		chromatogram.addScan(getScan(1000));
		chromatogram.addScan(getScan(1500));
		//
		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
		//
		assertEquals(500, chromatogram.getScanDelay());
		assertEquals(500, chromatogram.getScanInterval());
	}

	public void test6() {

		chromatogram.addScan(getScan(750));
		chromatogram.addScan(getScan(1000));
		chromatogram.addScan(getScan(1250));
		chromatogram.addScan(getScan(1500));
		//
		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogram);
		//
		assertEquals(750, chromatogram.getScanDelay());
		assertEquals(250, chromatogram.getScanInterval());
	}

	private IScan getScan(int retentionTime) {

		/*
		 * Intensity is not important here.
		 */
		IScan scan = new Scan(2500);
		scan.setRetentionTime(retentionTime);
		//
		return scan;
	}
}