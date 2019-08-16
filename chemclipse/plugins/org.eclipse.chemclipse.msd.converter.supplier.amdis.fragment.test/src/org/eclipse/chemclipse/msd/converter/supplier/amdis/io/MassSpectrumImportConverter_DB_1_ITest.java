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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class MassSpectrumImportConverter_DB_1_ITest extends ImportConverterMslTestCase {

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_DB_1));
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals("MassSpectra", 6, massSpectra.size());
	}

	public void testImport_2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum) {
			ms = (IVendorLibraryMassSpectrum)massSpectrum;
		}
		assertNotNull("IAmdisMassSpectrum", ms);
		assertEquals("Name", "0.5203 min, OP17760", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "OP17760-N1001", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "0.5203 min, OP17760", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 31218, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 6, ms.getNumberOfIons());
		assertEquals("Lowest Ion", 16.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 13.0f, ms.getLowestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 28.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 999.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(6);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum) {
			ms = (IVendorLibraryMassSpectrum)massSpectrum;
		}
		assertNotNull("IAmdisMassSpectrum", ms);
		assertEquals("Name", "1.5763 min, OP17760", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "OP17760-N1006", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "1.5763 min, OP17760", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 94578, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 27, ms.getNumberOfIons());
		assertEquals("Lowest Ion", 15.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 29.0f, ms.getLowestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 41.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 999.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(4);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum) {
			ms = (IVendorLibraryMassSpectrum)massSpectrum;
		}
		assertNotNull("IAmdisMassSpectrum", ms);
		assertEquals("Name", "1.3982 min, OP17760", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "OP17760-N1004", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "1.3982 min, OP17760", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 83892, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 6, ms.getNumberOfIons());
		assertEquals("Lowest Ion", 16.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 20.0f, ms.getLowestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 44.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 999.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_5() {

		IScanMSD massSpectrum;
		List<Integer> numberOfIons = new ArrayList<Integer>();
		numberOfIons.add(0); // first is 0, because massSpectra starts
								// with index 1
		numberOfIons.add(6);
		numberOfIons.add(12);
		numberOfIons.add(11);
		numberOfIons.add(6);
		numberOfIons.add(10);
		numberOfIons.add(27);
		for(int i = 1; i <= massSpectra.size(); i++) {
			massSpectrum = massSpectra.getMassSpectrum(i);
			assertEquals("Ions", (int)numberOfIons.get(i), massSpectrum.getNumberOfIons());
		}
	}
}
