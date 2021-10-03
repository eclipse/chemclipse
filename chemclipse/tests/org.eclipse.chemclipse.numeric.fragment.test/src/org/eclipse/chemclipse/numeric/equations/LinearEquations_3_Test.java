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
package org.eclipse.chemclipse.numeric.equations;

import static org.junit.Assert.assertNotEquals;

import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;

import junit.framework.TestCase;

public class LinearEquations_3_Test extends TestCase {

	private LinearEquation eq1;
	private LinearEquation eq2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		IPoint p11 = new Point(1, 4);
		IPoint p12 = new Point(8, 14);
		eq1 = Equations.createLinearEquation(p11, p12);
		IPoint p21 = new Point(6, 3);
		IPoint p22 = new Point(1, 8);
		eq2 = Equations.createLinearEquation(p21, p22);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testEquals_1() {

		assertNotEquals("equals", eq1, eq2);
	}

	public void testEquals_2() {

		assertNotEquals("equals", eq2, eq1);
	}

	public void testEquals_3() {

		assertNotNull("equals", eq1);
	}

	public void testEquals_4() {

		assertNotEquals("equals", eq1, new Object());
	}
}
