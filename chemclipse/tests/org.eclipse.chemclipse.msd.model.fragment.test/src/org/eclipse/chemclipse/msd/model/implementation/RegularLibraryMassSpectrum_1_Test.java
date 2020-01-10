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

import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import junit.framework.TestCase;

public class RegularLibraryMassSpectrum_1_Test extends TestCase {

	private IRegularLibraryMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectrum = new RegularLibraryMassSpectrum();
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	public void testGetName_1() {

		assertEquals("Name", "", massSpectrum.getLibraryInformation().getName());
	}

	public void testGetName_2() {

		massSpectrum.getLibraryInformation().setName("Philip");
		assertEquals("Name", "Philip", massSpectrum.getLibraryInformation().getName());
	}

	public void testGetName_3() {

		massSpectrum.getLibraryInformation().setName(null);
		assertEquals("Name", "", massSpectrum.getLibraryInformation().getName());
	}

	public void testGetComments_1() {

		assertEquals("Comments", "", massSpectrum.getLibraryInformation().getComments());
	}

	public void testGetComments_2() {

		massSpectrum.getLibraryInformation().setComments("Here are the test comments");
		assertEquals("Comments", "Here are the test comments", massSpectrum.getLibraryInformation().getComments());
	}

	public void testGetComments_3() {

		massSpectrum.getLibraryInformation().setComments(null);
		assertEquals("Comments", "", massSpectrum.getLibraryInformation().getComments());
	}

	public void testGetCasNumber_1() {

		assertEquals("CAS Number", "", massSpectrum.getLibraryInformation().getCasNumber());
	}

	public void testGetCasNumber_2() {

		massSpectrum.getLibraryInformation().setCasNumber("56-38-XX");
		assertEquals("CAS Number", "56-38-XX", massSpectrum.getLibraryInformation().getCasNumber());
	}

	public void testGetCasNumber_3() {

		massSpectrum.getLibraryInformation().setCasNumber(null);
		assertEquals("CAS Number", "", massSpectrum.getLibraryInformation().getCasNumber());
	}
}
