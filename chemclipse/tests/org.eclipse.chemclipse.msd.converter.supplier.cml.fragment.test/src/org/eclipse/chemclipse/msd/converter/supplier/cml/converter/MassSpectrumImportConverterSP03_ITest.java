/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.cml.converter;

import java.io.File;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.supplier.cml.PathResolver;
import org.eclipse.chemclipse.msd.converter.supplier.cml.TestPathHelper;
import org.eclipse.chemclipse.msd.converter.supplier.cml.model.VendorLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import junit.framework.TestCase;

public class MassSpectrumImportConverterSP03_ITest extends TestCase {

	private IScanMSD massSpectrum;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File file = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_SP03));
		DatabaseImportConverter importConverter = new DatabaseImportConverter();
		IProcessingInfo<IMassSpectra> processingInfo = importConverter.convert(file, new NullProgressMonitor());
		massSpectrum = processingInfo.getProcessingResult().getMassSpectrum(1);
	}

	@Override
	protected void tearDown() throws Exception {

		massSpectrum = null;
		super.tearDown();
	}

	@Test
	public void testLoading() {

		assertNotNull(massSpectrum);
		assertEquals("sp03", massSpectrum.getIdentifier());
	}

	@Test
	public void testLibraryInformation() {

		VendorLibraryMassSpectrum libraryMassSpectrum = (VendorLibraryMassSpectrum)massSpectrum;
		ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
		assertEquals("D.HENNEBERG, MAX-PLANCK INSTITUTE, MULHEIM, WEST GERMANY", libraryInformation.getContributor());
		assertEquals("109-99-9", libraryInformation.getCasNumber());
		assertEquals("C 4 H 8 O 1", libraryInformation.getFormula());
	}

	@Test
	public void testSignals() {

		assertEquals(32, massSpectrum.getNumberOfIons());
		assertEquals(73d, massSpectrum.getHighestIon().getIon());
	}
}
