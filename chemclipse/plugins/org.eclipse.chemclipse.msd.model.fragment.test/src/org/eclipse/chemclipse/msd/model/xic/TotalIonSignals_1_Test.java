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

import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;

import junit.framework.TestCase;

public class TotalIonSignals_1_Test extends TestCase {

	private ITotalScanSignals signals;
	private ITotalScanSignal signal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		signals = new TotalScanSignals(10);
		for(int i = 1; i <= 10; i++) {
			signal = new TotalScanSignal(i * 100, 0.0f, i * 100);
			signals.add(signal);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("size", 10, signals.size());
	}

	public void testGetTotalIonSignal_1() {

		signal = signals.getTotalScanSignal(1);
		assertEquals("retention time", 1 * 100, signal.getRetentionTime());
		assertEquals("retention index", 0.0f, signal.getRetentionIndex());
		assertEquals("total signal", 1 * 100.0f, signal.getTotalSignal());
	}

	public void testGetTotalIonSignal_2() {

		signal = signals.getTotalScanSignal(5);
		assertEquals("retention time", 5 * 100, signal.getRetentionTime());
		assertEquals("retention index", 0.0f, signal.getRetentionIndex());
		assertEquals("total signal", 5 * 100.0f, signal.getTotalSignal());
	}

	public void testGetTotalIonSignal_3() {

		signal = signals.getTotalScanSignal(10);
		assertEquals("retention time", 10 * 100, signal.getRetentionTime());
		assertEquals("retention index", 0.0f, signal.getRetentionIndex());
		assertEquals("total signal", 10 * 100.0f, signal.getTotalSignal());
	}

	public void testGetTotalIonSignal_4() {

		signal = signals.getTotalScanSignal(0);
		assertNull("ITotalIonSignal", signal);
	}

	public void testGetTotalIonSignal_5() {

		signal = signals.getTotalScanSignal(11);
		assertNull("ITotalIonSignal", signal);
	}

	public void testGetTotalIonSignal_6() {

		signal = signals.getTotalScanSignal(-1);
		assertNull("ITotalIonSignal", signal);
	}
}
