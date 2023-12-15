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

public class AmdisMSPReader_2_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_2_MSP));
		MSPReader reader = new MSPReader();
		massSpectra = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectra = null;
		super.tearDown();
	}

	public void test1() {

		assertEquals(3, massSpectra.size());
	}

	public void test2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Peak1", libraryInformation.getName());
			assertEquals("CCCHHH", libraryInformation.getFormula());
			assertEquals(169.0d, libraryInformation.getMolWeight());
			assertEquals("111-22-3", libraryInformation.getCasNumber());
			assertEquals("Comment1", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(25, massSpectrum.getNumberOfIons());
		assertEquals(42.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(80.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(171.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(20.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(120.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(999.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(2364.0f, massSpectrum.getTotalSignal());
	}

	public void test3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(2);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Peak2", libraryInformation.getName());
			assertEquals("CCCNNN", libraryInformation.getFormula());
			assertEquals(203.0d, libraryInformation.getMolWeight());
			assertEquals("222-33-4", libraryInformation.getCasNumber());
			assertEquals("Comment2", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(47, massSpectrum.getNumberOfIons());
		assertEquals(27.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(220.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(203.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(10.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(154.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(999.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(4275.0f, massSpectrum.getTotalSignal());
	}

	public void test4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(3);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Peak3", libraryInformation.getName());
			assertEquals("CCCOOO", libraryInformation.getFormula());
			assertEquals(155.0d, libraryInformation.getMolWeight());
			assertEquals("333-44-5", libraryInformation.getCasNumber());
			assertEquals("Comment3", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(69, massSpectrum.getNumberOfIons());
		assertEquals(15.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(35.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(159.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(4.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(106.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(999.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(2963.0f, massSpectrum.getTotalSignal());
	}
}
