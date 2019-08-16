/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram;

import junit.framework.TestCase;

public class ChromatogramFilterSupplier_1_Test extends TestCase {

	private ChromatogramFilterSupplierCSD supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new ChromatogramFilterSupplierCSD();
		supplier.setId("org.eclipse.chemclipse.test");
		supplier.setDescription("This is a description.");
		supplier.setFilterName("Filter Name");
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

	public void testGetFilterName_1() {

		assertEquals("Filter Name", "Filter Name", supplier.getFilterName());
	}
}
