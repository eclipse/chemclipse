/*******************************************************************************
 * Copyright (c) 2011, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.core;

import org.eclipse.chemclipse.converter.core.IConverterSupportSetter;

public class AbstractConverterSupport_2_Test extends AbstractConverterTestCase {

	private IConverterSupportSetter converterSupport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converterSupport = getConverterSupport();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testGetConverterIdByName_1() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.agilent";
		try {
			assertEquals(converterId, converterSupport.getSupplier().get(0).getId());
		} catch(Exception e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_2() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.agilent.msd1";
		try {
			assertEquals(converterId, converterSupport.getSupplier().get(1).getId());
		} catch(Exception e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_3() {

		String converterId = "net.openchrom.msd.converter.supplier.cdf";
		try {
			assertEquals(converterId, converterSupport.getSupplier().get(2).getId());
		} catch(Exception e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_4() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.excel";
		try {
			assertEquals(converterId, converterSupport.getSupplier().get(3).getId());
		} catch(Exception e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_5() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.test";
		try {
			assertEquals(converterId, converterSupport.getSupplier().get(4).getId());
		} catch(Exception e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_6() {

		try {
			@SuppressWarnings("unused")
			String converterId = converterSupport.getSupplier().get(-1).getId();
		} catch(Exception e) {
			assertTrue(true);
		}
	}

	public void testGetConverterIdByName_7() {

		try {
			@SuppressWarnings("unused")
			String converterId = converterSupport.getSupplier().get(5).getId();
		} catch(Exception e) {
			assertTrue(true);
		}
	}
}
