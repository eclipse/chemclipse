/*******************************************************************************
 * Copyright (c) 2011, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

/**
 * @author eselmeister
 */
public class Chromatogram_25_Test extends TestCase {

	private IChromatogramMSD chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
		chromatogram.setChromatogramIntegratedArea(null, "SomeTest");
		chromatogram.setBackgroundIntegratedArea(null, "SomeTest");
	}

	@Override
	protected void tearDown() throws Exception {

		chromatogram = null;
		super.tearDown();
	}

	public void testGetChromatogramIntegratedArea_1() {

		assertEquals(0.0d, chromatogram.getChromatogramIntegratedArea());
	}

	public void testGetChromatogramIntegratorDescription_1() {

		assertEquals("", chromatogram.getChromatogramIntegratorDescription());
	}

	public void testGetBackgroundIntegratedArea_1() {

		assertEquals(0.0d, chromatogram.getBackgroundIntegratedArea());
	}

	public void testGetBackgroundIntegratorDescription_1() {

		assertEquals("", chromatogram.getBackgroundIntegratorDescription());
	}
}
