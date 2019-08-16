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
import org.eclipse.chemclipse.model.signals.TotalScanSignal;

import junit.framework.TestCase;

/**
 * Test all methods of TotalIonSignal.
 * 
 * @author eselmeister
 */
public class TotalIonSignal_2_Test extends TestCase {

	private ITotalScanSignal totalIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		totalIonSignal = new TotalScanSignal(-1, -1.0f, -1.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal = null;
		super.tearDown();
	}

	public void testGetRetentionTime_1() {

		assertEquals("getRetentionTime", 0, totalIonSignal.getRetentionTime());
	}

	public void testGetRetentionIndex_1() {

		assertEquals("getRetentionIndex", 0.0f, totalIonSignal.getRetentionIndex());
	}

	public void testGetTotalSignal_1() {

		assertEquals("getTotalSignal", 0.0f, totalIonSignal.getTotalSignal());
	}
}
