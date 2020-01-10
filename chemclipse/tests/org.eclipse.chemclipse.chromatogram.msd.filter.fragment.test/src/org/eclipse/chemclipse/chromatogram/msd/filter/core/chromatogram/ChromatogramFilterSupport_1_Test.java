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
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class ChromatogramFilterSupport_1_Test extends TestCase {

	private ChromatogramFilterSupportMSD support;
	private ChromatogramFilterSupplierMSD supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new ChromatogramFilterSupportMSD();
		supplier = new ChromatogramFilterSupplierMSD();
		supplier.setId("net.first.supplier");
		supplier.setDescription("Filter Description");
		supplier.setFilterName("Test Filter Name");
		support.add(supplier);
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		supplier = null;
		super.tearDown();
	}

	public void testGetAvailableFilterIds_1() {

		try {
			List<String> ids = support.getAvailableFilterIds();
			assertEquals("getId", "net.first.supplier", ids.get(0));
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			assertTrue("NoChromatogramFilterSupplierAvailableException", false);
		}
	}

	public void testGetIntegratorId_1() {

		try {
			String name = support.getFilterId(0);
			assertEquals("Name", "net.first.supplier", name);
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			assertTrue("NoChromatogramFilterSupplierAvailableException", false);
		}
	}

	public void testGetIntegratorSupplier_1() {

		IChromatogramFilterSupplierMSD supplier;
		try {
			supplier = support.getFilterSupplier("net.first.supplier");
			assertNotNull(supplier);
			assertEquals("Name", "Test Filter Name", supplier.getFilterName());
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			assertTrue("NoChromatogramFilterSupplierAvailableException", false);
		}
	}

	public void testGetIntegratorNames_1() {

		try {
			String[] names = support.getFilterNames();
			assertEquals("length", 1, names.length);
			assertEquals("name", "Test Filter Name", names[0]);
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			assertTrue("NoChromatogramFilterSupplierAvailableException", false);
		}
	}
}
