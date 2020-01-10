/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.NoChromatogramClassifierSupplierAvailableException;

import junit.framework.TestCase;

/**
 * @author eselmeister
 */
public class ChromatogramClassifierSupport_1_Test extends TestCase {

	private ChromatogramClassifierSupport support;
	private ChromatogramClassifierSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new ChromatogramClassifierSupport();
		supplier = new ChromatogramClassifierSupplier();
		supplier.setId("net.first.supplier");
		supplier.setDescription("Classifier Description");
		supplier.setClassifierName("Test Classifier Name");
		support.add(supplier);
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		supplier = null;
		super.tearDown();
	}

	public void testGetAvailableClassifierIds_1() {

		try {
			List<String> ids = support.getAvailableClassifierIds();
			assertEquals("getId", "net.first.supplier", ids.get(0));
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			assertTrue("NoChromatogramClassifierSupplierAvailableException", false);
		}
	}

	public void testGetIntegratorId_1() {

		try {
			String name = support.getClassifierId(0);
			assertEquals("Name", "net.first.supplier", name);
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			assertTrue("NoChromatogramClassifierSupplierAvailableException", false);
		}
	}

	public void testGetIntegratorSupplier_1() {

		IChromatogramClassifierSupplier supplier;
		try {
			supplier = support.getClassifierSupplier("net.first.supplier");
			assertNotNull(supplier);
			assertEquals("Name", "Test Classifier Name", supplier.getClassifierName());
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			assertTrue("NoChromatogramClassifierSupplierAvailableException", false);
		}
	}

	public void testGetIntegratorNames_1() {

		try {
			String[] names = support.getClassifierNames();
			assertEquals("length", 1, names.length);
			assertEquals("name", "Test Classifier Name", names[0]);
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			assertTrue("NoChromatogramClassifierSupplierAvailableException", false);
		}
	}
}
