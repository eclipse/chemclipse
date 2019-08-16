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
package org.eclipse.chemclipse.numeric.equations;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import junit.framework.TestCase;

/**
 * Check the compilation of a linear equation.
 * 
 * @author eselmeister
 */
public class Equations_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCreateLinearEquation_1() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = new Point(7.0d, 3.0d);
		LinearEquation eq = Equations.createLinearEquation(p1, p2);
		assertEquals("X=0", 27.5d, eq.calculateY(0));
	}

	public void testCreateLinearEquation_2() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = new Point(7.0d, 3.0d);
		LinearEquation eq = Equations.createLinearEquation(p1, p2);
		assertEquals("toString()", "org.eclipse.chemclipse.numeric.equations.LinearEquation[f(x)=-3.5x + 27.5]", eq.toString());
	}

	public void testGetSlope_1() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = new Point(7.0d, 3.0d);
		assertEquals("Slope", -3.5d, Equations.calculateSlope(p1, p2));
	}

	public void testGetSlope_2() {

		IPoint p1 = null;
		IPoint p2 = new Point(7.0d, 3.0d);
		assertEquals("Slope", 0.0d, Equations.calculateSlope(p1, p2));
	}

	public void testGetSlope_3() {

		IPoint p1 = new Point(5.0d, 10.0d);
		IPoint p2 = null;
		assertEquals("Slope", 0.0d, Equations.calculateSlope(p1, p2));
	}
}
