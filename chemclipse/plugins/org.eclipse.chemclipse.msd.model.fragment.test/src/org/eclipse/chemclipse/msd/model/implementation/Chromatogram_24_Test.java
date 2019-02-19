/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class Chromatogram_24_Test extends TestCase {

	private IChromatogramMSD chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		chromatogram = new ChromatogramMSD();
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

		assertEquals("", chromatogram.getIntegratorDescription());
	}

	public void testGetChromatogramIntegratorDescription_2() {

		chromatogram.setIntegratorDescription(null);
		assertEquals("", chromatogram.getIntegratorDescription());
	}

	public void testGetChromatogramIntegratorDescription_3() {

		chromatogram.setIntegratorDescription("ChromatogramIntegrator");
		assertEquals("ChromatogramIntegrator", chromatogram.getIntegratorDescription());
	}

	public void testGetBackgroundIntegratedArea_1() {

		assertEquals(0.0d, chromatogram.getBackgroundIntegratedArea());
	}

	public void testGetBackgroundIntegratorDescription_1() {

		assertEquals("", chromatogram.getIntegratorDescription());
	}

	public void testGetBackgroundIntegratorDescription_2() {

		chromatogram.setIntegratorDescription(null);
		assertEquals("", chromatogram.getIntegratorDescription());
	}

	public void testGetBackgroundIntegratorDescription_3() {

		chromatogram.setIntegratorDescription("BackgroundIntegrator");
		assertEquals("BackgroundIntegrator", chromatogram.getIntegratorDescription());
	}
}
