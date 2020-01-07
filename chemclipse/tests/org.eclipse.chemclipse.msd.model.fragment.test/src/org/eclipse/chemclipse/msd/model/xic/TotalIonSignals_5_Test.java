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

public class TotalIonSignals_5_Test extends TestCase {

	private ITotalScanSignals signals;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		signals = new TotalScanSignals(0);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSize_1() {

		assertEquals("size", 0, signals.size());
	}

	public void testGetMinMax_1() {

		assertEquals("max signal", 0.0f, signals.getMaxSignal());
		assertEquals("min signal", 0.0f, signals.getMinSignal());
	}
}
