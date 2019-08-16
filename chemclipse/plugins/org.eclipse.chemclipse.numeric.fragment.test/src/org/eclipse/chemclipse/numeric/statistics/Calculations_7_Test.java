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
public class Calculations_7_Test extends TestCase {

	private int[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new int[13];
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

	public void testGetSum_1() {

		int result = Calculations.getSum(values);
		assertEquals("getSum", 5865, result);
	}
}
