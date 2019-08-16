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

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

/**
 * @author eselmeister
 */
public class Chromatogram_23_Test extends TestCase {

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

	public void testGetName_1() {

		assertEquals("Chromatogram", chromatogram.getName());
	}

	public void testGetName_2() {

		chromatogram.setFile(null);
		assertEquals("Chromatogram", chromatogram.getName());
	}

	public void testGetName_3() {

		File file = new File("TestChromatogram.chrom");
		chromatogram.setFile(file);
		assertEquals("TestChromatogram.chrom", chromatogram.getName());
	}
}
