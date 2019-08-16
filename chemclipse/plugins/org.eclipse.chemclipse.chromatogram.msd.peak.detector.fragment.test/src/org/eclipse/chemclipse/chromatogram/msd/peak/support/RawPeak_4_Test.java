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
public class RawPeak_4_Test extends TestCase {

	IRawPeak rawPeak1, rawPeak2;
	int startScan;
	int maximumScan;
	int stopScan;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		startScan = 5;
		stopScan = 20;
		maximumScan = 15;
		rawPeak1 = new RawPeak(startScan, maximumScan, stopScan);
		rawPeak2 = new RawPeak(startScan, 16, stopScan);
	}

	@Override
	protected void tearDown() throws Exception {

		rawPeak1 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertFalse("equals", rawPeak1.equals(rawPeak2));
	}

	public void testEquals_2() {

		assertFalse("equals", rawPeak2.equals(rawPeak1));
	}

	public void testEquals_3() {

		assertFalse("equals", rawPeak1.equals(null));
	}

	public void testEquals_4() {

		assertFalse("equals", rawPeak1.equals("test"));
	}
}
