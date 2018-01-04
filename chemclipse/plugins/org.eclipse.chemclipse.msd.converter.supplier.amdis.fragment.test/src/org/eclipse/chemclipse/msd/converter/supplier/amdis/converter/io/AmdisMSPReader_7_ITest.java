/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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

public class AmdisMSPReader_7_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_7_MSP));
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
		if(massSpectrum instanceof ILibraryMassSpectrum) {
			ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Demo1", libraryInformation.getName());
			assertEquals("", libraryInformation.getFormula());
			assertEquals(0.0d, libraryInformation.getMolWeight());
			assertEquals("", libraryInformation.getCasNumber());
			assertEquals("Comment1", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(23, massSpectrum.getNumberOfIons());
		assertEquals(36.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(488.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(131.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(1282.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(83.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(45971.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(135419.0f, massSpectrum.getTotalSignal());
	}

	public void test3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(2);
		if(massSpectrum instanceof ILibraryMassSpectrum) {
			ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Demo2", libraryInformation.getName());
			assertEquals("", libraryInformation.getFormula());
			assertEquals(0.0d, libraryInformation.getMolWeight());
			assertEquals("", libraryInformation.getCasNumber());
			assertEquals("Comment2", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(36, massSpectrum.getNumberOfIons());
		assertEquals(37.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(13815.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(114.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(1072.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(39.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(85984.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(331818.0f, massSpectrum.getTotalSignal());
	}

	public void test4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(3);
		if(massSpectrum instanceof ILibraryMassSpectrum) {
			ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Demo3", libraryInformation.getName());
			assertEquals("", libraryInformation.getFormula());
			assertEquals(0.0d, libraryInformation.getMolWeight());
			assertEquals("", libraryInformation.getCasNumber());
			assertEquals("Comment3", libraryInformation.getComments());
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(19, massSpectrum.getNumberOfIons());
		assertEquals(37.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(6154.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(93.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(5121.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(91.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(173936.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(394705.0f, massSpectrum.getTotalSignal());
	}
}
