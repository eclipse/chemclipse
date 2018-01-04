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

import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import junit.framework.TestCase;

public class ExtractedIonSignals_2_Test extends TestCase {

	private IExtractedIonSignals extractedIonSignals;
	private IExtractedIonSignal extractedIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		extractedIonSignals = null;
		extractedIonSignal = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		extractedIonSignals = new ExtractedIonSignals(10);
		for(int i = 1; i <= 10; i++) {
			extractedIonSignal = new ExtractedIonSignal(i * 10, i * 20);
			extractedIonSignals.add(extractedIonSignal);
		}
		assertEquals("StartScan", 1, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 10, extractedIonSignals.getStopScan());
		assertEquals("StartIon", 10, extractedIonSignals.getStartIon());
		assertEquals("StopIon", 200, extractedIonSignals.getStopIon());
	}

	public void testConstructor_2() {

		extractedIonSignals = new ExtractedIonSignals(-1, -1);
		assertEquals("StartScan", 0, extractedIonSignals.getStartScan());
		assertEquals("StopScan", 0, extractedIonSignals.getStopScan());
		int scan = extractedIonSignals.getStartScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
		scan = extractedIonSignals.getStopScan();
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
		scan = 0;
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
		scan = 2;
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", true);
		}
	}

	public void testGetExtractedIonSignal_1() {

		extractedIonSignals = new ExtractedIonSignals(10);
		for(int i = 1; i <= 10; i++) {
			extractedIonSignal = new ExtractedIonSignal(i * 10, i * 20);
			extractedIonSignals.add(extractedIonSignal);
		}
		int scan = 5;
		try {
			extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			assertTrue("NoExtractedIonSignalStoredException", false);
		}
		assertEquals("StartIon", 50, extractedIonSignal.getStartIon());
		assertEquals("StopIon", 100, extractedIonSignal.getStopIon());
	}
}
