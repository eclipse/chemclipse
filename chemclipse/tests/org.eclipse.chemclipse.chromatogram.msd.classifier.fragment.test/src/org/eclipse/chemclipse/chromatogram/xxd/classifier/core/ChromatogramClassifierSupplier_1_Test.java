/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.core;

import junit.framework.TestCase;

public class ChromatogramClassifierSupplier_1_Test extends TestCase {

	private ChromatogramClassifierSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new ChromatogramClassifierSupplier();
		supplier.setId("org.eclipse.chemclipse.test");
		supplier.setDescription("This is a description.");
		supplier.setClassifierName("Classifier Name");
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testGetId_1() {

		assertEquals("Id", "org.eclipse.chemclipse.test", supplier.getId());
	}

	public void testGetDescription_1() {

		assertEquals("Description", "This is a description.", supplier.getDescription());
	}

	public void testGetClassifierName_1() {

		assertEquals("Classifier Name", "Classifier Name", supplier.getClassifierName());
	}
}