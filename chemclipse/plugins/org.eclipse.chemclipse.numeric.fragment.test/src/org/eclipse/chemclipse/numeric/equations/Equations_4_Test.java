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
import org.eclipse.chemclipse.numeric.exceptions.SolverException;
import junit.framework.TestCase;

/**
 * Checks calculation of intersection between two linear equations.
 * 
 * @author eselmeister
 */
public class Equations_4_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCalculateIntersection_1() {

		IPoint p11 = new Point(1, 4);
		IPoint p12 = new Point(8, 14);
		LinearEquation eq1 = Equations.createLinearEquation(p11, p12);
		IPoint p21 = new Point(6, 3);
		IPoint p22 = new Point(1, 8);
		LinearEquation eq2 = Equations.createLinearEquation(p21, p22);
		try {
			IPoint intersection = Equations.calculateIntersection(eq1, eq2);
			assertEquals("X", 2.6470588235294117d, intersection.getX());
			assertEquals("Y", 6.352941176470588d, intersection.getY());
		} catch(SolverException e) {
			assertTrue("SolverException", false);
		}
	}

	public void testCalculateIntersection_2() {

		IPoint p11 = new Point(1, 4);
		IPoint p12 = new Point(8, 14);
		LinearEquation eq1 = Equations.createLinearEquation(p11, p12);
		try {
			@SuppressWarnings("unused")
			IPoint intersection = Equations.calculateIntersection(eq1, eq1);
		} catch(SolverException e) {
			assertTrue("SolverException", true);
		}
	}

	public void testCalculateIntersection_3() {

		IPoint p11 = new Point(1, 4);
		IPoint p12 = new Point(8, 14);
		LinearEquation eq1 = Equations.createLinearEquation(p11, p12);
		IPoint p21 = new Point(1, 10);
		IPoint p22 = new Point(8, 20);
		LinearEquation eq2 = Equations.createLinearEquation(p21, p22);
		try {
			@SuppressWarnings("unused")
			IPoint intersection = Equations.calculateIntersection(eq1, eq2);
		} catch(SolverException e) {
			assertTrue("SolverException", true);
		}
	}

	public void testCalculateIntersection_4() {

		IPoint p11 = new Point(1, 4);
		IPoint p12 = new Point(8, 14);
		LinearEquation eq1 = Equations.createLinearEquation(p11, p12);
		try {
			@SuppressWarnings("unused")
			IPoint intersection = Equations.calculateIntersection(eq1, eq1);
		} catch(SolverException e) {
			assertTrue("SolverException", true);
		}
	}
}
