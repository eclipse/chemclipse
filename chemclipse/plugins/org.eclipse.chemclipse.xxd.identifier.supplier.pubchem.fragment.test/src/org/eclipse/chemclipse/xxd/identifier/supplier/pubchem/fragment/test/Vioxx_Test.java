/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.fragment.test;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.xxd.identifier.supplier.pubchem.rest.PowerUserGateway;

import junit.framework.TestCase;

public class Vioxx_Test extends TestCase {

	ILibraryInformation libraryInformation;

	@Override
	public void setUp() {

		libraryInformation = new LibraryInformation();
		libraryInformation.setName("Vioxx");
	}

	public void testSMILES() {

		String smiles = PowerUserGateway.getCanonicalSMILES(libraryInformation);
		assertEquals("CS(=O)(=O)C1=CC=C(C=C1)C2=C(C(=O)OC2)C3=CC=CC=C3", smiles);
	}

	public void testCID() {

		assertEquals(Integer.valueOf(5090), PowerUserGateway.getCIDS(libraryInformation).get(0));
	}
}
