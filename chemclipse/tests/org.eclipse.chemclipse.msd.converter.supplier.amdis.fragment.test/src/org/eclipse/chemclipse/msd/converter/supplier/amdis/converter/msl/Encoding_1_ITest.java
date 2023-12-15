/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ImportConverterMslTestCase;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class Encoding_1_ITest extends ImportConverterMslTestCase {

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_ENCODING_MSL));
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals("MassSpectra", 2, massSpectra.size());
	}

	public void testImport_2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		IVendorLibraryMassSpectrum libraryMassSpectrum = (massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) ? vendorLibraryMassSpectrum : null;
		assertNotNull(libraryMassSpectrum);
		assertEquals("Propylphenyl acetate te te", libraryMassSpectrum.getLibraryInformation().getName());
	}

	public void testImport_3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(2);
		IVendorLibraryMassSpectrum libraryMassSpectrum = (massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) ? vendorLibraryMassSpectrum : null;
		assertNotNull(libraryMassSpectrum);
		assertEquals("Lauryl acetate etate te te", libraryMassSpectrum.getLibraryInformation().getName());
	}
}