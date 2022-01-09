/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import junit.framework.TestCase;

public class DeltaCalculationSupport_1_Test extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		double unknown = 2000.0d;
		double reference = 2000.0d;
		double delta = 0.0d;
		assertTrue(DeltaCalculationSupport.useTarget(unknown, reference, delta));
	}

	public void test2() {

		double unknown = 2000.0d;
		double reference = 1990.0d;
		double delta = 10.0d;
		assertTrue(DeltaCalculationSupport.useTarget(unknown, reference, delta));
	}

	public void test3() {

		double unknown = 2000.0d;
		double reference = 2010.0d;
		double delta = 10.0d;
		assertTrue(DeltaCalculationSupport.useTarget(unknown, reference, delta));
	}

	public void test4() {

		double unknown = 2000.0d;
		double reference = 1989.0d;
		double delta = 10.0d;
		assertFalse(DeltaCalculationSupport.useTarget(unknown, reference, delta));
	}

	public void test5() {

		double unknown = 2000.0d;
		double reference = 2011.0d;
		double delta = 10.0d;
		assertFalse(DeltaCalculationSupport.useTarget(unknown, reference, delta));
	}
}