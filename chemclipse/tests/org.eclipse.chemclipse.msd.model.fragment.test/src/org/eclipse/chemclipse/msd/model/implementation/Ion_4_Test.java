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
package org.eclipse.chemclipse.msd.model.implementation;

import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

/**
 * Equals test.
 * 
 * @author Philip Wenig
 */
public class Ion_4_Test extends TestCase {

	private Ion ion1;
	private Ion ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(2.2f, 4527.3f);
		ion2 = new Ion(5.2f, 4756.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertNotEquals("equals", ion1, ion2);
	}

	public void testEquals_2() {

		assertNotEquals("equals", ion2, ion1);
	}

	public void testEquals_3() {

		assertEquals("equals", ion1, ion1);
	}

	public void testEquals_4() {

		assertNotNull("equals", ion1);
	}

	public void testEquals_5() {

		assertNotEquals("equals", ion2, new Object());
	}

	public void testCompareTo_1() {

		assertTrue(-1 >= ion1.compareTo(ion2));
	}

	public void testCompareTo_2() {

		assertTrue(1 <= ion2.compareTo(ion1));
	}
}
