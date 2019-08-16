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
package org.eclipse.chemclipse.msd.converter.massspectrum;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;

import junit.framework.TestCase;

/**
 * Testing toString(), hashCode() and equals() of ChromatogramSupplier.
 * 
 * @author eselmeister
 */
public class MassSpectrumSupplier_2_Test extends TestCase {

	private MassSpectrumSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new MassSpectrumSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testToString_1() {

		String test = "org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumSupplier[id=,description=,filterName=,fileExtension=,fileName=,directoryExtension=,isExportable=false,isImportable=false]";
		assertEquals("toString", test, supplier.toString());
	}

	public void testEquals_1() {

		MassSpectrumSupplier anotherSupplier = new MassSpectrumSupplier();
		assertEquals("equals", true, supplier.equals(anotherSupplier));
	}

	public void testEquals_2() {

		MassSpectrumSupplier anotherSupplier = new MassSpectrumSupplier();
		anotherSupplier.setId("chemclipse");
		assertEquals("equals", false, supplier.equals(anotherSupplier));
		anotherSupplier.setId("");
		assertEquals("equals", true, supplier.equals(anotherSupplier));
		anotherSupplier.setDescription("chemclipse");
		assertEquals("equals", false, supplier.equals(anotherSupplier));
		anotherSupplier.setDescription("");
		assertEquals("equals", true, supplier.equals(anotherSupplier));
		anotherSupplier.setFilterName("chemclipse");
		assertEquals("equals", false, supplier.equals(anotherSupplier));
		anotherSupplier.setFilterName("");
		assertEquals("equals", true, supplier.equals(anotherSupplier));
		anotherSupplier.setFileExtension("chemclipse");
		assertEquals("equals", false, supplier.equals(anotherSupplier));
		anotherSupplier.setFileExtension("");
		assertEquals("equals", true, supplier.equals(anotherSupplier));
		anotherSupplier.setFileName("chemclipse");
		assertEquals("equals", false, supplier.equals(anotherSupplier));
		anotherSupplier.setFileName("");
		assertEquals("equals", true, supplier.equals(anotherSupplier));
		anotherSupplier.setDirectoryExtension("chemclipse");
		assertEquals("equals", false, supplier.equals(anotherSupplier));
		anotherSupplier.setDirectoryExtension("");
		assertEquals("equals", true, supplier.equals(anotherSupplier));
	}

	public void testEquals_3() {

		MassSpectrumSupplier anotherSupplier = new MassSpectrumSupplier();
		anotherSupplier.setId("chemclipse");
		assertEquals("equals", false, anotherSupplier.equals(supplier));
		anotherSupplier.setId("");
		assertEquals("equals", true, anotherSupplier.equals(supplier));
		anotherSupplier.setDescription("chemclipse");
		assertEquals("equals", false, anotherSupplier.equals(supplier));
		anotherSupplier.setDescription("");
		assertEquals("equals", true, anotherSupplier.equals(supplier));
		anotherSupplier.setFilterName("chemclipse");
		assertEquals("equals", false, anotherSupplier.equals(supplier));
		anotherSupplier.setFilterName("");
		assertEquals("equals", true, anotherSupplier.equals(supplier));
		anotherSupplier.setFileExtension("chemclipse");
		assertEquals("equals", false, anotherSupplier.equals(supplier));
		anotherSupplier.setFileExtension("");
		assertEquals("equals", true, anotherSupplier.equals(supplier));
		anotherSupplier.setFileName("chemclipse");
		assertEquals("equals", false, anotherSupplier.equals(supplier));
		anotherSupplier.setFileName("");
		assertEquals("equals", true, anotherSupplier.equals(supplier));
		anotherSupplier.setDirectoryExtension("chemclipse");
		assertEquals("equals", false, anotherSupplier.equals(supplier));
		anotherSupplier.setDirectoryExtension("");
		assertEquals("equals", true, anotherSupplier.equals(supplier));
	}

	public void testEquals_4() {

		MassSpectrumSupplier anotherSupplier = supplier;
		assertEquals("equals", true, anotherSupplier.equals(supplier));
	}

	public void testEquals_5() {

		assertEquals("equals", true, supplier.equals(supplier));
	}

	public void testEquals_6() {

		ChromatogramSupplier anotherSupplier = null;
		assertEquals("equals", false, supplier.equals(anotherSupplier));
	}

	public void testEquals_7() {

		Object anotherSupplier = new Object();
		assertEquals("equals", false, supplier.equals(anotherSupplier));
	}

	public void testHashCode_1() {

		ChromatogramSupplier anotherSupplier = new ChromatogramSupplier();
		assertEquals("hashCode", anotherSupplier.hashCode(), supplier.hashCode());
	}

	public void testHashCode_2() {

		ChromatogramSupplier anotherSupplier = new ChromatogramSupplier();
		anotherSupplier.setId("chemclipse");
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setId("");
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setDescription("chemclipse");
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setDescription("");
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setFilterName("chemclipse");
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setFilterName("");
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setFileExtension("chemclipse");
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setFileExtension("");
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setFileName("chemclipse");
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setFileName("");
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setDirectoryExtension("chemclipse");
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setDirectoryExtension("");
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setImportable(true);
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setImportable(false);
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setExportable(true);
		assertFalse("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
		anotherSupplier.setExportable(false);
		assertTrue("hashCode", anotherSupplier.hashCode() == supplier.hashCode());
	}
}
