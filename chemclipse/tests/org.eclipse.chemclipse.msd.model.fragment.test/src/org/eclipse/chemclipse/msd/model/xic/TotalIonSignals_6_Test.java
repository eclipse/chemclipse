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

public class TotalIonSignals_6_Test extends TestCase {

	private ITotalScanSignals signals;
	private ITotalScanSignal signal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		signals = new TotalScanSignals(12);
		float[] abundance = new float[12];
		abundance[0] = 4512.3f;
		abundance[1] = 57822.4f;
		abundance[2] = 47556.2f;
		abundance[3] = 23354.2f;
		abundance[4] = 896116.3f;
		abundance[5] = 1245234.8f;
		abundance[6] = 785154.2f;
		abundance[7] = 655421.2f;
		abundance[8] = 1245.3f;
		abundance[9] = 512.9f;
		abundance[10] = 52.2f;
		abundance[11] = 5.9f;
		for(int i = 1; i <= 12; i++) {
			signal = new TotalScanSignal(i * 100, 0.0f, abundance[i - 1]);
			signals.add(signal);
		}
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("size", 12, signals.size());
	}

	public void testGetMinMax_1() {

		signal = signals.getMaxTotalScanSignal();
		assertEquals("max signal", 1245234.8f, signal.getTotalSignal());
		signal = signals.getMinTotalScanSignal();
		assertEquals("min signal", 5.9f, signal.getTotalSignal());
	}
}
