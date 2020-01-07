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

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;

import junit.framework.TestCase;

public class TotalIonSignals_9_Test extends TestCase {

	private ITotalScanSignals signals;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		signals = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		signals = new TotalScanSignals(12);
		assertEquals("StartScan", 1, signals.getStartScan());
		assertEquals("StopScan", 12, signals.getStopScan());
	}

	public void testConstructor_2() {

		signals = new TotalScanSignals(150);
		assertEquals("StartScan", 1, signals.getStartScan());
		assertEquals("StopScan", 150, signals.getStopScan());
	}

	public void testConstructor_3() {

		signals = new TotalScanSignals(0);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_4() {

		signals = new TotalScanSignals(-1);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_5() {

		signals = new TotalScanSignals(12, 18);
		assertEquals("StartScan", 12, signals.getStartScan());
		assertEquals("StopScan", 18, signals.getStopScan());
	}

	public void testConstructor_6() {

		signals = new TotalScanSignals(18, 12);
		assertEquals("StartScan", 12, signals.getStartScan());
		assertEquals("StopScan", 18, signals.getStopScan());
	}

	public void testConstructor_7() {

		signals = new TotalScanSignals(0, 18);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_8() {

		signals = new TotalScanSignals(18, 0);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_9() {

		signals = new TotalScanSignals(0, 0);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_10() {

		signals = new TotalScanSignals(-1, 0);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_11() {

		signals = new TotalScanSignals(0, -1);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}

	public void testConstructor_12() {

		signals = new TotalScanSignals(-1, -1);
		assertEquals("StartScan", 0, signals.getStartScan());
		assertEquals("StopScan", 0, signals.getStopScan());
	}
}
