/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.util.Set;

import org.eclipse.chemclipse.msd.converter.supplier.amdis.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class MSPReader_1_ITest extends TestCase {

	private MSPReader reader;
	private File file;
	private IMassSpectra massSpectra;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		reader = new MSPReader();
		String pathname = TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_SYNONYMS);
		file = new File(pathname);
		massSpectra = reader.read(file, new NullProgressMonitor());
	}

	@Override
	protected void tearDown() throws Exception {

		reader = null;
		super.tearDown();
	}

	public void testRead_1() {

		assertNotNull(massSpectra);
	}

	public void testRead_2() {

		assertEquals(1, massSpectra.size());
	}

	public void testRead_3() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		assertNotNull(massSpectrum);
	}

	public void testRead_4() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		assertTrue(massSpectrum instanceof ILibraryMassSpectrum);
	}

	public void testRead_5() {

		IScanMSD massSpectrum = massSpectra.getMassSpectrum(1);
		if(massSpectrum instanceof ILibraryMassSpectrum libraryMassSpectrum) {
			Set<String> synonyms = libraryMassSpectrum.getLibraryInformation().getSynonyms();
			assertEquals(6, synonyms.size());
			assertTrue(synonyms.contains("test1"));
			assertTrue(synonyms.contains("test2"));
			assertTrue(synonyms.contains("test4"));
			assertTrue(synonyms.contains("test6"));
			assertTrue(synonyms.contains("UN 500"));
			assertTrue(synonyms.contains("UN 600"));
		}
	}
}
