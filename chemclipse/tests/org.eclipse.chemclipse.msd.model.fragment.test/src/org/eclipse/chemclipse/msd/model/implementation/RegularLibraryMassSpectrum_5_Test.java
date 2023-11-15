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

public class RegularLibraryMassSpectrum_5_Test extends TestCase {

	private IRegularLibraryMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new RegularLibraryMassSpectrum();
		massSpectrum.putProperty(IRegularLibraryMassSpectrum.PROPERTY_PRECURSOR_TYPE, "[M+H]+");
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

		assertEquals(294.14556d, massSpectrum.getNeutralMass(295.1535d));
	}

	public void test3() {

		assertEquals("+", massSpectrum.getPolarity());
	}

	public void test4() {

		assertEquals(1, massSpectrum.getPropertyKeySet().size());
	}

	public void test5() {

		assertEquals("[M+H]+", massSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_PRECURSOR_TYPE));
	}
}