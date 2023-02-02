/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.converter;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.mzxml.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class ChromatogramImportConverterTinyMzXML20_ITest extends TestCase {

	private IVendorChromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_TINY1_MZXML20));
		ChromatogramImportConverter converter = new ChromatogramImportConverter();
		IProcessingInfo<IChromatogramMSD> processingInfo = converter.convert(importFile, new NullProgressMonitor());
		chromatogram = (VendorChromatogram)processingInfo.getProcessingResult();
	}

	@Test
	public void testInstrument() {

		assertEquals("ThermoFinnigan LCQ Deca", chromatogram.getInstrument());
		assertEquals("ESI", chromatogram.getIonisation());
		assertEquals("Ion Trap", chromatogram.getMassAnalyzer());
		assertEquals("EMT", chromatogram.getMassDetector());
		assertEquals("Xcalibur 1.3 alpha 8", chromatogram.getSoftware());
	}

	@Test
	public void testEditHistory() {

		assertEquals("conversion", chromatogram.getEditHistory().get(0).getDescription());
		assertEquals("Thermo2mzXML 1", chromatogram.getEditHistory().get(0).getEditor());
	}

	@Test
	public void testNumberOfScans() {

		assertEquals("NumberOfScans", 1, chromatogram.getNumberOfScans());
	}

	@Test
	public void testTotalSignal() {

		assertEquals("Total Signal", 1.66755259E7f, chromatogram.getTotalSignal());
	}

	@Test
	public void testMaxIonAbundance() {

		assertEquals("Max Signal", 120053.0f, chromatogram.getMaxIonAbundance());
	}

	@Test
	public void testFirstScan() {

		IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)chromatogram.getScan(1);
		assertEquals("Ions", 1313, massSpectrum.getNumberOfIons());
	}
}
