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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.support.ITwoPoints;
import org.eclipse.chemclipse.model.support.TwoPoints;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.LinearEquation;
import junit.framework.TestCase;

public class TwoPoints_1_Test extends TestCase {

	private IPoint p1;
	private IPoint p2;
	private ITwoPoints points;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		p1 = new Point(5.0d, 10.0d);
		p2 = new Point(7.0d, 3.0d);
		points = new TwoPoints(p1, p2);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetLinearEquation_1() {

		LinearEquation eq = points.getLinearEquation();
		assertEquals("X=0", 27.5d, eq.calculateY(0));
	}

	public void testGetSlope_1() {

		LinearEquation eq = points.getLinearEquation();
		double slope = points.getSlope();
		assertEquals("Slope", eq.getA(), slope);
	}

	public void testGetPoint_1() {

		assertEquals("Point1", 5.0d, points.getPoint1().getX());
	}

	public void testGetPoint_2() {

		assertEquals("Point1", 10.0d, points.getPoint1().getY());
	}

	public void testGetPoint_3() {

		assertEquals("Point2", 7.0d, points.getPoint2().getX());
	}

	public void testGetPoint_4() {

		assertEquals("Point2", 3.0d, points.getPoint2().getY());
	}
}
