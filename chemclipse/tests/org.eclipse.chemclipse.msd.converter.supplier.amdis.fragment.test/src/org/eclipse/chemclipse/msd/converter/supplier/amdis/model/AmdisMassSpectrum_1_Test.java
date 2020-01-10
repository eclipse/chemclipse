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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.model;

import junit.framework.TestCase;

public class AmdisMassSpectrum_1_Test extends TestCase {

	private IVendorLibraryMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new VendorLibraryMassSpectrum();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetSource_1() {

		assertEquals("Source", "", massSpectrum.getSource());
	}

	public void testGetSource_2() {

		massSpectrum.setSource("file something");
		assertEquals("Source", "file something", massSpectrum.getSource());
	}

	public void testGetSource_3() {

		massSpectrum.setSource(null);
		assertEquals("Source", "", massSpectrum.getSource());
	}
}
