/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

import java.util.Map;

import org.eclipse.chemclipse.model.core.IChromatogram;

import junit.framework.TestCase;

public class Chromatogram_6_Test extends TestCase {

	private IChromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void test_1() {

		assertNotNull(chromatogram.getMiscellaneous());
	}

	public void test_2() {

		assertEquals(0, chromatogram.getMiscellaneous().size());
	}

	public void test_3() {

		Map<String, String> miscellaneous = chromatogram.getMiscellaneous();
		chromatogram.getMiscellaneous().put("Test", "This is a test case.");
		assertEquals("This is a test case.", miscellaneous.get("Test"));
	}

	public void test_4() {

		chromatogram.getMiscellaneous().put("Test", "This is a test case.");
		Map<String, String> miscellaneous = chromatogram.getMiscellaneous();
		assertEquals("This is a test case.", miscellaneous.get("Test"));
	}
}
