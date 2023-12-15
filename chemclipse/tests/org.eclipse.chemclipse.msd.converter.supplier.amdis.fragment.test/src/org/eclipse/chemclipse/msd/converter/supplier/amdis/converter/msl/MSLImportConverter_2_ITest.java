/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ImportConverterMslTestCase;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class MSLImportConverter_2_ITest extends ImportConverterMslTestCase {

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_GOLMDB_TEST_MSL));
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals("MassSpectra", 3, massSpectra.size());
	}

	public void testImport_2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull(ms);
		assertEquals("M000000_A097001-101-xxx_NA_959,45_PRED_VAR5_ALK_Unknown#bth-pae-001", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "NA", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "consensus spectrum of 1 spectra per analyte, MPIMP ID and isotopomer, with majority threshold = 60%", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Relative Retention Time", 0, ms.getRelativeRetentionTime());
		assertEquals("Retention Index", 959.45f, ms.getRetentionIndex());
		assertEquals("Ion", 50, ms.getNumberOfIons());
		//
		assertEquals("BasePeak Ion", 77.0d, ms.getBasePeak());
		assertEquals("BasePeak Abundance", 1000.0f, ms.getBasePeakAbundance());
		//
		assertEquals("Lowest Ion", 70.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 10.0f, ms.getLowestIon().getAbundance());
		//
		assertEquals("Highest Ion", 231.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 5.0f, ms.getHighestIon().getAbundance());
	}

	public void testImport_3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(2);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull(ms);
		assertEquals("M000880_A098001-101-xxx_NA_986,97_TRUE_VAR5_ALK_Glycine, N,N-dimethyl- (1TMS)", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "1118-68-9", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "consensus spectrum of 1 spectra per analyte, MPIMP ID and isotopomer, with majority threshold = 60%", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Relative Retention Time", 0, ms.getRelativeRetentionTime());
		assertEquals("Retention Index", 986.97f, ms.getRetentionIndex());
		assertEquals("Ion", 52, ms.getNumberOfIons());
		//
		assertEquals("BasePeak Ion", 160.0d, ms.getBasePeak());
		assertEquals("BasePeak Abundance", 1000.0f, ms.getBasePeakAbundance());
		//
		assertEquals("Lowest Ion", 70.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 81.0f, ms.getLowestIon().getAbundance());
		//
		assertEquals("Highest Ion", 177.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 18.0f, ms.getHighestIon().getAbundance());
	}

	public void testImport_4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(3);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull(ms);
		assertEquals("M001213_A098002-101-xxx_NA_988_TRUE_VAR5_ALK_Propane-1,2-diol (2TMS)", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "17887-27-3", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "consensus spectrum of 3 spectra per analyte, MPIMP ID and isotopomer, with majority threshold = 60%", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Relative Retention Time", 0, ms.getRelativeRetentionTime());
		assertEquals("Retention Index", 988.0f, ms.getRetentionIndex());
		assertEquals("Ion", 61, ms.getNumberOfIons());
		//
		assertEquals("BasePeak Ion", 117.0d, ms.getBasePeak());
		assertEquals("BasePeak Abundance", 1000.0f, ms.getBasePeakAbundance());
		//
		assertEquals("Lowest Ion", 70.0d, ms.getLowestIon().getIon());
		assertEquals("Lowest Ion Abundance", 6.0f, ms.getLowestIon().getAbundance());
		//
		assertEquals("Highest Ion", 219.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 1.0f, ms.getHighestIon().getAbundance());
	}
}
