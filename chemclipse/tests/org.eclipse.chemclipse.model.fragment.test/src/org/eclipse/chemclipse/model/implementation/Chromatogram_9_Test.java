/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
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

public class Chromatogram_9_Test extends TestCase {

	private Chromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new Chromatogram();
		chromatogram.setConverterId("hello.world.converter");
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void test1() {

		assertEquals("hello.world.converter", chromatogram.getConverterId());
	}

	public void test2() {

		chromatogram.setFinalized(true);
		assertEquals("", chromatogram.getConverterId());
	}

	public void test3() {

		chromatogram.setFinalized(true);
		chromatogram.setFinalized(false);
		assertEquals("hello.world.converter", chromatogram.getConverterId());
	}
}