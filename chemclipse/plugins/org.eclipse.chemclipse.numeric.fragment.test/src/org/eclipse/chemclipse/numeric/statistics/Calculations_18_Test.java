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
public class Calculations_18_Test extends TestCase {

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

	public void testCalculateSquareRootSum_1() {

		double sum = Calculations.calculateSumPow2(values);
		assertEquals("square root sum", 2801699.0d, sum);
	}

	public void testScaleToNormalizedUnity_1() {

		Calculations.scaleToNormalizedUnity(values);
		assertEquals("NormalizedUnity", 0.19282049927561812d, values[0]);
		assertEquals("NormalizedUnity", 0.05710820470007663d, values[4]);
		assertEquals("NormalizedUnity", 0.07004642540115837d, values[7]);
		assertEquals("NormalizedUnity", 0.0518117756404239d, values[12]);
		double sum = Calculations.getSum(values);
		assertEquals("sum", 1.0d, sum);
	}
}
