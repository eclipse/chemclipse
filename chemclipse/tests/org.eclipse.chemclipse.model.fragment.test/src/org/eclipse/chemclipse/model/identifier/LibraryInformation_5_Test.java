/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import junit.framework.TestCase;

public class LibraryInformation_5_Test extends TestCase {

	private ILibraryInformation libraryInformation;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		libraryInformation = new LibraryInformation();
	}

	@Override
	protected void tearDown() throws Exception {

		libraryInformation = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals("", libraryInformation.getCasNumber());
	}

	public void test_2() {

		assertEquals(0, libraryInformation.getCasNumbers().size());
	}

	public void test_3() {

		libraryInformation.setCasNumber("100–52-7");
		assertEquals("100–52-7", libraryInformation.getCasNumber());
	}

	public void test_4() {

		libraryInformation.addCasNumber("100–52-7");
		assertEquals("100–52-7", libraryInformation.getCasNumber());
	}

	public void test_5() {

		libraryInformation.setCasNumber("100–52-7");
		libraryInformation.addCasNumber("103-36-6");
		libraryInformation.addCasNumber("103-45-7");
		assertEquals("100–52-7", libraryInformation.getCasNumber());
	}

	public void test_6() {

		libraryInformation.setCasNumber("100–52-7");
		libraryInformation.addCasNumber("103-36-6");
		libraryInformation.addCasNumber("103-45-7");
		libraryInformation.deleteCasNumber("100–52-7");
		assertEquals("103-36-6", libraryInformation.getCasNumber());
	}

	public void test_7() {

		libraryInformation.addCasNumber("103-36-6");
		libraryInformation.addCasNumber("103-45-7");
		libraryInformation.setCasNumber("100–52-7");
		assertEquals("100–52-7", libraryInformation.getCasNumber());
	}

	public void test_8() {

		libraryInformation.addCasNumber(null);
		libraryInformation.addCasNumber("100–52-7");
		libraryInformation.addCasNumber("103-36-6");
		libraryInformation.addCasNumber("103-36-6"); // duplicate
		libraryInformation.addCasNumber("103-45-7");
		assertEquals(3, libraryInformation.getCasNumbers().size());
		libraryInformation.deleteCasNumber(null);
		assertEquals(3, libraryInformation.getCasNumbers().size());
		libraryInformation.deleteCasNumber("100–52-7");
		assertEquals(2, libraryInformation.getCasNumbers().size());
		libraryInformation.clearCasNumbers();
		assertEquals(0, libraryInformation.getCasNumbers().size());
		assertEquals("", libraryInformation.getCasNumber());
	}
}