/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.io;

import java.io.File;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.MSPReader;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class AmdisMSPReader_1_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_1_MSP));
		MSPReader reader = new MSPReader();
		massSpectra = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectra = null;
		super.tearDown();
	}

	public void test1() {

		assertEquals(2, massSpectra.size());
	}

	public void test2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Test1", libraryInformation.getName());
			assertEquals("CHO", libraryInformation.getFormula());
			assertEquals(100.0d, libraryInformation.getMolWeight());
			assertEquals("44444", libraryInformation.getCasNumber());
			assertEquals("Demo1", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(37, massSpectrum.getNumberOfIons());
		assertEquals(42.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(17.98f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(108.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(244.78f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(93.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(999.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(3439.7798f, massSpectrum.getTotalSignal());
	}

	public void test3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(2);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Test2", libraryInformation.getName());
			assertEquals("CHO", libraryInformation.getFormula());
			assertEquals(100.0d, libraryInformation.getMolWeight());
			assertEquals("44444", libraryInformation.getCasNumber());
			assertEquals("Demo2", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(45, massSpectrum.getNumberOfIons());
		assertEquals(26.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(0.70f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(131.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(9.79f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(43.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(999.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1543.4396f, massSpectrum.getTotalSignal());
	}
}
