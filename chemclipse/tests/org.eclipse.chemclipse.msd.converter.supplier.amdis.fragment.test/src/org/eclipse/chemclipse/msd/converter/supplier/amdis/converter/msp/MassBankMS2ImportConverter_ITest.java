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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msp;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.ImportConverterMspTestCase;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.Polarity;
import org.junit.Test;

public class MassBankMS2ImportConverter_ITest extends ImportConverterMspTestCase {

	@Override
	protected void setUp() throws Exception {

		importFile = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_MASSBANK_TEST_MSP));
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	@Test
	public void testMassSpectra() {

		assertNotNull(massSpectra);
		assertEquals(1, massSpectra.size());
	}

	@Test
	public void testMassSpectrum() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		assertNotNull(massSpectrum);
		if(massSpectrum instanceof IRegularLibraryMassSpectrum regularLibraryMassSpectrum) {
			assertEquals((short)2, regularLibraryMassSpectrum.getMassSpectrometer());
			assertEquals(288.1225d, regularLibraryMassSpectrum.getPrecursorIon());
			assertEquals(287.11575d, regularLibraryMassSpectrum.getNeutralMass());
			assertEquals("30(NCE)", regularLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_COLLISION_ENERGY));
			assertEquals(Polarity.POSITIVE, regularLibraryMassSpectrum.getPolarity());
			assertEquals("[M+H]+", regularLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_PRECURSOR_TYPE));
			assertEquals("Q-Exactive Orbitrap Thermo Scientific", regularLibraryMassSpectrum.getProperty(IRegularLibraryMassSpectrum.PROPERTY_INSTRUMENT_NAME));
		}
	}
}
