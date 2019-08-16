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

public class SimpleRegression_1_Test extends TestCase {

	private SimpleRegression simpleRegression;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		simpleRegression = new SimpleRegression(false);
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

		assertEquals(0.001955804845977127d, simpleRegression.getMeanSquareError());
	}

	public void testSlope() {

		assertEquals(0.8401540522197605d, simpleRegression.getSlope());
	}

	public void testIntercept() {

		assertEquals(0.0d, simpleRegression.getIntercept());
	}

	public void testRegressionCoefficient() {

		assertEquals(0.9999875572637453d, simpleRegression.getRSquare());
	}

	public void testInterceptStdErr() {

		assertEquals(Double.NaN, simpleRegression.getInterceptStdErr());
	}

	public void testN() {

		assertEquals(5, simpleRegression.getN());
	}

	public void testR() {

		assertEquals(0.9999937786125198d, simpleRegression.getR());
	}

	public void testRegressionSumSquares() {

		assertEquals(628.7300382742203d, simpleRegression.getRegressionSumSquares());
	}

	public void testSignificance() {

		assertEquals(1.2099233170914658E-8d, simpleRegression.getSignificance());
	}

	public void testSlopeConfidenceInterval() {

		assertEquals(0.004715748826223578d, simpleRegression.getSlopeConfidenceInterval());
	}

	public void testSlopeStdErr() {

		assertEquals(0.0014817999659651035d, simpleRegression.getSlopeStdErr());
	}

	public void testSumOfCrossProducts() {

		assertEquals(748.3508966159962d, simpleRegression.getSumOfCrossProducts());
	}

	public void testSumSquaredErrors() {

		assertEquals(0.007823219383908508d, simpleRegression.getSumSquaredErrors());
	}

	public void testTotalSumSquares() {

		assertEquals(628.7378614936042d, simpleRegression.getTotalSumSquares());
	}

	public void testXSumSquares() {

		assertEquals(890.7305685651194d, simpleRegression.getXSumSquares());
	}
}
