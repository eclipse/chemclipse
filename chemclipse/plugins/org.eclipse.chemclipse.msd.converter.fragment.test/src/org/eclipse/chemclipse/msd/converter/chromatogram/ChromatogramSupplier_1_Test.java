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
package org.eclipse.chemclipse.msd.converter.chromatogram;

import org.eclipse.chemclipse.converter.chromatogram.ChromatogramSupplier;

import junit.framework.TestCase;

/**
 * Testing all methods of ChromatogramSupplier and its specification.
 * 
 * @author eselmeister
 */
public class ChromatogramSupplier_1_Test extends TestCase {

	private ChromatogramSupplier supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		supplier = new ChromatogramSupplier();
	}

	@Override
	protected void tearDown() throws Exception {

		supplier = null;
		super.tearDown();
	}

	public void testId_1() {

		assertEquals("id", "", supplier.getId());
		supplier.setId(null);
		assertEquals("id", "", supplier.getId());
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.agilent");
		assertEquals("id", "org.eclipse.chemclipse.msd.converter.supplier.agilent", supplier.getId());
		supplier.setId("");
		assertEquals("id", "", supplier.getId());
	}

	public void testDescription_1() {

		assertEquals("desciption", "", supplier.getDescription());
		supplier.setDescription(null);
		assertEquals("desciption", "", supplier.getDescription());
		supplier.setDescription("This is the agilent data format.");
		assertEquals("desciption", "This is the agilent data format.", supplier.getDescription());
		supplier.setDescription("");
		assertEquals("desciption", "", supplier.getDescription());
	}

	public void testFilterName_1() {

		assertEquals("desciption", "", supplier.getDescription());
		supplier.setDescription(null);
		assertEquals("desciption", "", supplier.getDescription());
		supplier.setDescription("This is the agilent data format.");
		assertEquals("desciption", "This is the agilent data format.", supplier.getDescription());
		supplier.setDescription("");
		assertEquals("desciption", "", supplier.getDescription());
	}

	public void testFileExtension_1() {

		assertEquals("fileExtension", "", supplier.getFileExtension());
		supplier.setFileExtension(null);
		assertEquals("fileExtension", "", supplier.getFileExtension());
		supplier.setFileExtension("MS");
		assertEquals("fileExtension", "MS", supplier.getFileExtension());
		supplier.setFileExtension(".MS");
		assertEquals("fileExtension", ".MS", supplier.getFileExtension());
		supplier.setFileExtension("");
		assertEquals("fileExtension", "", supplier.getFileExtension());
	}

	public void testFileName_1() {

		assertEquals("fileName", "", supplier.getFileName());
		supplier.setFileName(null);
		assertEquals("fileName", "", supplier.getFileName());
		supplier.setFileName("DATA");
		assertEquals("fileName", "DATA", supplier.getFileName());
		supplier.setFileName("");
		assertEquals("fileName", "", supplier.getFileName());
	}

	public void testDirectoryExtension_1() {

		assertEquals("directoryExtension", "", supplier.getDirectoryExtension());
		supplier.setDirectoryExtension(null);
		assertEquals("directoryExtension", "", supplier.getDirectoryExtension());
		supplier.setDirectoryExtension("D");
		assertEquals("directoryExtension", ".D", supplier.getDirectoryExtension());
		supplier.setDirectoryExtension(".D");
		assertEquals("directoryExtension", ".D", supplier.getDirectoryExtension());
		supplier.setDirectoryExtension("");
		assertEquals("directoryExtension", "", supplier.getDirectoryExtension());
	}

	public void testIsImportable_1() {

		assertEquals("isImportable", false, supplier.isImportable());
		supplier.setImportable(false);
		assertEquals("isImportable", false, supplier.isImportable());
		supplier.setImportable(true);
		assertEquals("isImportable", true, supplier.isImportable());
	}

	public void testIsExportable_1() {

		assertEquals("isExportable", false, supplier.isExportable());
		supplier.setExportable(false);
		assertEquals("isExportable", false, supplier.isExportable());
		supplier.setExportable(true);
		assertEquals("isExportable", true, supplier.isExportable());
	}
}
