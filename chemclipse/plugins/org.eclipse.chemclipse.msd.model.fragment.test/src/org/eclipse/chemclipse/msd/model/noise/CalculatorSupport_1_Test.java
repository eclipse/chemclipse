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

import junit.framework.TestCase;

public class CalculatorSupport_1_Test extends TestCase {

	private CalculatorSupport calculatorSupport;

	protected void setUp() throws Exception {

		super.setUp();
		calculatorSupport = new CalculatorSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testAcceptSegment_1() {

		double[] values = new double[13];
		values[0] = 20;
		values[1] = 10;
		values[2] = 20;
		values[3] = 10;
		values[4] = 10;
		values[5] = 20;
		values[6] = 10;
		values[7] = 10;
		values[8] = 20;
		values[9] = 10;
		values[10] = 10;
		values[11] = 20;
		values[12] = 10;
		double mean = 13.8461538461538;
		assertTrue("9 crossings", calculatorSupport.acceptSegment(values, mean));
	}

	public void testAcceptSegment_2() {

		double[] values = new double[13];
		values[0] = 20;
		values[1] = 10;
		values[2] = 20;
		values[3] = 10;
		values[4] = 10;
		values[5] = 20;
		values[6] = 10;
		values[7] = 10;
		values[8] = 20;
		values[9] = 10;
		values[10] = 10;
		values[11] = 10;
		values[12] = 10;
		double mean = 13.0769230769231;
		assertTrue("7 crossings", calculatorSupport.acceptSegment(values, mean));
	}

	public void testAcceptSegment_3() {

		double[] values = new double[13];
		values[0] = 10;
		values[1] = 10;
		values[2] = 20;
		values[3] = 10;
		values[4] = 10;
		values[5] = 20;
		values[6] = 10;
		values[7] = 10;
		values[8] = 20;
		values[9] = 10;
		values[10] = 10;
		values[11] = 10;
		values[12] = 10;
		double mean = 12.3076923076923;
		assertFalse("6 crossings", calculatorSupport.acceptSegment(values, mean));
	}

	public void testAcceptSegment_4() {

		double[] values = new double[13];
		values[0] = 10;
		values[1] = 10;
		values[2] = 10;
		values[3] = 10;
		values[4] = 10;
		values[5] = 20;
		values[6] = 10;
		values[7] = 10;
		values[8] = 20;
		values[9] = 10;
		values[10] = 10;
		values[11] = 10;
		values[12] = 10;
		double mean = 11.5384615384615;
		assertFalse("4 crossings", calculatorSupport.acceptSegment(values, mean));
	}
}
