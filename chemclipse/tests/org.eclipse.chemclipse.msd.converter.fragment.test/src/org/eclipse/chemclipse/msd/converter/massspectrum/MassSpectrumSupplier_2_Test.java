/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
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

import static org.junit.Assert.assertNotEquals;

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
		assertEquals("equals", supplier, anotherSupplier);
	}

	public void testEquals_2() {

		MassSpectrumSupplier anotherSupplier = new MassSpectrumSupplier();
		anotherSupplier.setId("chemclipse");
		assertNotEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setId("");
		assertEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setDescription("chemclipse");
		assertNotEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setDescription("");
		assertEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setFilterName("chemclipse");
		assertNotEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setFilterName("");
		assertEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setFileExtension("chemclipse");
		assertNotEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setFileExtension("");
		assertEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setFileName("chemclipse");
		assertNotEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setFileName("");
		assertEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setDirectoryExtension("chemclipse");
		assertNotEquals("equals", supplier, anotherSupplier);
		anotherSupplier.setDirectoryExtension("");
		assertEquals("equals", supplier, anotherSupplier);
	}

	public void testEquals_3() {

		MassSpectrumSupplier anotherSupplier = new MassSpectrumSupplier();
		anotherSupplier.setId("chemclipse");
		assertNotEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setId("");
		assertEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setDescription("chemclipse");
		assertNotEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setDescription("");
		assertEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setFilterName("chemclipse");
		assertNotEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setFilterName("");
		assertEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setFileExtension("chemclipse");
		assertNotEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setFileExtension("");
		assertEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setFileName("chemclipse");
		assertNotEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setFileName("");
		assertEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setDirectoryExtension("chemclipse");
		assertNotEquals("equals", anotherSupplier, supplier);
		anotherSupplier.setDirectoryExtension("");
		assertEquals("equals", anotherSupplier, supplier);
	}

	public void testEquals_4() {

		MassSpectrumSupplier anotherSupplier = supplier;
		assertEquals("equals", anotherSupplier, supplier);
	}

	public void testEquals_5() {

		assertEquals("equals", supplier, supplier);
	}

	public void testEquals_6() {

		ChromatogramSupplier anotherSupplier = null;
		assertNotEquals("equals", supplier, anotherSupplier);
	}

	public void testEquals_7() {

		Object anotherSupplier = new Object();
		assertNotEquals("equals", supplier, anotherSupplier);
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
