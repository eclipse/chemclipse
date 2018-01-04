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
package org.eclipse.chemclipse.msd.model.implementation;

import junit.framework.TestCase;

/**
 * Equals test.
 * 
 * @author eselmeister
 */
public class Ion_5_Test extends TestCase {

	private Ion ion1;
	private Ion ion2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ion1 = new Ion(5.2f, 4756.3f);
		ion2 = new Ion(2.2f, 4527.3f);
	}

	@Override
	protected void tearDown() throws Exception {

		ion1 = null;
		ion2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertEquals("equals", false, ion1.equals(ion2));
	}

	public void testEquals_2() {

		assertEquals("equals", false, ion2.equals(ion1));
	}

	public void testEquals_3() {

		assertEquals("equals", true, ion1.equals(ion1));
	}

	public void testEquals_4() {

		assertEquals("equals", false, ion1.equals(null));
	}

	public void testEquals_5() {

		assertEquals("equals", false, ion2.equals(new String("other object")));
	}

	public void testCompareTo_1() {

		assertTrue(1 <= ion1.compareTo(ion2));
	}

	public void testCompareTo_2() {

		assertTrue(-1 >= ion2.compareTo(ion1));
	}
}
