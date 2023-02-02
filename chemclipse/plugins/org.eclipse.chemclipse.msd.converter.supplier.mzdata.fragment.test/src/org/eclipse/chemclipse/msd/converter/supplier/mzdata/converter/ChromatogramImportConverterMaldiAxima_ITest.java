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
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.VendorStandaloneMassSpectrum;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class ChromatogramImportConverterMaldiAxima_ITest extends TestCase {

	private IVendorStandaloneMassSpectrum standaloneMassSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_MALDI_AXIMA_CFR));
		MassSpectrumImportConverter converter = new MassSpectrumImportConverter();
		IProcessingInfo<IMassSpectra> processingInfo = converter.convert(importFile, new NullProgressMonitor());
		VendorMassSpectra massSpectra = (VendorMassSpectra)processingInfo.getProcessingResult();
		standaloneMassSpectrum = (VendorStandaloneMassSpectrum)massSpectra.getMassSpectrum(1);
	}

	@Test
	public void testOperator() {

		assertEquals("Mike Ashton, Kratos Analytical Limited", standaloneMassSpectrum.getOperator());
	}

	@Test
	public void testInstrument() {

		assertEquals("AXIMA-CFR", standaloneMassSpectrum.getInstrument());
	}

	@Test
	public void testNumberOfIons() {

		assertEquals(134, standaloneMassSpectrum.getNumberOfIons());
	}
}
