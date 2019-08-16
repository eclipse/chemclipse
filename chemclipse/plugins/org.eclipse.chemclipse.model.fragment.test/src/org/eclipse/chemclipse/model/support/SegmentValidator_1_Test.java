/*******************************************************************************
 * 
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
package org.eclipse.chemclipse.model.support;

import junit.framework.TestCase;

public class SegmentValidator_1_Test extends TestCase {

	private SegmentValidator validator;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		validator = new SegmentValidator();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		/*
		 * 13 values
		 * Start above mean
		 * 6 crossings
		 * false
		 */
		double[] values = new double[]{70, 70, 70, 80, 70, 100, 20, 70, 40, 80, 13, 90, 100};
		double mean = 58;
		assertFalse(validator.acceptSegment(values, mean));
	}

	public void test2() {

		/*
		 * 13 values
		 * Start above mean
		 * 7 crossings
		 * true
		 */
		double[] values = new double[]{70, 70, 70, 80, 20, 100, 20, 70, 40, 80, 13, 90, 100};
		double mean = 58;
		assertTrue(validator.acceptSegment(values, mean));
	}

	public void test3() {

		/*
		 * 13 values
		 * Start under mean
		 * 6 crossings
		 * false
		 */
		double[] values = new double[]{40, 40, 40, 80, 20, 40, 20, 30, 40, 80, 13, 90, 40};
		double mean = 58;
		assertFalse(validator.acceptSegment(values, mean));
	}

	public void test4() {

		/*
		 * 13 values
		 * Start under mean
		 * 7 crossings
		 * true
		 */
		double[] values = new double[]{40, 40, 40, 80, 20, 80, 20, 30, 40, 80, 13, 90, 40};
		double mean = 58;
		assertTrue(validator.acceptSegment(values, mean));
	}
}
