/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;

public class AbstractConverterSupport_1_Test extends AbstractConverterTestCase {

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
		String converterName = "Agilent Chromatogram (*.D/DATA.MS)";
		try {
			assertEquals(converterId, converterSupport.getConverterId(converterName, false));
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_2() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.agilent.msd1";
		String converterName = "Agilent Chromatogram (*.D/MSD1.MS)";
		try {
			assertEquals(converterId, converterSupport.getConverterId(converterName, false));
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_3() {

		String converterId = "net.openchrom.msd.converter.supplier.cdf";
		String converterName = "ANDI/AIA CDF Chromatogram (*.CDF)";
		try {
			assertEquals(converterId, converterSupport.getConverterId(converterName, false));
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_4() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.excel";
		String converterName = "Excel Chromatogram (*.xlsx)";
		try {
			assertEquals(converterId, converterSupport.getConverterId(converterName, false));
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_5() {

		String converterId = "org.eclipse.chemclipse.msd.converter.supplier.test";
		String converterName = "Test Chromatogram (*.C/CHROM.MS)";
		try {
			assertEquals(converterId, converterSupport.getConverterId(converterName, false));
		} catch(NoConverterAvailableException e) {
			assertTrue("The failure NoConverterAvailableException should not be thrown here.", false);
		}
	}

	public void testGetConverterIdByName_6() {

		String converterName = "org.eclipse.chemclipse.msd.converter";
		try {
			converterSupport.getConverterId(converterName, false);
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}

	public void testGetConverterIdByName_7() {

		String converterName = "";
		try {
			converterSupport.getConverterId(converterName, false);
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}
}
