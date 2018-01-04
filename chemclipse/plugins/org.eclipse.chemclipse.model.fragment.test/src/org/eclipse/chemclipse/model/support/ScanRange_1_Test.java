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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;

import junit.framework.TestCase;

/**
 * Tests the class IonRange.
 * 
 * @author eselmeister
 */
public class ScanRange_1_Test extends TestCase {

	private IScanRange scanRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		scanRange = null;
		super.tearDown();
	}

	public void testSetup_1() {

		scanRange = new ScanRange(1, 5);
		assertEquals("startScan", 1, scanRange.getStartScan());
		assertEquals("stopScan", 5, scanRange.getStopScan());
	}

	public void testSetup_2() {

		scanRange = new ScanRange(5, 1);
		assertEquals("startScan", 1, scanRange.getStartScan());
		assertEquals("stopScan", 5, scanRange.getStopScan());
		assertEquals("width", 5, scanRange.getWidth());
	}

	public void testSetup_3() {

		scanRange = new ScanRange(0, 5);
		assertEquals("startScan", IScanRange.MIN_SCAN, scanRange.getStartScan());
		assertEquals("stopScan", 5, scanRange.getStopScan());
		assertEquals("width", 5, scanRange.getWidth());
	}

	public void testSetup_4() {

		scanRange = new ScanRange(1, 0);
		assertEquals("startScan", 1, scanRange.getStartScan());
		assertEquals("stopScan", IScanRange.MIN_SCAN, scanRange.getStopScan());
		assertEquals("width", 1, scanRange.getWidth());
	}

	public void testSetup_5() {

		// Because IScanRange.MAX_SCAN+1 is negative.
		scanRange = new ScanRange(IScanRange.MAX_SCAN + 1, 5);
		assertEquals("startScan", 1, scanRange.getStartScan());
		assertEquals("stopScan", 5, scanRange.getStopScan());
		assertEquals("width", 5, scanRange.getWidth());
	}

	public void testSetup_6() {

		// Because IScanRange.MAX_SCAN+1 is negative.
		scanRange = new ScanRange(1, IScanRange.MAX_SCAN + 1);
		assertEquals("startScan", 1, scanRange.getStartScan());
		assertEquals("stopScan", 1, scanRange.getStopScan());
		assertEquals("width", 1, scanRange.getWidth());
	}

	public void testSetup_7() {

		// Because IScanRange.MAX_SCAN+1 is negative.
		scanRange = new ScanRange(-1, IScanRange.MAX_SCAN + 1);
		assertEquals("startScan", 1, scanRange.getStartScan());
		assertEquals("stopScan", IScanRange.MAX_SCAN, scanRange.getStopScan());
		assertEquals("width", IScanRange.MAX_SCAN, scanRange.getWidth());
	}
}
