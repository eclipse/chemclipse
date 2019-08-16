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
 * Tests the methods equals and hashCode.
 * 
 * @author eselmeister
 */
public class Chromatogram_1_Test extends TestCase {

	private ChromatogramMSD chromatogram1;
	private ChromatogramMSD chromatogram2;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram1 = new ChromatogramMSD();
		chromatogram2 = new ChromatogramMSD();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram1 = null;
		chromatogram2 = null;
		super.tearDown();
	}

	public void testEquals_1() {

		assertTrue(chromatogram1.equals(chromatogram2));
	}

	public void testEquals_2() {

		assertTrue(chromatogram2.equals(chromatogram1));
	}

	public void testEquals_3() {

		assertFalse(chromatogram1.equals(null));
	}

	public void testEquals_4() {

		assertFalse(chromatogram2.equals(null));
	}

	public void testEquals_5() {

		assertFalse(chromatogram1.equals(new String("")));
	}

	public void testEquals_6() {

		assertFalse(chromatogram2.equals(new String("")));
	}

	public void testHashCode_1() {

		assertEquals(chromatogram1.hashCode(), chromatogram2.hashCode());
	}

	public void testHashCode_2() {

		assertEquals(chromatogram2.hashCode(), chromatogram1.hashCode());
	}
}
