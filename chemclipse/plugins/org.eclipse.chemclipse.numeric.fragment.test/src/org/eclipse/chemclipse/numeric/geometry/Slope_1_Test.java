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
package org.eclipse.chemclipse.numeric.geometry;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import junit.framework.TestCase;

public class Slope_1_Test extends TestCase {

	ISlope slope;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetSlope_1() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = new Point(7.0d, 3.0d);
		slope = new Slope(p1, p2);
		assertEquals("Slope", -3.5d, slope.getSlope());
	}

	public void testGetSlope_2() {

		IPoint p1 = null;
		IPoint p2 = new Point(7.0d, 3.0d);
		slope = new Slope(p1, p2);
		assertEquals("Slope", 0.0d, slope.getSlope());
	}

	public void testGetSlope_3() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = null;
		slope = new Slope(p1, p2);
		assertEquals("Slope", 0.0d, slope.getSlope());
	}

	public void testGetSlope_4() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = null;
		slope = new Slope(p1, p2);
		slope.setSlope(4.6d);
		assertEquals("Slope", 4.6d, slope.getSlope());
	}
}
