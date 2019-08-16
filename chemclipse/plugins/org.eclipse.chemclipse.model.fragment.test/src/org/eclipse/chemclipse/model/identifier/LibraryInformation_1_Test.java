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
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;

import junit.framework.TestCase;

public class LibraryInformation_1_Test extends TestCase {

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

	public void testGetName_1() {

		assertEquals("Name", "", libraryInformation.getName());
	}

	public void testGetName_2() {

		libraryInformation.setName("Philip");
		assertEquals("Name", "Philip", libraryInformation.getName());
	}

	public void testGetName_3() {

		libraryInformation.setName(null);
		assertEquals("Name", "", libraryInformation.getName());
	}

	public void testGetComments_1() {

		assertEquals("Comments", "", libraryInformation.getComments());
	}

	public void testGetComments_2() {

		libraryInformation.setComments("Here are the test comments");
		assertEquals("Comments", "Here are the test comments", libraryInformation.getComments());
	}

	public void testGetComments_3() {

		libraryInformation.setComments(null);
		assertEquals("Comments", "", libraryInformation.getComments());
	}

	public void testGetCasNumber_1() {

		assertEquals("CAS Number", "", libraryInformation.getCasNumber());
	}

	public void testGetCasNumber_2() {

		libraryInformation.setCasNumber("56-38-XX");
		assertEquals("CAS Number", "56-38-XX", libraryInformation.getCasNumber());
	}

	public void testGetCasNumber_3() {

		libraryInformation.setCasNumber(null);
		assertEquals("CAS Number", "", libraryInformation.getCasNumber());
	}
}
