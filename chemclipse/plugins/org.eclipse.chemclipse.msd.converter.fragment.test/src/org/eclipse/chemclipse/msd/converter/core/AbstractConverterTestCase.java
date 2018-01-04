/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.converter.core.ISupplierSetter;

import junit.framework.TestCase;

public class AbstractConverterTestCase extends TestCase {

	private IConverterSupportSetter converterSupport;
	private ISupplierSetter supplier;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converterSupport = new DefaultConverterSupport();
		supplier = new DefaultSupplier();
		// Supplier I
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.agilent");
		supplier.setDescription("Reads Agilent Chromatograms.");
		supplier.setFilterName("Agilent Chromatogram (*.D/DATA.MS)");
		supplier.setExportable(false);
		supplier.setImportable(true);
		supplier.setDirectoryExtension(".D");
		supplier.setFileExtension(".MS");
		supplier.setFileName("DATA");
		converterSupport.add(supplier);
		// Supplier II
		supplier = new DefaultSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.agilent.msd1");
		supplier.setDescription("Reads Agilent Chromatograms (MSD1).");
		supplier.setFilterName("Agilent Chromatogram (*.D/MSD1.MS)");
		supplier.setExportable(false);
		supplier.setImportable(true);
		supplier.setDirectoryExtension(".D");
		supplier.setFileExtension(".MSD1");
		supplier.setFileName("DATA");
		converterSupport.add(supplier);
		// Supplier III
		supplier = new DefaultSupplier();
		supplier.setId("net.openchrom.msd.converter.supplier.cdf");
		supplier.setDescription("Reads an writes ANDI/AIA CDF Chromatograms.");
		supplier.setFilterName("ANDI/AIA CDF Chromatogram (*.CDF)");
		supplier.setExportable(true);
		supplier.setImportable(true);
		supplier.setDirectoryExtension("");
		supplier.setFileExtension(".CDF");
		supplier.setFileName("");
		converterSupport.add(supplier);
		// Supplier IV
		supplier = new DefaultSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.excel");
		supplier.setDescription("Exports Chromatograms to Microsoft Excel 2007.");
		supplier.setFilterName("Excel Chromatogram (*.xlsx)");
		supplier.setExportable(true);
		supplier.setImportable(false);
		supplier.setDirectoryExtension("");
		supplier.setFileExtension(".xlsx");
		supplier.setFileName("");
		converterSupport.add(supplier);
		// Supplier V
		supplier = new DefaultSupplier();
		supplier.setId("org.eclipse.chemclipse.msd.converter.supplier.test");
		supplier.setDescription("Exports Test Chromatograms to Directories.");
		supplier.setFilterName("Test Chromatogram (*.C/CHROM.MS)");
		supplier.setExportable(true);
		supplier.setImportable(true);
		supplier.setDirectoryExtension(".C");
		supplier.setFileExtension(".MS");
		supplier.setFileName("CHROM");
		converterSupport.add(supplier);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public IConverterSupportSetter getConverterSupport() {

		return converterSupport;
	}
}
