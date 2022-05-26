/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.massspectrum;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;

import junit.framework.TestCase;

/**
 * This TestCase analyses if the class MassSpectrumConverterSupport methods work
 * in a correct way.
 * 
 * @author eselmeister
 */
public class MassSpectrumConverterSupport_2_Test extends TestCase {

	private MassSpectrumConverterSupport support;
	private MassSpectrumSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new MassSpectrumConverterSupport();
		supplier = new MassSpectrumSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.ascii");
		supplier.setFileExtension(".txt");
		supplier.setDirectoryExtension(".TXT");
		supplier.setFilterName("ASCII Text (.TXT)");
		support.add(supplier);
		supplier = new MassSpectrumSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.amdis");
		supplier.setFileExtension(".msl");
		supplier.setDirectoryExtension("");
		supplier.setFilterName("AMDIS Mass Spectra (*.msl)");
		support.add(supplier);
		supplier = new MassSpectrumSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.jcamp");
		supplier.setFileExtension(".jdx");
		supplier.setDirectoryExtension("");
		supplier.setFilterName("JCAMP-DX (*.jdx)");
		support.add(supplier);
	}

	@Override
	protected void tearDown() throws Exception {

		support = null;
		super.tearDown();
	}

	public void testGetConverterId_1() {

		try {
			@SuppressWarnings("unused")
			String id = support.getConverterId(-1);
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}

	public void testGetConverterId_2() {

		try {
			@SuppressWarnings("unused")
			String id = support.getConverterId(3);
		} catch(NoConverterAvailableException e) {
			assertTrue(true);
		}
	}

	public void testGetConverterId_3() {

		String id;
		try {
			id = support.getConverterId(0);
			assertEquals("id#0", "org.eclipse.chemclipse.msd.converter.supplier.ascii", id);
			id = support.getConverterId(1);
			assertEquals("id#1", "org.eclipse.chemclipse.msd.converter.supplier.amdis", id);
			id = support.getConverterId(2);
			assertEquals("id#2", "org.eclipse.chemclipse.msd.converter.supplier.jcamp", id);
		} catch(NoConverterAvailableException e) {
			assertTrue(false);
		}
	}

	public void testGetFilterExtensions_1() {

		String[] ids;
		try {
			ids = support.getFilterExtensions();
			assertEquals("FilterExtension #0", "*.", ids[0]); // Important ... otherwise 'Save As...' fails
			assertEquals("FilterExtension #1", "*.msl;*.MSL", ids[1]);
			assertEquals("FilterExtension #2", "*.jdx;*.JDX", ids[2]);
		} catch(NoConverterAvailableException e) {
			assertTrue(false);
		}
	}

	public void testGetFilterNames_1() {

		String[] names;
		try {
			names = support.getFilterNames();
			assertEquals("FilterName #0", "ASCII Text (.TXT)", names[0]);
			assertEquals("FilterName #1", "AMDIS Mass Spectra (*.msl)", names[1]);
			assertEquals("FilterName #2", "JCAMP-DX (*.jdx)", names[2]);
		} catch(NoConverterAvailableException e) {
			assertTrue(false);
		}
	}
}
