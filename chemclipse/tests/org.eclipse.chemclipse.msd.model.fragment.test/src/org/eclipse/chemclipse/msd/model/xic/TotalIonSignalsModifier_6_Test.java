/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;

import junit.framework.TestCase;

public class TotalIonSignalsModifier_6_Test extends TestCase {

	private ITotalScanSignals signals;
	private ITotalScanSignal signal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		signals = new TotalScanSignals(4);
		float[] abundance = new float[4];
		abundance[0] = 4512.3f;
		abundance[1] = 57822.4f;
		abundance[2] = 47556.2f;
		abundance[3] = 23354.2f;
		for(int i = 1; i <= 4; i++) {
			signal = new TotalScanSignal(i * 100, 0.0f, abundance[i - 1]);
			signals.add(signal);
		}
		TotalScanSignalsModifier.calculateMovingAverage(signals, 5);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("size", 4, signals.size());
	}

	public void testGetPoint_1() {

		int scan = 1;
		ITotalScanSignal s1 = signals.getTotalScanSignal(scan);
		assertEquals("total signal", 4512.3f, s1.getTotalSignal());
	}

	public void testGetPoint_2() {

		int scan = 2;
		ITotalScanSignal s1 = signals.getTotalScanSignal(scan);
		assertEquals("total signal", 57822.4f, s1.getTotalSignal());
	}

	public void testGetPoint_3() {

		int scan = 3;
		ITotalScanSignal s1 = signals.getTotalScanSignal(scan);
		assertEquals("total signal", 47556.2f, s1.getTotalSignal());
	}

	public void testGetPoint_4() {

		int scan = 4;
		ITotalScanSignal s1 = signals.getTotalScanSignal(scan);
		assertEquals("total signal", 23354.2f, s1.getTotalSignal());
	}
}
