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
public class Calculations_21_Test extends TestCase {

	private float[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new float[0];
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testGetMean_1() {

		float min = Calculations.getMin(values);
		assertEquals("min", 0.0f, min);
	}

	public void testGetMean_2() {

		float max = Calculations.getMax(values);
		assertEquals("max", 0.0f, max);
	}
}
