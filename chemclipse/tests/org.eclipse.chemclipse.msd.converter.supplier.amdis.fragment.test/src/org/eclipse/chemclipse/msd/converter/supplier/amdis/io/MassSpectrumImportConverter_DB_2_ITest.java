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

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.model.IVendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class MassSpectrumImportConverter_DB_2_ITest extends ImportConverterMslTestCase {

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_DB_2));
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testImport_1() {

		assertEquals("MassSpectra", 8, massSpectra.size());
	}

	public void testImport_2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum) {
			ms = (IVendorLibraryMassSpectrum)massSpectrum;
		}
		assertNotNull(ms);
		assertEquals("Name", "1,3,5-Trimethylbenzol", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "0", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "Converted", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 2, ms.getNumberOfIons());
		assertEquals("Highest Ion", 101.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 608.0f, ms.getHighestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 100.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 1000.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(6);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum) {
			ms = (IVendorLibraryMassSpectrum)massSpectrum;
		}
		assertNotNull(ms);
		assertEquals("Name", "Decahydronapthalin (cis/trans) 1. Isomer", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "0", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "Converted", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 2, ms.getNumberOfIons());
		assertEquals("Highest Ion", 101.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 608.0f, ms.getHighestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 100.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 1000.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(4);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull(ms);
		assertEquals("Name", "Isopropylmyristat (wrs.)", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "0", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "Converted", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 2, ms.getNumberOfIons());
		assertEquals("Highest Ion", 101.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 608.0f, ms.getHighestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 100.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 1000.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_5() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(7);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull(ms);
		assertEquals("Name", "unbekannt (???THF-derivate?) Oligomeren-reihe RT:2085/2550/3015/3470/3910/nach4200", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "0", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "Converted", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 2, ms.getNumberOfIons());
		assertEquals("Highest Ion", 101.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 608.0f, ms.getHighestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 100.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 1000.0f, ms.getHighestAbundance().getAbundance());
	}

	public void testImport_6() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(8);
		IVendorLibraryMassSpectrum ms = null;
		if(massSpectrum instanceof IVendorLibraryMassSpectrum vendorLibraryMassSpectrum) {
			ms = vendorLibraryMassSpectrum;
		}
		assertNotNull(ms);
		assertEquals("Name", "Abbaupeak Irgacure 819 (RT: bei C23)", ms.getLibraryInformation().getName());
		assertEquals("CAS Number", "0", ms.getLibraryInformation().getCasNumber());
		assertEquals("Comments", "Converted", ms.getLibraryInformation().getComments());
		assertEquals("Retention Time", 0, ms.getRetentionTime());
		assertEquals("Retention Index", 0.0f, ms.getRetentionIndex());
		assertEquals("Ion", 2, ms.getNumberOfIons());
		assertEquals("Highest Ion", 101.0d, ms.getHighestIon().getIon());
		assertEquals("Highest Ion Abundance", 608.0f, ms.getHighestIon().getAbundance());
		assertEquals("Highest Abundance Ion", 100.0d, ms.getHighestAbundance().getIon());
		assertEquals("Highest Abundance", 1000.0f, ms.getHighestAbundance().getAbundance());
	}
}
