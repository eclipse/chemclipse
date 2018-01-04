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
package org.eclipse.chemclipse.msd.converter.io;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

import junit.framework.TestCase;

public class MassSpectraReader_1_Test extends TestCase {

	private IMassSpectraReader massSpectraReader;
	private IRegularLibraryMassSpectrum massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		massSpectraReader = new MassSpectraReader_Test_Impl();
		massSpectrum = new RegularLibraryMassSpectrum_Test_Impl();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		String name = null;
		String referenceIdentifierMarker = null;
		String referenceIdentifierPrefix = null;
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("", libraryInformation.getName());
		assertEquals("", libraryInformation.getReferenceIdentifier());
	}

	public void test2() {

		String name = "";
		String referenceIdentifierMarker = "";
		String referenceIdentifierPrefix = "";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("", libraryInformation.getName());
		assertEquals("", libraryInformation.getReferenceIdentifier());
	}

	public void test3() {

		String name = "ChemClipse";
		String referenceIdentifierMarker = "";
		String referenceIdentifierPrefix = "";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse", libraryInformation.getName());
		assertEquals("", libraryInformation.getReferenceIdentifier());
	}

	public void test4() {

		String name = "ChemClipse/CID7829";
		String referenceIdentifierMarker = "/CID";
		String referenceIdentifierPrefix = "ID-";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse", libraryInformation.getName());
		assertEquals("ID-7829", libraryInformation.getReferenceIdentifier());
	}

	public void test5() {

		String name = "ChemClipse/CID7829";
		String referenceIdentifierMarker = "/CID";
		String referenceIdentifierPrefix = "";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse", libraryInformation.getName());
		assertEquals("7829", libraryInformation.getReferenceIdentifier());
	}

	public void test6() {

		String name = "ChemClipse/CID7829";
		String referenceIdentifierMarker = null;
		String referenceIdentifierPrefix = "";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse/CID7829", libraryInformation.getName());
		assertEquals("", libraryInformation.getReferenceIdentifier());
	}

	public void test7() {

		String name = "ChemClipse/CID7829";
		String referenceIdentifierMarker = "/CID";
		String referenceIdentifierPrefix = null;
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse", libraryInformation.getName());
		assertEquals("7829", libraryInformation.getReferenceIdentifier());
	}

	public void test8() {

		String name = "ChemClipse/CID7829/CID589/CIDhello";
		String referenceIdentifierMarker = "/CID";
		String referenceIdentifierPrefix = "";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse", libraryInformation.getName());
		assertEquals("7829 589 hello", libraryInformation.getReferenceIdentifier());
	}

	public void test9() {

		String name = "ChemClipse/CID7829/CID589/CIDhello";
		String referenceIdentifierMarker = "/CID";
		String referenceIdentifierPrefix = "D-";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("ChemClipse", libraryInformation.getName());
		assertEquals("D-7829 589 hello", libraryInformation.getReferenceIdentifier());
	}

	public void test10() {

		String name = "/CID7829";
		String referenceIdentifierMarker = "/CID";
		String referenceIdentifierPrefix = "ID-";
		//
		massSpectraReader.extractNameAndReferenceIdentifier(massSpectrum, name, referenceIdentifierMarker, referenceIdentifierPrefix);
		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		assertEquals("", libraryInformation.getName());
		assertEquals("ID-7829", libraryInformation.getReferenceIdentifier());
	}
}
