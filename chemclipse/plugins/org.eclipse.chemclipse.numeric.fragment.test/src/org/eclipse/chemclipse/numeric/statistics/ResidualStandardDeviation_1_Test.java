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

import junit.framework.TestCase;

public class ResidualStandardDeviation_1_Test extends TestCase {

	/*
	 * http://office.microsoft.com/de-de/excel-help/stfehleryx-HP005209284.aspx
	 */
	private ResidualStandardDeviationCalculator calculator;
	private double result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new ResidualStandardDeviationCalculator();
		// (y, x)
		double[][] data = {{2, 6}, {3, 5}, {9, 11}, {1, 7}, {8, 5}, {7, 4}, {5, 4}};
		result = calculator.calculate(data);
	}

	@Override
	protected void tearDown() throws Exception {

		calculator = null;
		super.tearDown();
	}

	public void testMeanSquareError() {

		assertEquals(3.305718950210041d, result);
	}
}
