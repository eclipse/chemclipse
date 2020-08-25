/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import junit.framework.TestCase;

public class Chromatogram_8_Test extends TestCase {

	private Chromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
		chromatogram.setActiveBaseline("MyBaseline");
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void test1() {

		assertNotNull(chromatogram.getBaselineModel());
	}

	public void test2() {

		assertEquals("MyBaseline", chromatogram.getActiveBaseline());
	}

	public void test3() {

		assertEquals(2, chromatogram.getBaselineIds().size());
	}

	public void test4() {

		assertTrue(chromatogram.getBaselineIds().contains("Default"));
		assertTrue(chromatogram.getBaselineIds().contains("MyBaseline"));
	}

	public void test5() {

		chromatogram.setActiveBaseline(null);
		assertEquals("MyBaseline", chromatogram.getActiveBaseline());
	}

	public void test6() {

		chromatogram.setActiveBaseline("");
		assertEquals("MyBaseline", chromatogram.getActiveBaseline());
	}

	public void test7() {

		assertEquals(2, chromatogram.getBaselineIds().size());
		chromatogram.removeBaseline("");
		assertEquals("MyBaseline", chromatogram.getActiveBaseline());
		chromatogram.removeBaseline("MyBaseline");
		assertEquals(1, chromatogram.getBaselineIds().size());
		assertEquals("Default", chromatogram.getActiveBaseline());
	}

	public void test8() {

		assertEquals(2, chromatogram.getBaselineIds().size());
		chromatogram.removeBaseline(null);
		assertEquals("MyBaseline", chromatogram.getActiveBaseline());
		chromatogram.removeBaseline("MyBaseline");
		assertEquals(1, chromatogram.getBaselineIds().size());
		assertEquals("Default", chromatogram.getActiveBaseline());
	}
}
