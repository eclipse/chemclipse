/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
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

public class ChromatogramClassifierSupplier_2_Test extends TestCase {

	private ChromatogramClassifierSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new ChromatogramClassifierSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testGetId_1() {

		assertEquals("Id", "", supplier.getId());
	}

	public void testGetDescription_1() {

		assertEquals("Description", "", supplier.getDescription());
	}

	public void testGetClassifierName_1() {

		assertEquals("Classifier Name", "", supplier.getClassifierName());
	}
}
