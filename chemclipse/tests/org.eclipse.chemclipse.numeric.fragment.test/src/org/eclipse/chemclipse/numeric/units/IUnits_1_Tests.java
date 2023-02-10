/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
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

		assertEquals("g", Units.GRAM);
	}

	public void test2() {

		assertEquals("kg", Units.KILOGRAM);
	}

	public void test3() {

		assertEquals("ml", Units.MILILITER);
	}

	public void test4() {

		assertEquals("l", Units.LITER);
	}

	public void test5() {

		assertEquals("mg/kg", Units.MILIGRAM_PER_KILOGRAM);
	}

	public void test6() {

		assertEquals("mg/l", Units.MILIGRAM_PER_LITER);
	}
}
