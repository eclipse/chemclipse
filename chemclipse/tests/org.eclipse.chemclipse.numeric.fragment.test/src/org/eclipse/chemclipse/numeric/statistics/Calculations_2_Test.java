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

public class Calculations_2_Test extends TestCase {

	private double[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new double[13];
		values[0] = 735;
		values[1] = 504;
		values[2] = 561;
		values[3] = 504;
		values[4] = 400;
		values[5] = 420;
		values[6] = 501;
		values[7] = 443;
		values[8] = 430;
		values[9] = 337;
		values[10] = 345;
		values[11] = 304;
		values[12] = 381;
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testGetMean_1() {

		double result = Calculations.getMean(values);
		assertEquals("getMean", 451.15384615384613d, result);
	}

	public void testGetVariance_1() {

		double result = Calculations.getVariance(values);
		assertEquals("getVariance", 12973.474358974356d, result);
	}

	public void testGetStandardDeviation_1() {

		double result = Calculations.getStandardDeviation(values);
		assertEquals("getStandardDeviation", 113.90116048124513d, result);
	}
}
