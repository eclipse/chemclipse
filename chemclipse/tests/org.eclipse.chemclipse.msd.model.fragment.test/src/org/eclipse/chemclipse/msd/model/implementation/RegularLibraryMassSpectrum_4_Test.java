/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

import junit.framework.TestCase;

public class RegularLibraryMassSpectrum_4_Test extends TestCase {

	private IRegularLibraryMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new RegularLibraryMassSpectrum();
		massSpectrum.setPrecursorIon(127.764d);
		massSpectrum.setNeutralMass(127.764d);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void test1() {

		assertNull(massSpectrum.getPrecursorType());
	}

	public void test2() {

		assertEquals(127.764d, massSpectrum.getPrecursorIon());
	}

	public void test3() {

		assertEquals(127.764d, massSpectrum.getNeutralMass());
	}

	public void test4() {

		assertEquals("", massSpectrum.getPolarity());
	}

	public void test5() {

		assertEquals(0, massSpectrum.getPropertyKeySet().size());
	}

	public void test6() {

		assertEquals("", massSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_PRECURSOR_TYPE));
	}

	public void test7() {

		assertEquals("", massSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_COLLISION_ENERGY));
	}

	public void test8() {

		assertEquals("", massSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_INSTRUMENT_NAME));
	}
}