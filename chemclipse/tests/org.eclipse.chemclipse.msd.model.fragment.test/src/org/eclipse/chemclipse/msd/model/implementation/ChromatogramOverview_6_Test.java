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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;

import junit.framework.TestCase;

/**
 * Test the interface IChromatogramOverview with 1 scan.
 * 
 * @author eselmeister
 */
public class ChromatogramOverview_6_Test extends TestCase {

	private ChromatogramMSD chrom;
	private IChromatogramOverview chromatogram;
	private float RT_FACTOR = 1000.0f * 60.0f;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chrom = new ChromatogramMSD();
		chromatogram = chrom;
		// There is no mass spectrum added to the chromatogram.
		chromatogram.setScanDelay(5500);
		chromatogram.setScanInterval(1500);
		chromatogram.recalculateRetentionTimes();
	}

	@Override
	protected void tearDown() throws Exception {

		chrom = null;
		chromatogram = null;
		super.tearDown();
	}

	public void testGetScanNumber_1() {

		float min = 12736 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_2() {

		float min = 5501 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_3() {

		float min = 5500 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_4() {

		float min = 5499 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_5() {

		float min = 0 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}

	public void testGetScanNumber_6() {

		float min = -1 / RT_FACTOR;
		assertEquals("ScanNumber", 0, chromatogram.getScanNumber(min));
	}
}
