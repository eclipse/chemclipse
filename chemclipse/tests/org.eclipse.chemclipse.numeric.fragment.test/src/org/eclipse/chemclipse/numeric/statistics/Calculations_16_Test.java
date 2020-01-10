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
public class Calculations_16_Test extends TestCase {

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

	public void testScaleToEuclidianLength_1() {

		Calculations.scaleToEuclidianLength(values);
		double sum = Calculations.getSum(values);
		assertEquals("Sum", 3.503944986207772d, sum);
		double[] euclidianLength;
		euclidianLength = new double[13];
		euclidianLength[0] = 0.4391133102920226d;
		euclidianLength[1] = 0.30110626991452977d;
		euclidianLength[2] = 0.33515995520248254d;
		euclidianLength[3] = 0.30110626991452977d;
		euclidianLength[4] = 0.23897323009089666d;
		euclidianLength[5] = 0.2509218915954415d;
		euclidianLength[6] = 0.2993139706888481d;
		euclidianLength[7] = 0.26466285232566805d;
		euclidianLength[8] = 0.2568962223477139d;
		euclidianLength[9] = 0.20133494635158045d;
		euclidianLength[10] = 0.20611441095339836d;
		euclidianLength[11] = 0.18161965486908146d;
		euclidianLength[12] = 0.22762200166157906d;
		for(int i = 0; i < values.length; i++) {
			assertEquals("EuclidianLengthValues", euclidianLength[i], values[i]);
		}
	}
}
