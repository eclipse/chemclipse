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
public class Calculations_8_Test extends TestCase {

	private float[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new float[13];
		values[0] = 735.0f;
		values[1] = 504.0f;
		values[2] = 561.0f;
		values[3] = 504.0f;
		values[4] = 400.0f;
		values[5] = 420.0f;
		values[6] = 501.0f;
		values[7] = 443.0f;
		values[8] = 430.0f;
		values[9] = 337.0f;
		values[10] = 345.0f;
		values[11] = 304.0f;
		values[12] = 381.0f;
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testNormalize_1() {

		Calculations.normalize(values);
		assertEquals(1.0f, values[0]);
		assertEquals(0.51836735f, values[12]);
		assertEquals(0.68163264f, values[6]);
		assertEquals(0.46938777f, values[10]);
	}
}
