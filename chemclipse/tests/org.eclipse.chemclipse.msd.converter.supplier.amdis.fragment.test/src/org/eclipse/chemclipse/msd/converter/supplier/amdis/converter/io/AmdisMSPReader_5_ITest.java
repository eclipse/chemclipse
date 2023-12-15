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
import java.util.Set;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.MSPReader;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class AmdisMSPReader_5_ITest extends TestCase {

	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_PEAKS_5_MSP));
		MSPReader reader = new MSPReader();
		massSpectra = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectra = null;
		super.tearDown();
	}

	public void test1() {

		assertEquals(1, massSpectra.size());
	}

	public void test2() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			assertEquals("Demo1", libraryInformation.getName());
			assertEquals("N2", libraryInformation.getFormula());
			assertEquals(100.0d, libraryInformation.getMolWeight());
			assertEquals("222-111-3333", libraryInformation.getCasNumber());
			assertEquals("Comment1", libraryInformation.getComments());
			Set<String> synonyms = libraryInformation.getSynonyms();
			assertEquals(3, synonyms.size());
			assertTrue(synonyms.contains("Synonym1"));
			assertTrue(synonyms.contains("Synonym2"));
			assertTrue(synonyms.contains("Synonym3"));
		} else {
			/*
			 * It must be a library mass spectrum.
			 */
			assertTrue(false);
		}
		assertEquals(2, massSpectrum.getNumberOfIons());
		assertEquals(1.0d, massSpectrum.getLowestIon().getIon());
		assertEquals(21.0f, massSpectrum.getLowestIon().getAbundance());
		assertEquals(2.0d, massSpectrum.getHighestIon().getIon());
		assertEquals(999.0f, massSpectrum.getHighestIon().getAbundance());
		assertEquals(2.0d, massSpectrum.getHighestAbundance().getIon());
		assertEquals(999.0f, massSpectrum.getHighestAbundance().getAbundance());
		assertEquals(0, massSpectrum.getRetentionTime());
		assertEquals(0.0f, massSpectrum.getRetentionIndex());
		assertEquals(1020.0f, massSpectrum.getTotalSignal());
	}
}
