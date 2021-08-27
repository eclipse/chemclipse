/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
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

public class Point_1_Test extends TestCase {

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

	public void testPointX_1() {

		assertEquals("X", x, point.getX());
	}

	public void testPointX_2() {

		assertEquals("X", x, point.getX());
		x = 3682.234;
		point.setX(x);
		assertEquals("X", x, point.getX());
	}

	public void testPointY_1() {

		assertEquals("Y", y, point.getY());
	}

	public void testPointY_2() {

		assertEquals("Y", y, point.getY());
		y = 8273.3;
		point.setY(y);
		assertEquals("Y", y, point.getY());
	}
}
