/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class MassSpectrumImportConverter_CHROMATOGRAM_1_ITest extends ImportConverterMslTestCase {

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_CHROMATOGRAM));
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals("MassSpectra", 279, massSpectra.size());
	}

	public void testImport_2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(20);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull("IAmdisMassSpectrum", ms);
		assertEquals("Name", "11.4966 min, OP17760", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "OP17760-N1020", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "11.4966 min, OP17760", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 689796, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 45, ms.getNumberOfIons());
		assertEquals("Lowest Ion", 26.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 12.0f, ms.getLowestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 41.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 999.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(132);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull("IAmdisMassSpectrum", ms);
		assertEquals("Name", "29.2365 min, OP17760", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "OP17760-N1132", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "29.2365 min, OP17760", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 1754190, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 94, ms.getNumberOfIons());
		assertEquals("Lowest Ion", 17.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 7.0f, ms.getLowestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 67.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 999.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(279);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull("IAmdisMassSpectrum", ms);
		assertEquals("Name", "60.7763 min, OP17760", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "OP17760-N1279", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "60.7763 min, OP17760", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 3646578, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 121, ms.getNumberOfIons());
		assertEquals("Lowest Ion", 29.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 72.0f, ms.getLowestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 67.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 999.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_5() {

		IScanMSD massSpectrum;
		List<Integer> numberOfIons = new ArrayList<>();
		numberOfIons.add(0); // first is 0, because massSpectra starts
								// with index 1
		numberOfIons.add(6);
		numberOfIons.add(12);
		numberOfIons.add(11);
		numberOfIons.add(6);
		numberOfIons.add(10);
		numberOfIons.add(27);
		numberOfIons.add(28);
		numberOfIons.add(27);
		numberOfIons.add(30);
		numberOfIons.add(39);
		numberOfIons.add(30);
		numberOfIons.add(37);
		numberOfIons.add(33);
		numberOfIons.add(48);
		numberOfIons.add(31);
		for(int i = 1; i <= 15; i++) {
			massSpectrum = massSpectra.getMassSpectrum(i);
			assertEquals("Ions", (int)numberOfIons.get(i), massSpectrum.getNumberOfIons());
		}
	}
}
