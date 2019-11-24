/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.chromatogram;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;

import junit.framework.TestCase;

/**
 * This TestCase analyses if the class ChromatogramConverterSupport methods work
 * in a correct way.
 * 
 * @author eselmeister
 */
public class ChromatogramConverterSupport_2_Test extends TestCase {

	private ChromatogramConverterSupport support;
	private ChromatogramSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		support = new ChromatogramConverterSupport(null);
		supplier = new ChromatogramSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.agilent");
		supplier.setFileExtension(".MS");
		supplier.setDirectoryExtension(".D");
		supplier.setFilterName("Agilent Chromatogram (.D)");
		support.add(supplier);
		supplier = new ChromatogramSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.netCDF");
		supplier.setFileExtension(".netCDF");
		supplier.setDirectoryExtension("");
		supplier.setFilterName("ANDI/AIA (.netCDF)");
		support.add(supplier);
		supplier = new ChromatogramSupplier();
		supplier.setId("org.eclipse.chemclipse.xxd.converter.supplier.chemclipse");
		supplier.setFileExtension(".chrom");
		supplier.setDirectoryExtension("");
		supplier.setFilterName("ChemClipse Chromatogram (.chrom)");
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
			assertEquals("id#0", "org.eclipse.chemclipse.msd.converter.supplier.agilent", id);
			id = support.getConverterId(1);
			assertEquals("id#1", "org.eclipse.chemclipse.msd.converter.supplier.netCDF", id);
			id = support.getConverterId(2);
			assertEquals("id#2", "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse", id);
		} catch(NoConverterAvailableException e) {
			assertTrue(false);
		}
	}

	public void testGetFilterExtensions_1() {

		String[] ids;
		try {
			ids = support.getFilterExtensions();
			assertEquals("FilterExtension #0", "*.", ids[0]);
			assertEquals("FilterExtension #1", "*.netCDF;*.netcdf;*.NETCDF", ids[1]);
			assertEquals("FilterExtension #2", "*.chrom;*.CHROM", ids[2]);
		} catch(NoConverterAvailableException e) {
			assertTrue(false);
		}
	}

	public void testGetFilterNames_1() {

		String[] names;
		try {
			names = support.getFilterNames();
			assertEquals("FilterName #0", "Agilent Chromatogram (.D)", names[0]);
			assertEquals("FilterName #1", "ANDI/AIA (.netCDF)", names[1]);
			assertEquals("FilterName #2", "ChemClipse Chromatogram (.chrom)", names[2]);
		} catch(NoConverterAvailableException e) {
			assertTrue(false);
		}
	}
}
