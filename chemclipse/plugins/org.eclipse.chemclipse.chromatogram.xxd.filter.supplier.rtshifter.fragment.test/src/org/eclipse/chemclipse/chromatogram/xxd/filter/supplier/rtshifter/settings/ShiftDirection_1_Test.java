/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ShiftDirection;

import junit.framework.TestCase;

public class ShiftDirection_1_Test extends TestCase {

	private String[][] elements;

	@Override
	protected void setUp() throws Exception {

		elements = ShiftDirection.getElements();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testElements_1() {

		assertEquals(4, elements.length);
	}

	public void testElements_2() {

		String description = elements[0][0];
		String name = elements[0][1];
		assertEquals("Forward", description);
		assertEquals("FORWARD", name);
	}

	public void testElements_3() {

		String description = elements[1][0];
		String name = elements[1][1];
		assertEquals("Fast Forward", description);
		assertEquals("FAST_FORWARD", name);
	}

	public void testElements_4() {

		String description = elements[2][0];
		String name = elements[2][1];
		assertEquals("Backward", description);
		assertEquals("BACKWARD", name);
	}

	public void testElements_5() {

		String description = elements[3][0];
		String name = elements[3][1];
		assertEquals("Fast Backward", description);
		assertEquals("FAST_BACKWARD", name);
	}
}
