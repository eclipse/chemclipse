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
package org.eclipse.chemclipse.numeric.miscellaneous;

import junit.framework.TestCase;

/**
 * Tests the valuesAreIncreasing(double[] values) method.
 * 
 * @author eselmeister
 */
public class Evaluation_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValuesAreaIncreasing_1() {

		double[] values = new double[3];
		values[0] = 4.5d;
		values[1] = 5.4d;
		values[2] = 5.8d;
		assertTrue(Evaluation.valuesAreIncreasing(values));
	}

	public void testValuesAreaIncreasing_2() {

		double[] values = new double[2];
		values[0] = 4.5d;
		values[1] = 5.4d;
		assertTrue(Evaluation.valuesAreIncreasing(values));
	}

	public void testValuesAreaIncreasing_3() {

		double[] values = new double[3];
		values[0] = 4.5d;
		values[1] = 5.4d;
		assertFalse(Evaluation.valuesAreIncreasing(values));
	}

	public void testValuesAreaIncreasing_4() {

		double[] values = new double[1];
		values[0] = 4.5d;
		assertFalse(Evaluation.valuesAreIncreasing(values));
	}

	public void testValuesAreaIncreasing_5() {

		double[] values = new double[3];
		values[0] = 4.5d;
		values[1] = 4.4d;
		values[2] = 5.8d;
		assertFalse(Evaluation.valuesAreIncreasing(values));
	}
}
