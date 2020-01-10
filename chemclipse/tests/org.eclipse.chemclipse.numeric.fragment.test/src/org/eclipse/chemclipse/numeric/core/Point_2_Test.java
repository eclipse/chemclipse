/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.core;

import junit.framework.TestCase;

public class Point_2_Test extends TestCase {

	private IPoint point;
	private double x = 25.3;
	private double y = 457.7;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		point = new Point(x, y);
	}

	@Override
	protected void tearDown() throws Exception {

		point = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals(25.3d, point.getX());
	}

	public void test_2() {

		assertEquals(457.7d, point.getY());
	}

	public void test_3() {

		IPoint point2 = new Point(25.3d, 457.7d);
		assertTrue(point.equals(point2));
	}

	public void test_4() {

		IPoint point2 = new Point(25.3d, 457.7d);
		assertTrue(point2.equals(point));
	}

	public void test_5() {

		IPoint point2 = new Point(25.4d, 457.7d);
		assertFalse(point.equals(point2));
	}

	public void test_6() {

		IPoint point2 = new Point(25.4d, 457.7d);
		assertFalse(point2.equals(point));
	}

	public void test_7() {

		IPoint point2 = new Point(25.3d, 457.6d);
		assertFalse(point.equals(point2));
	}

	public void test_8() {

		IPoint point2 = new Point(25.3d, 457.6d);
		assertFalse(point2.equals(point));
	}

	public void test_9() {

		IPoint point2 = new Point(25.2d, 457.8d);
		assertFalse(point.equals(point2));
	}

	public void test_10() {

		IPoint point2 = new Point(25.2d, 457.8d);
		assertFalse(point2.equals(point));
	}
}
