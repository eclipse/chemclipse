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
 * Check the compilation of a linear equation by multiple x,y value pairs using
 * GaussJordan fitting.
 * 
 * @author eselmeister
 */
public class Equations_2_Test extends TestCase {

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

		LinearEquation eq = Equations.createLinearEquation(points);
		assertEquals("X=0", 6.711864406779662d, eq.calculateY(0));
	}

	public void testCreateLinearEquation_2() {

		LinearEquation eq = Equations.createLinearEquation(points);
		assertEquals("toString()", "org.eclipse.chemclipse.numeric.equations.LinearEquation[f(x)=-0.7288135593220342x + 6.711864406779662]", eq.toString());
	}
}
