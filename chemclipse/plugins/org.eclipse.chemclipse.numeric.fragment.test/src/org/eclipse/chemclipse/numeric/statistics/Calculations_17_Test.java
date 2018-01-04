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
public class Calculations_17_Test extends TestCase {

	private double[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new double[13];
		values[0] = 735.0d;
		values[1] = 504.0d;
		values[2] = 561.0d;
		values[3] = 504.0d;
		values[4] = 400.0d;
		values[5] = 420.0d;
		values[6] = 501.0d;
		values[7] = 443.0d;
		values[8] = 430.0d;
		values[9] = 337.0d;
		values[10] = 345.0d;
		values[11] = 304.0d;
		values[12] = 381.0d;
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testScaleToStandardizedLength_1() {

		Calculations.scaleToStandardizedLength(values);
		double sum = Calculations.getSum(values);
		assertEquals("Sum", 2.3314683517128287E-15d, sum);
		double[] standardizedLength;
		standardizedLength = new double[13];
		standardizedLength[0] = 2.492039173673667d;
		standardizedLength[1] = 0.46396501688721137d;
		standardizedLength[2] = 0.9643988997306225d;
		standardizedLength[3] = 0.46396501688721137d;
		standardizedLength[4] = -0.4491073307569073d;
		standardizedLength[5] = -0.27351649467149985d;
		standardizedLength[6] = 0.43762639147440024d;
		standardizedLength[7] = -0.0715870331732813d;
		standardizedLength[8] = -0.18572107662879614d;
		standardizedLength[9] = -1.0022184644259406d;
		standardizedLength[10] = -0.9319821299917778d;
		standardizedLength[11] = -1.291943343966863d;
		standardizedLength[12] = -0.6159186250380443d;
		for(int i = 0; i < values.length; i++) {
			assertEquals("StandardizedLengthValues", standardizedLength[i], values[i]);
		}
	}
}
