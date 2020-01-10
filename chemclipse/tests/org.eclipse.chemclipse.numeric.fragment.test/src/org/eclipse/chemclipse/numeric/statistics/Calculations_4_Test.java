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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Testing median.
 * 
 * @author eselmeister
 */
public class Calculations_4_Test extends TestCase {

	private double[] values;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		values = new double[12];
		values[0] = 735.0d;
		values[1] = 504.0d;
		values[2] = 561.0d;
		values[3] = 504.0d;
		values[4] = 400.0d;
		values[5] = 501.0d;
		values[6] = 443.0d;
		values[7] = 430.0d;
		values[8] = 337.0d;
		values[9] = 345.0d;
		values[10] = 304.0d;
		values[11] = 381.0d;
	}

	@Override
	protected void tearDown() throws Exception {

		values = null;
		super.tearDown();
	}

	public void testGetMedian_1() {

		double result = Calculations.getMedian(values);
		assertEquals("getMedian", 436.5d, result);
	}

	public void testGetMedian_2() {

		List<Double> val = new ArrayList<Double>();
		for(double value : values) {
			val.add(value);
		}
		double result = Calculations.getMedian(val);
		assertEquals("getMedian", 436.5d, result);
	}

	public void testGetMedian_3() {

		List<Double> val = new ArrayList<Double>();
		/*
		 * Add null to test null values.<br/> To preserve the median, a higher
		 * value than the existing max value needs to be added too.<br/> Add in
		 * this case 780.0d.
		 */
		val.add(null);
		for(double value : values) {
			val.add(value);
		}
		val.add(780.0d);
		double result = Calculations.getMedian(val);
		assertEquals("getMedian", 436.5d, result);
	}
}
