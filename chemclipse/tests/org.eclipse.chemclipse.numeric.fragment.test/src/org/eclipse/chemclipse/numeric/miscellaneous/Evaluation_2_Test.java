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
 * Tests the valuesAreGreaterThanThreshold(double[] values, double threshold)
 * method.
 * 
 * @author eselmeister
 */
public class Evaluation_2_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testValuesAreGreaterThanThreshold_1() {

		double threshold = 4.0d;
		double[] values = new double[3];
		values[0] = 4.5d;
		values[1] = 5.4d;
		values[2] = 5.8d;
		assertTrue(Evaluation.valuesAreGreaterThanThreshold(values, threshold));
	}

	public void testValuesAreGreaterThanThreshold_2() {

		double threshold = 5.0d;
		double[] values = new double[3];
		values[0] = 4.5d;
		values[1] = 5.4d;
		values[2] = 5.8d;
		assertFalse(Evaluation.valuesAreGreaterThanThreshold(values, threshold));
	}

	public void testValuesAreGreaterThanThreshold_3() {

		double threshold = 5.0d;
		double[] values = new double[3];
		assertFalse(Evaluation.valuesAreGreaterThanThreshold(values, threshold));
	}

	public void testValuesAreGreaterThanThreshold_4() {

		double threshold = 4.5d;
		double[] values = new double[3];
		values[0] = 4.5d;
		values[1] = 5.4d;
		values[2] = 5.8d;
		assertFalse(Evaluation.valuesAreGreaterThanThreshold(values, threshold));
	}
}
