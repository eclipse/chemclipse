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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;

import junit.framework.TestCase;

public class LibraryInformation_2_Test extends TestCase {

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

	public void testGetSynonyms_1() {

		assertNotNull(libraryInformation.getSynonyms());
	}

	public void testGetSynonyms_2() {

		libraryInformation.setSynonyms(null);
		assertNotNull(libraryInformation.getSynonyms());
	}

	public void testGetSynonyms_3() {

		assertEquals(0, libraryInformation.getSynonyms().size());
	}

	public void testGetSynonyms_4() {

		libraryInformation.setSynonyms(null);
		assertEquals(0, libraryInformation.getSynonyms().size());
	}

	public void testGetSynonyms_5() {

		Set<String> synonyms = new HashSet<String>();
		synonyms.add("Philip");
		synonyms.add("is");
		synonyms.add("the");
		synonyms.add("founder");
		synonyms.add("of");
		synonyms.add("ChemClipse.");
		libraryInformation.setSynonyms(synonyms);
		assertEquals(6, libraryInformation.getSynonyms().size());
	}
}
