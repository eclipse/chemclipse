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
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.implementation.IdentificationTarget;

import junit.framework.TestCase;

public class IdentificationTarget_1_Test extends TestCase {

	private IdentificationTarget identificationTarget;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		ILibraryInformation libraryInformation = new LibraryInformation();
		IComparisonResult comparisonResult = new ComparisonResult(100.0f, 100.0f, 0.0f, 0.0f);
		identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
	}

	@Override
	protected void tearDown() throws Exception {

		identificationTarget = null;
		super.tearDown();
	}

	public void test1() {

		assertEquals("", identificationTarget.getIdentifier());
	}

	public void test2() {

		identificationTarget.setIdentifier("");
		assertEquals("", identificationTarget.getIdentifier());
	}

	public void test3() {

		identificationTarget.setIdentifier("ChemClipse");
		assertEquals("ChemClipse", identificationTarget.getIdentifier());
	}

	public void test4() {

		assertEquals(false, identificationTarget.isManuallyVerified());
	}

	public void test5() {

		identificationTarget.setManuallyVerified(false);
		assertEquals(false, identificationTarget.isManuallyVerified());
	}

	public void test6() {

		identificationTarget.setManuallyVerified(true);
		assertEquals(true, identificationTarget.isManuallyVerified());
	}
}
