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
package org.eclipse.chemclipse.msd.model.xic;

import junit.framework.TestCase;

public class ExtractedIonSignals_1_Test extends TestCase {

	private IExtractedIonSignals extractedIonSignals;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignals = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		extractedIonSignals = new ExtractedIonSignals(200);
		assertEquals("StartScan", 1, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 200, extractedIonSignals.getStopScan());
	}

	public void testConstructor_2() {

		extractedIonSignals = new ExtractedIonSignals(0);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
	}

	public void testConstructor_3() {

		extractedIonSignals = new ExtractedIonSignals(-1);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
	}

	public void testConstructor_4() {

		extractedIonSignals = new ExtractedIonSignals(20);
		assertEquals("StartScan", 1, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 20, extractedIonSignals.getStopScan());
	}

	public void testConstructor_5() {

		extractedIonSignals = new ExtractedIonSignals(20, 50);
		assertEquals("StartScan", 20, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 50, extractedIonSignals.getStopScan());
	}

	public void testConstructor_6() {

		extractedIonSignals = new ExtractedIonSignals(50, 20);
		assertEquals("StartScan", 20, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 50, extractedIonSignals.getStopScan());
	}

	public void testConstructor_7() {

		extractedIonSignals = new ExtractedIonSignals(-1, 20);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
	}

	public void testConstructor_8() {

		extractedIonSignals = new ExtractedIonSignals(20, -1);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
	}

	public void testConstructor_9() {

		extractedIonSignals = new ExtractedIonSignals(-1, -1);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
	}
}
