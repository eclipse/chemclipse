/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

public class Point_3_Test extends TestCase {

	private Point point;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		point = new Point(0, 0);
		assertEquals(0.0d, point.getX());
		assertEquals(0.0d, point.getY());
	}

	public void test2() {

		point = new Point(1.5d, 0);
		assertEquals(1.5d, point.getX());
		assertEquals(0.0d, point.getY());
	}

	public void test3() {

		point = new Point(0, 1.5d);
		assertEquals(0.0d, point.getX());
		assertEquals(1.5d, point.getY());
	}

	public void test4() {

		point = new Point(-1.5d, 0);
		assertEquals(-1.5d, point.getX());
		assertEquals(0.0d, point.getY());
	}

	public void test5() {

		point = new Point(0, -1.5d);
		assertEquals(0.0d, point.getX());
		assertEquals(-1.5d, point.getY());
	}
}
