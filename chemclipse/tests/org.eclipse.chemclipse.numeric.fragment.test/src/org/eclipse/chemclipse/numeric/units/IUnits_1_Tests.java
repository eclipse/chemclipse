/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.numeric.units;

import junit.framework.TestCase;

public class IUnits_1_Tests extends TestCase {

	@Override
	protected void setUp() throws Exception {

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals("g", IUnits.g);
	}

	public void test2() {

		assertEquals("kg", IUnits.kg);
	}

	public void test3() {

		assertEquals("ml", IUnits.ml);
	}

	public void test4() {

		assertEquals("l", IUnits.l);
	}

	public void test5() {

		assertEquals("mg/kg", IUnits.mg_per_kg);
	}

	public void test6() {

		assertEquals("mg/l", IUnits.mg_per_liter);
	}
}
