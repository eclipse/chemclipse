/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
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
 * Check the compilation of a quadratic equation by multiple x,y value pairs
 * using GaussJordan fitting.
 * 
 * @author eselmeister
 */
public class Equations_3_Test extends TestCase {

	private IPoint[] points;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		points = new IPoint[4];
		points[0] = new Point(4.0d, 4.0d);
		points[1] = new Point(7.0d, 1.0d);
		points[2] = new Point(6.0d, 3.0d);
		points[3] = new Point(2.0d, 5.0d);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCreateLinearEquation_1() {

		IQuadraticEquation eq = Equations.createQuadraticEquation(points);
		assertEquals("X=0", 4.371859296482454d, eq.calculateY(0));
	}

	public void testCreateLinearEquation_2() {

		IQuadraticEquation eq = Equations.createQuadraticEquation(points);
		assertEquals("toString()", "org.eclipse.chemclipse.numeric.equations.QuadraticEquation[f(x)=-0.1432160804020076x^2 + 0.5552763819095254x + 4.371859296482454]", eq.toString());
	}

	public void testCreateLinearEquation_3() {

		IQuadraticEquation equation = Equations.createQuadraticEquation(points);
		assertEquals("getApexValueForX Apex.NEGATIVE", 7.793890860025901d, equation.getApexValueForX(Apex.POSITIVE));
	}

	public void testCreateLinearEquation_4() {

		IQuadraticEquation eq = Equations.createQuadraticEquation(points);
		assertEquals("getApexValueForX Apex.NEGATIVE", -3.9166978775698498d, eq.getApexValueForX(Apex.NEGATIVE));
	}
}