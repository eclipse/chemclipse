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
package org.eclipse.chemclipse.numeric.statistics;

import junit.framework.TestCase;

/**
 * Testing median.
 * 
 * @author eselmeister
 */
public class Calculations_14_Test extends TestCase {

	private double[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new double[13];
		values[0] = 735.0d;
		values[1] = 504.0d;
		values[2] = 561.0d;
		values[3] = 504.0d;
		values[4] = 400.0d;
		values[5] = 420.0d;
		values[6] = 501.0d;
		values[7] = 443.0d;
		values[8] = 430.0d;
		values[9] = 337.0d;
		values[10] = 345.0d;
		values[11] = 304.0d;
		values[12] = 381.0d;
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testGetWindowReducedLength_1() {

		int result = Calculations.getWindowReducedLength(values, 3);
		assertEquals("getWindowReducedLength", 11, result);
	}

	public void testGetWindowReducedLength_2() {

		int result = Calculations.getWindowReducedLength(null, 3);
		assertEquals("getWindowReducedLength", 0, result);
	}

	public void testGetWindowReducedLength_3() {

		int result = Calculations.getWindowReducedLength(values, 1);
		assertEquals("getWindowReducedLength", 13, result);
	}

	public void testGetWindowReducedLength_4() {

		int result = Calculations.getWindowReducedLength(values, 0);
		assertEquals("getWindowReducedLength", 13, result);
	}

	public void testGetWindowReducedLength_5() {

		int result = Calculations.getWindowReducedLength(values, 13);
		assertEquals("getWindowReducedLength", 1, result);
	}

	public void testGetWindowReducedLength_6() {

		int result = Calculations.getWindowReducedLength(values, 12);
		assertEquals("getWindowReducedLength", 2, result);
	}

	public void testGetWindowReducedLength_7() {

		int result = Calculations.getWindowReducedLength(values, 14);
		assertEquals("getWindowReducedLength", 13, result);
	}
}
