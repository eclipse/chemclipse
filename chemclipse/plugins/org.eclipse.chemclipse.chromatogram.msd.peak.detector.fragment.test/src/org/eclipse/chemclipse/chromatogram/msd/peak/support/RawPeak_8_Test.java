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
public class RawPeak_8_Test extends TestCase {

	IRawPeak rawPeak;
	int startScan;
	int maximumScan;
	int stopScan;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		startScan = 14;
		stopScan = 20;
		maximumScan = 21;
		rawPeak = new RawPeak(startScan, maximumScan, stopScan);
	}

	@Override
	protected void tearDown() throws Exception {

		rawPeak = null;
		super.tearDown();
	}

	public void testGetRetentionTimeAtMaximum_1() {

		assertEquals("RetentionTimeAtMaximum", 0, rawPeak.getRetentionTimeAtMaximum());
	}

	public void testGetRetentionTimeAtMaximum_2() {

		rawPeak.setRetentionTimeAtMaximum(2562);
		assertEquals("RetentionTimeAtMaximum", 2562, rawPeak.getRetentionTimeAtMaximum());
	}

	public void testGetRetentionTimeAtMaximum_3() {

		rawPeak.setRetentionTimeAtMaximum(-1);
		assertEquals("RetentionTimeAtMaximum", 0, rawPeak.getRetentionTimeAtMaximum());
	}

	public void testGetRetentionTimeAtMaximum_4() {

		rawPeak.setRetentionTimeAtMaximum(2562);
		rawPeak.setRetentionTimeAtMaximum(0);
		assertEquals("RetentionTimeAtMaximum", 0, rawPeak.getRetentionTimeAtMaximum());
	}
}
