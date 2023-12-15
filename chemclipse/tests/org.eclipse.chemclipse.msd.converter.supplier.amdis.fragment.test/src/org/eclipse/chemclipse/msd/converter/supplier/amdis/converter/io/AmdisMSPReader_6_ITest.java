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

public class AmdisMSPReader_6_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_6_MSP));
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
			assertEquals("Demo1", libraryInformation.getName());
			assertEquals("", libraryInformation.getFormula());
			assertEquals(0.0d, libraryInformation.getMolWeight());
			assertEquals("", libraryInformation.getCasNumber());
			assertEquals("", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(73, massSpectrum.getNumberOfIons());
		assertEquals(37.1d, massSpectrum.getLowestIon().getIon());
		assertEquals(3789.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(280.15d, massSpectrum.getHighestIon().getIon());
		assertEquals(5340.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(104.05d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(427584.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(2025957.0f, massSpectrum.getTotalSignal());
	}

	public void test3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(2);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Demo2", libraryInformation.getName());
			assertEquals("", libraryInformation.getFormula());
			assertEquals(0.0d, libraryInformation.getMolWeight());
			assertEquals("", libraryInformation.getCasNumber());
			assertEquals("", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(153, massSpectrum.getNumberOfIons());
		assertEquals(15.05d, massSpectrum.getLowestIon().getIon());
		assertEquals(19421.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(313.1d, massSpectrum.getHighestIon().getIon());
		assertEquals(1958.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(308.1d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(446528.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(2272266.0f, massSpectrum.getTotalSignal());
	}

	public void test4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(3);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Demo3", libraryInformation.getName());
			assertEquals("", libraryInformation.getFormula());
			assertEquals(0.0d, libraryInformation.getMolWeight());
			assertEquals("", libraryInformation.getCasNumber());
			assertEquals("", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(150, massSpectrum.getNumberOfIons());
		assertEquals(17.05d, massSpectrum.getLowestIon().getIon());
		assertEquals(1752.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(369.15d, massSpectrum.getHighestIon().getIon());
		assertEquals(2231.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(248.15d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(479104.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1986783.0f, massSpectrum.getTotalSignal());
	}
}
