/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.noise;

import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.exceptions.FilterException;

import junit.framework.TestCase;

public class CalculatorSupport_3_Test extends TestCase {

	private CalculatorSupport calculatorSupport;
	private ScanRange scanRange;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculatorSupport = new CalculatorSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetScanRange_1() {

		try {
			scanRange = null;
			calculatorSupport.checkScanRange(scanRange, 13);
		} catch(FilterException e) {
			assertTrue("FilterException", true);
		}
	}

	public void testGetScanRange_2() {

		try {
			scanRange = new ScanRange(1, 13);
			calculatorSupport.checkScanRange(scanRange, 13);
		} catch(FilterException e) {
			assertFalse("A FilterException should not be thrown here.", false);
		}
	}

	public void testGetScanRange_3() {

		try {
			scanRange = new ScanRange(1, 12);
			calculatorSupport.checkScanRange(scanRange, 13);
		} catch(FilterException e) {
			assertTrue("FilterException", true);
		}
	}
}
