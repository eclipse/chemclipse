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

import org.eclipse.chemclipse.model.signals.ExtendedTotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;

import junit.framework.TestCase;

/**
 * Test methods of ExtendedTotalIonSignal.
 * 
 * @author eselmeister
 */
public class ExtendedTotalIonSignal_1_Test extends TestCase {

	private ITotalScanSignal totalIonSignal;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		totalIonSignal = null;
		super.tearDown();
	}

	public void testConstructor_1() {

		totalIonSignal = new ExtendedTotalScanSignal(1000, 0.0f, -5949.3f);
		assertEquals("Total Signal", -5949.3f, totalIonSignal.getTotalSignal());
	}

	public void testSetTotalSignal_1() {

		totalIonSignal = new ExtendedTotalScanSignal(1000, 0.0f, 0.0f);
		totalIonSignal.setTotalSignal(-5949.3f);
		assertEquals("Total Signal", -5949.3f, totalIonSignal.getTotalSignal());
	}
}
