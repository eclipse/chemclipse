/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.implementation;

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

		super.tearDown();
	}

	public void test1() {

		assertEquals(0, libraryInformation.getRetentionTime());
	}

	public void test2() {

		assertEquals(0.0f, libraryInformation.getRetentionIndex());
	}

	public void test3() {

		libraryInformation.setRetentionTime(1000);
		assertEquals(1000, libraryInformation.getRetentionTime());
	}

	public void test4() {

		libraryInformation.setRetentionIndex(1293.5f);
		assertEquals(1293.5f, libraryInformation.getRetentionIndex());
	}
}
