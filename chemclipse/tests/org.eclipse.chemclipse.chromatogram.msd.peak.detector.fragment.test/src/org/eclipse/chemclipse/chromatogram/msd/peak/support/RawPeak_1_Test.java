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
package org.eclipse.chemclipse.chromatogram.msd.peak.support;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.peak.detector.support.RawPeak;

import junit.framework.TestCase;

/**
 * Tests the raw peak.
 * 
 * @author eselmeister
 */
public class RawPeak_1_Test extends TestCase {

	IRawPeak rawPeak;
	int startScan;
	int maximumScan;
	int stopScan;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		startScan = 5;
		stopScan = 20;
		maximumScan = 15;
		rawPeak = new RawPeak(startScan, maximumScan, stopScan);
	}

	@Override
	protected void tearDown() throws Exception {

		rawPeak = null;
		super.tearDown();
	}

	public void testGetStartScan_1() {

		assertEquals("StartScan", 5, rawPeak.getStartScan());
	}

	public void testGetMaximumScan_1() {

		assertEquals("MaximumScan", 15, rawPeak.getMaximumScan());
	}

	public void testGetStopScan_1() {

		assertEquals("StopScan", 20, rawPeak.getStopScan());
	}
}
