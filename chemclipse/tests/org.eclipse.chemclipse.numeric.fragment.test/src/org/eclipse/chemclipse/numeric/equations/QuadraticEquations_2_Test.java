/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
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

import junit.framework.TestCase;

public class QuadraticEquations_2_Test extends TestCase {

	private QuadraticEquation eq;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		eq = new QuadraticEquation(-2072.9175708325347d, 310206.81754652766d, 206670.62686893356);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testCreateLinearEquation_1() {

		double result = eq.calculateX(4192434.278134346d);
		assertEquals(14.195265251939228d, result);
	}
}