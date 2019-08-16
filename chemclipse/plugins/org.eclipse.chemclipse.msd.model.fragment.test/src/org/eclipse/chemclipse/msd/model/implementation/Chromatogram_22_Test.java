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

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

/**
 * @author eselmeister
 */
public class Chromatogram_22_Test extends TestCase {

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

	public void testGetConverterId_1() {

		assertEquals("", chromatogram.getConverterId());
	}

	public void testGetConverterId_2() {

		chromatogram.setConverterId(null);
		assertEquals(null, chromatogram.getConverterId());
	}

	public void testGetConverterId_3() {

		String id = "org.eclipse.chemclipse.msd.converter.supplier.test";
		chromatogram.setConverterId(id);
		assertEquals(id, chromatogram.getConverterId());
	}
}
