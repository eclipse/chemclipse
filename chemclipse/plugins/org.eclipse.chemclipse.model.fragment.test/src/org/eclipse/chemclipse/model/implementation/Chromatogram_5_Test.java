/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.core.IChromatogram;

import junit.framework.TestCase;

public class Chromatogram_5_Test extends TestCase {

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

		assertEquals("", chromatogram.getMiscInfo());
		assertEquals("", chromatogram.getMiscInfoSeparated());
	}

	public void test_2() {

		chromatogram.setMiscInfo("Hello World");
		chromatogram.setMiscInfo(null);
		assertEquals("", chromatogram.getMiscInfo());
		assertEquals("", chromatogram.getMiscInfoSeparated());
	}

	public void test_3() {

		chromatogram.setMiscInfo("Hello World");
		assertEquals("Hello World", chromatogram.getMiscInfo());
		assertEquals("", chromatogram.getMiscInfoSeparated());
	}

	public void test_4() {

		chromatogram.setMiscInfo("Hello World!How ya doing?");
		assertEquals("Hello World", chromatogram.getMiscInfo());
		assertEquals("How ya doing?", chromatogram.getMiscInfoSeparated());
	}

	public void test_5() {

		chromatogram.setMiscInfo("Hello World!How ya doing?!What a day");
		assertEquals("Hello World", chromatogram.getMiscInfo());
		assertEquals("How ya doing? What a day", chromatogram.getMiscInfoSeparated());
	}

	public void test_6() {

		chromatogram.setMiscInfo("Hello World!How ya doing?!What a day!");
		assertEquals("Hello World", chromatogram.getMiscInfo());
		assertEquals("How ya doing? What a day", chromatogram.getMiscInfoSeparated());
	}
}
