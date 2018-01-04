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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.ChromatogramIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram.IChromatogramIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class IntegratorSupport_1_Test extends TestCase {

	private ChromatogramIntegratorSupport support;
	private ChromatogramIntegratorSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new ChromatogramIntegratorSupport();
		supplier = new ChromatogramIntegratorSupplier();
		supplier.setId("net.first.supplier");
		supplier.setDescription("Integrator Description");
		supplier.setIntegratorName("Test Integrator Name");
		support.add(supplier);
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		supplier = null;
		super.tearDown();
	}

	public void testGetAvailableIntegratorIds_1() {

		List<String> ids;
		try {
			ids = support.getAvailableIntegratorIds();
			assertEquals("getId", "net.first.supplier", ids.get(0));
		} catch(NoIntegratorAvailableException e) {
			assertTrue("NoIntegratorAvailableException", false);
		}
	}

	public void testGetIntegratorId_1() {

		try {
			String name = support.getIntegratorId(0);
			assertEquals("Name", "net.first.supplier", name);
		} catch(NoIntegratorAvailableException e) {
			assertTrue("NoIntegratorAvailableException", false);
		}
	}

	public void testGetIntegratorSupplier_1() {

		IChromatogramIntegratorSupplier supplier;
		try {
			supplier = support.getIntegratorSupplier("net.first.supplier");
			assertNotNull(supplier);
			assertEquals("Name", "Test Integrator Name", supplier.getIntegratorName());
		} catch(NoIntegratorAvailableException e) {
			assertTrue("NoIntegratorAvailableException", false);
		}
	}

	public void testGetIntegratorNames_1() {

		try {
			String[] names = support.getIntegratorNames();
			assertEquals("length", 1, names.length);
			assertEquals("name", "Test Integrator Name", names[0]);
		} catch(NoIntegratorAvailableException e) {
			assertTrue("NoIntegratorAvailableException", false);
		}
	}
}
