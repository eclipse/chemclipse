/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.statistics;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import junit.framework.TestCase;

public class SimpleRegression_2_Test extends TestCase {

	private SimpleRegression simpleRegression;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		simpleRegression = new SimpleRegression(true);
		//
		simpleRegression.addData(0.0197044334975369, 0.0146511627906977);
		simpleRegression.addData(0.2955665024630540, 0.2474747474747470);
		simpleRegression.addData(2.2167487684729100, 1.8750000000000000);
		simpleRegression.addData(12.3152709359606000, 10.2666666666667000);
		simpleRegression.addData(27.0935960591133000, 22.7981651376147000);
	}

	@Override
	protected void tearDown() throws Exception {

		simpleRegression = null;
		super.tearDown();
	}

	public void testMeanSquareError() {

		assertEquals(0.0024739296329698846d, simpleRegression.getMeanSquareError());
	}

	public void testSlope() {

		assertEquals(0.8406964541842654d, simpleRegression.getSlope());
	}

	public void testIntercept() {

		assertEquals(-0.01151940381461003d, simpleRegression.getIntercept());
	}

	public void testRegressionCoefficient() {

		assertEquals(0.9999805152424263d, simpleRegression.getRSquare());
	}

	public void testInterceptStdErr() {

		assertEquals(0.028596869868268208d, simpleRegression.getInterceptStdErr());
	}

	public void testN() {

		assertEquals(5, simpleRegression.getN());
	}

	public void testR() {

		assertEquals(0.9999902575737557d, simpleRegression.getR());
	}

	public void testRegressionSumSquares() {

		assertEquals(380.8948743173564d, simpleRegression.getRegressionSumSquares());
	}

	public void testSignificance() {

		assertEquals(3.65034500404704E-8d, simpleRegression.getSignificance());
	}

	public void testSlopeConfidenceInterval() {

		assertEquals(0.006818540236764045d, simpleRegression.getSlopeConfidenceInterval());
	}

	public void testSlopeStdErr() {

		assertEquals(0.0021425468283177863d, simpleRegression.getSlopeStdErr());
	}

	public void testSumOfCrossProducts() {

		assertEquals(453.0706325946644d, simpleRegression.getSumOfCrossProducts());
	}

	public void testSumSquaredErrors() {

		assertEquals(0.007421788898909654d, simpleRegression.getSumSquaredErrors());
	}

	public void testTotalSumSquares() {

		assertEquals(380.90229610625533d, simpleRegression.getTotalSumSquares());
	}

	public void testXSumSquares() {

		assertEquals(538.9229731369362d, simpleRegression.getXSumSquares());
	}
}
