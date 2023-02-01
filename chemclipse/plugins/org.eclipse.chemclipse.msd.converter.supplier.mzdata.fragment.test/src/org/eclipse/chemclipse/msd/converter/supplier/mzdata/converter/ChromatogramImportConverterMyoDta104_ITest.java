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
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.converter;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.mzdata.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class ChromatogramImportConverterMyoDta104_ITest extends TestCase {

	private IVendorChromatogram chromatogram;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_MYO_DTA_104));
		ChromatogramImportConverter converter = new ChromatogramImportConverter();
		IProcessingInfo<IChromatogramMSD> processingInfo = converter.convert(importFile, new NullProgressMonitor());
		chromatogram = (VendorChromatogram)processingInfo.getProcessingResult();
	}

	@Test
	public void testSample() {

		assertEquals("Horse Myoglobin", chromatogram.getDataName());
	}

	@Test
	public void testOperator() {

		assertEquals("Randy Julian, Eli Lilly, rkj@lilly.com", chromatogram.getOperator());
	}

	@Test
	public void testInstrument() {

		assertEquals("LCQ Deca XP", chromatogram.getInstrument());
	}

	@Test
	public void testEditHistory() {

		assertEquals("deisotoped", chromatogram.getEditHistory().get(0).getDescription());
		assertEquals("chargeDeconvolved", chromatogram.getEditHistory().get(1).getDescription());
		assertEquals("peakProcessing", chromatogram.getEditHistory().get(2).getDescription());
		assertEquals("PSI-MS XCalibur RAW converter 1.04", chromatogram.getEditHistory().get(2).getEditor());
	}

	@Test
	public void testNumberOfScans() {

		assertEquals("NumberOfScans", 6, chromatogram.getNumberOfScans());
	}

	@Test
	public void testTotalSignal() {

		assertEquals("Total Signal", 4.4378344E7f, chromatogram.getTotalSignal());
	}

	@Test
	public void testMaxIonAbundance() {

		assertEquals("Max Signal", 2383616.0f, chromatogram.getMaxIonAbundance());
	}

	@Test
	public void testFirstScan() {

		IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)chromatogram.getScan(1);
		assertEquals("Ions", 331, massSpectrum.getNumberOfIons());
	}
}
