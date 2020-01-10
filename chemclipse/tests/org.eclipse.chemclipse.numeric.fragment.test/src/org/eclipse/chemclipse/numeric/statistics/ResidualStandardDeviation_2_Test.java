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

public class ResidualStandardDeviation_2_Test extends TestCase {

	private ResidualStandardDeviationCalculator calculator;
	private double result;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		calculator = new ResidualStandardDeviationCalculator();
		// (y, x)
		double[][] data = {{0.0146511627906977, 0.0197044334975369}, {0.2474747474747470, 0.2955665024630540}, {1.8750000000000000, 2.2167487684729100}, {10.2666666666667000, 12.3152709359606000}, {22.7981651376147000, 27.0935960591133000}};
		result = calculator.calculate(data);
	}

	@Override
	protected void tearDown() throws Exception {

		calculator = null;
		super.tearDown();
	}

	public void testMeanSquareError() {

		assertEquals(0.04973861309857649d, result);
	}
}
